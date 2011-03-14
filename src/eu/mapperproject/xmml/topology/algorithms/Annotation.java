package eu.mapperproject.xmml.topology.algorithms;

import eu.mapperproject.xmml.util.Numbered;
import java.util.Collection;
import java.util.List;

/** An Annotation keeps a counter of an object and last known counters of peer objects.
 *
 * Besides the merge operation, all operations are immutable.
 *
 * @author Joris Borgdorff
 */
public class Annotation<T extends Numbered> {
	protected final Trace<T> t;
	protected final AnnotationType type;
	protected int counter;

	/** Type of annotation */
	public enum AnnotationType {
		INSTANCE, ITERATION, OPERATOR;
	}

	/** Create an empty Annotation of the given type */
	public Annotation(AnnotationType tp) {
		this(tp, new Trace<T>(), 0);
	}

	/** Create an Annotation with a count and information on peer objects */
	private Annotation(AnnotationType tp, Trace<T> trace, int counter) {
		this.t = trace;
		this.counter = counter;
		this.type = tp;
	}

	/** Set the subject of the annotation to the given object, and add the counter to the trace */
	public void setSubject(T pd) {
		this.t.put(pd, this.counter);
	}

	/** Set last-known value of the counter of a peer object to a certain value */
	public void set(T pd, int c) {
		this.t.put(pd, c);
		this.counter = c;
	}

	/** Create a new Annotation with as counter the next iteration of the given peer object */
	public void next(T pd) {
		this.counter = this.t.nextInt(pd);
	}

	/** Create a new Annotation with as counter the current iteration of the given peer object */
	public void current(T pd) {
		this.counter = 0;

		if (this.t.isInstantiated(pd)) {
			this.counter = this.t.currentInt(pd);
		}
	}
	
	/** Create a new Annotation with as counter the current iteration of the given peer object */
	public void reset(T pd) {
		this.counter = 0;
		this.t.reset(pd);
	}
	
	/** Create a new Annotation with as counter the previous iteration of the given peer object */
	public void previous(T pd) {
		this.counter = this.t.previousInt(pd);
	}

	/** Get the current counter value of this annotation */
	public int getCounter() {
		return this.counter;
	}

	/** Add all peer information of the given annotation to the current one.
	 * Returns a collection of objects that actually override the previous trace information */
	public List<List<T>> merge(Annotation<T> an, boolean track) {
		return this.t.merge(an.t, track);
	}

	/** Add selected information of the given annotation to the current one.
	 * Returns a collection of objects that actually override the previous trace information */
	public void override(Annotation<T> an, List<T> col) {
		this.t.override(an.t, col);
	}

	/** Add selected information of the given annotation to the current one.
	 * Returns a collection of objects that actually override the previous trace information */
	public void merge(Annotation<T> an, List<T> col) {
		this.t.merge(an.t, col);
	}

	/** Get the string value of the counter */
	public String counterString() {
		return String.valueOf(this.counter);
	}

	/** Get the type of annotation */
	public AnnotationType getType() {
		return this.type;
	}
	
	/** Get a copy of the current annotation */
	public Annotation<T> copy() {
		return new Annotation<T>(this.type, new Trace<T>(this.t), this.counter);
	}

	@Override
	public String toString() {
		return "Annotation<" + this.type + ">(" + this.counter + "," + this.t + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())	return false;
		Annotation<?> an = (Annotation<?>) o;
		return this.counter == an.counter && this.type.equals(an.type);
	}
	
	@Override
	public int hashCode() {
		return 31*counter + this.type.hashCode();
	}
}
