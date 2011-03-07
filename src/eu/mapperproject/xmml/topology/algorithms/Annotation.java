package eu.mapperproject.xmml.topology.algorithms;

/** An Annotation keeps a counter of an object and last known counters of peer objects */
public class Annotation<T> {
	protected final Trace<T> t;
	protected final AnnotationType type;
	protected final int counter;

	/** Type of annotation */
	public enum AnnotationType {
		INSTANCE, ITERATION, OPERATOR;
	}

	/** Create an empty Annotation of the given type */
	public Annotation(AnnotationType tp) {
		this(tp, new Trace<T>(), 0);
	}

	/** Create an Annotation with a counter of the given object, but without any information on peer objects */
	public Annotation(AnnotationType tp, T pd, int counter) {
		this.counter = counter;
		this.type = tp;

		this.t = new Trace<T>();
		this.t.put(pd, counter);
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
	public Annotation<T> set(T pd, int c) {
		Trace<T> trace = new Trace<T>(this.t);
		trace.put(pd, c);
		return new Annotation<T>(this.type, trace, c);
	}

	/** Create a new Annotation with as counter the next iteration of the given peer object */
	public Annotation<T> next(T pd) {
		Trace<T> trace = new Trace<T>(this.t);
		int c = trace.nextInt(pd);
		return new Annotation<T>(this.type, trace, c);
	}

	/** Create a new Annotation with as counter the current iteration of the given peer object */
	public Annotation<T> current(T pd) {
		Trace<T> trace = new Trace<T>(this.t);
		int c = 0;

		if (trace.isInstantiated(pd)) {
			c = trace.currentInt(pd);
		}
		
		return new Annotation<T>(this.type, trace, c);
	}

	/** Create a new Annotation with as counter the current iteration of the given peer object */
	public Annotation<T> reset(T pd) {
		Trace<T> trace = new Trace<T>(this.t);
		int c = 0;
		trace.reset(pd);		
		return new Annotation<T>(this.type, trace, c);
	}
	
	/** Create a new Annotation with as counter the previous iteration of the given peer object */
	public Annotation<T> previous(T pd) {
		Trace<T> trace = new Trace<T>(this.t);
		int c = trace.previousInt(pd);
		return new Annotation<T>(this.type, trace, c);
	}

	/** Get the current counter value of this annotation */
	public int getCounter() {
		return this.counter;
	}

	/** Add all peer information of the given annotation to the current one. */
	public void merge(Annotation<T> an) {
		this.t.merge(an.t);
	}

	/** Get the string value of the counter */
	public String counterString() {
		return String.valueOf(this.counter);
	}

	/** Get the type of annotation */
	public AnnotationType getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return "Annotation<" + this.type + ">(" + this.counter + "," + this.t + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !this.getClass().equals(o.getClass()))	return false;
		Annotation<?> an = (Annotation<?>) o;
		return this.counter == an.counter && this.type.equals(an.type);
	}
	
	@Override
	public int hashCode() {
		return 31*counter + this.type.hashCode();
	}
}
