package eu.mapperproject.xmml.topology.algorithms;

public class Annotation<T> {
	protected final Trace<T> t;
	protected final AnnotationType type;
	protected int counter;

	public enum AnnotationType {
		INSTANCE, ITERATION, OPERATOR;
	}

	/** Makes a copy of the current annotation with a copy of the trace object */
	public Annotation(Annotation<T> an) {
		this(an.type, new Trace<T>(an.t), an.counter);
	}
	
	public Annotation(AnnotationType tp) {
		this(tp, new Trace<T>(), 0);
	}

	public Annotation(AnnotationType tp, T pd, int counter) {
		this.counter = counter;
		this.type = tp;

		this.t = new Trace<T>();
		this.t.put(pd, counter);
	}
	
	protected Annotation(AnnotationType tp, Trace<T> trace, int counter) {
		this.t = trace;
		this.counter = counter;
		this.type = tp;
	}

	public void add(T pd) {
		this.t.put(pd, this.counter);
	}

	public Annotation<T> set(T pd, int c) {
		Annotation<T> ret = this.shallowCopy();

		ret.t.put(pd, c);
		ret.counter = c;
		
		return ret;
	}

	public Annotation<T> next(T pd) {
		Annotation<T> ret = this.shallowCopy();

		ret.counter = ret.t.nextInt(pd);

		return ret;
	}

	public Annotation<T> current(T pd) {
		Annotation<T> ret = this.shallowCopy();

		if (ret.t.isInstantiated(pd)) {
			ret.counter = ret.t.currentInt(pd);
		} else {
			ret.counter = 0;
		}

		return ret;
	}

	public Annotation<T> reset(T pd) {
		Annotation<T> ret = this.shallowCopy();

		ret.t.reset(pd);
		ret.counter = 0;

		return ret;
	}
	
	public Annotation<T> previous(T pd) {
		Annotation<T> ret = new Annotation<T>(this);
		
		ret.counter = ret.t.previousInt(pd);
		
		return ret;
	}

	public int getCounter() {
		return this.counter;
	}

	public void put(Annotation<T> an) {
		this.merge(an);
		this.counter = an.counter;
	}

	public void merge(Annotation<T> an) {
		this.t.merge(an.t);
	}

	public String toString() {
		return "Annotation<" + this.type + ">(" + this.counter + "," + this.t + ")";
	}

	public String counterString() {
		return String.valueOf(this.counter);
	}

	/** Makes a copy of the current annotation with the same trace object */
	protected Annotation<T> shallowCopy() {
		return new Annotation<T>(this); //.type, this.t, this.counter);
	}

	public AnnotationType getType() {
		return this.type;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !this.getClass().equals(o.getClass()))	return false;
		Annotation<?> an = (Annotation<?>) o;
		return this.counter == an.counter && this.type.equals(an.type)
				&& this.t.equals(an.t);
	}
	
	@Override
	public int hashCode() {
		return 31*counter + this.type.hashCode();
	}
}
