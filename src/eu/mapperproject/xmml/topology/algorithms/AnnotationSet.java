package eu.mapperproject.xmml.topology.algorithms;

import cern.colt.list.IntArrayList;
import eu.mapperproject.xmml.definitions.Submodel.SEL;
import eu.mapperproject.xmml.topology.Instance;
import eu.mapperproject.xmml.topology.algorithms.Annotation.AnnotationType;

/**
 * Maintain a set of annotations, for instances, iterations and operators
 * of a processiteration.
 * 
 * @author Joris Borgdorff
 */
class AnnotationSet {
	private Annotation<Instance> anInst, anIter, anOper;
	private Instance inst;

	/** Create a new empty annotationset */
	AnnotationSet() {
		this(new Annotation<Instance>(AnnotationType.INSTANCE), new Annotation<Instance>(AnnotationType.ITERATION), new Annotation<Instance>(AnnotationType.OPERATOR));
	}

	/** Create a copy of given annotationset */
	AnnotationSet(AnnotationSet old) {
		this(old.anInst, old.anIter, old.anOper);
		this.inst = old.inst;
	}

	/** Create an annotationset consisting of given annotations */
	AnnotationSet(Annotation<Instance> inst, Annotation<Instance> iter, Annotation<Instance> oper) {
		if (!iter.getType().equals(AnnotationType.ITERATION) || !inst.getType().equals(AnnotationType.INSTANCE) || !oper.getType().equals(AnnotationType.OPERATOR)) {
			throw new IllegalArgumentException("AnnotationSet initialized with wrong Annotation types");
		}

		this.anInst = inst;
		this.anIter = iter;
		this.anOper = oper;
	}

	/** Current operator. */
	SEL getOperator() {
		return SEL.get(this.anOper.getCounter());
	}

	/** Get the value of the current iteration */
	int getIteration() {
		return this.anIter.getCounter();
	}

	/** Get the value of the current iteration */
	int getInstance() {
		return this.anInst.getCounter();
	}

	/** Set the operater */
	void setOperater(SEL op) {
		this.anOper.set(this.inst, op.ordinal());
	}

	/** Go to the next value of the requested annotation */
	void next(AnnotationType at) {
		Annotation<Instance> an = this.getAnnotation(at);
		an.next(inst);
	}

	/** Reset the value of the requested annotation */
	void reset(AnnotationType at) {
		Annotation<Instance> an = this.getAnnotation(at);
		an.reset(inst);
	}

	/** Get the annotation of given annotationtype */
	private Annotation<Instance> getAnnotation(AnnotationType at) {
		switch (at) {
			case ITERATION:
				return this.anIter;
			case INSTANCE:
				return this.anInst;
			case OPERATOR:
				return this.anOper;
			default:
				throw new IllegalArgumentException("AnnotationSet does not recognize AnnotationType " + at);
		}
	}

	/** Update the annotation and counter of the annotation */
	private void updateCounter(Annotation<Instance> an) {
		switch (an.getType()) {
			case ITERATION:
				this.anIter = an;
				break;
			case INSTANCE:
				this.anInst = an;
				break;
			case OPERATOR:
				this.anOper = an;
				break;
		}
	}

	/** Let the current instance be the subject of the annotation set */
	void setSubject(Instance inst) {
		for (AnnotationType at : AnnotationType.values()) {
			Annotation<Instance> cur = this.getAnnotation(at).copy();
			cur.current(inst);
			this.updateCounter(cur);
		}
		this.inst = inst;
	}

	/** Apply the current counter to the trace of each annotation */
	public void applySubject() {
		for (AnnotationType at : AnnotationType.values()) {
			this.getAnnotation(at).setSubject(this.inst);
		}
	}

	/** Merge the given set with the current one */
	public void merge(AnnotationSet set) {
		this.anInst.merge(set.anInst);
		IntArrayList[] col = this.anIter.merge(set.anIter);
		// Merge if the iteration was equal, override if it was larger
		this.anOper.merge(set.anOper, col[0]);
		this.anOper.override(set.anOper, col[1]);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) return false;
		final AnnotationSet other = (AnnotationSet) obj;
		return (this.anInst.getCounter() == other.anInst.getCounter())
			&& (this.anIter.getCounter() == other.anIter.getCounter())
			&& (this.anOper.getCounter() == other.anOper.getCounter());
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 23 * hash + this.anInst.getCounter();
		hash = 23 * hash + this.anIter.getCounter();
		hash = 23 * hash + this.anOper.getCounter();
		return hash;
	}

	@Override
	public String toString() {
		return "[" + this.anInst + ", " + this.anIter + ", " + this.anOper + "]";
	}
}
