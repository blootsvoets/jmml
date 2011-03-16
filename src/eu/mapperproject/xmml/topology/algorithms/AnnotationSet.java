package eu.mapperproject.xmml.topology.algorithms;

import cern.colt.list.IntArrayList;
import eu.mapperproject.xmml.definitions.Submodel.SEL;
import eu.mapperproject.xmml.topology.Instance;

/**
 * Maintain a set of annotations, for instances, iterations and operators
 * of a processiteration.
 * 
 * @author Joris Borgdorff
 */
class AnnotationSet {
	private int cInst, cIter;
	private SEL cOper;
	private Trace anInst, anIter, anOper;
	private int instNum;

	/** Create a new empty annotationset */
	AnnotationSet() {
		this(0, new Trace(), 0, new Trace(), SEL.finit, new Trace());
	}

	/** Create a copy of given annotationset */
	AnnotationSet(AnnotationSet old) {
		this(old.cInst, old.anInst, old.cIter, old.anIter, old.cOper, old.anOper);
		this.instNum = old.instNum;
	}

	/** Create an annotationset consisting of given annotations */
	private AnnotationSet(int cinst, Trace inst, int citer, Trace iter, SEL coper, Trace oper) {
		this.cIter = citer;
		this.cInst = cinst;
		this.cOper = coper;
		this.anInst = inst;
		this.anIter = iter;
		this.anOper = oper;
	}

	/** Current operator. */
	SEL getOperator() {
		return cOper;
	}

	/** Get the value of the current iteration */
	int getIteration() {
		return cIter;
	}

	/** Get the value of the current iteration */
	int getInstance() {
		return cInst;
	}

	/** Set the operater */
	void setOperater(SEL op) {
		cOper = op;
		anOper.put(instNum, op.ordinal());
	}

	/** Go to the next value of the requested annotation */
	void nextInstance() {
		cInst = anInst.nextInt(instNum);
	}

	/** Go to the next value of the requested annotation */
	void nextIteration() {
		cIter = anIter.nextInt(instNum);
	}

	/** Go to the next value of the requested annotation */
	void nextOperator() {
		cOper = SEL.get(anOper.nextInt(instNum));
	}

	/** Reset the value of the requested annotation */
	void resetIteration() {
		anIter.put(instNum, 0);
		cIter = 0;
	}

	/** Reset the value of the requested annotation */
	void resetOperator() {
		anOper.put(instNum, 0);
		cOper = SEL.finit;
	}

	/** Let the current instance be the subject of the annotation set */
	void setSubject(Instance inst) {
		instNum = inst.getNumber();
		
		anInst = new Trace(anInst);
		anIter = new Trace(anIter);
		anOper = new Trace(anOper);

		cInst = traceCurrent(anInst);
		cIter = traceCurrent(anIter);
		cOper = SEL.get(traceCurrent(anOper));
	}

	private int traceCurrent(Trace t) {
		if (t.isInstantiated(instNum)) {
			return t.currentInt(instNum);
		}
		else {
			t.put(instNum, 0);
			return 0;
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
		return (this.cInst == other.cInst)
			&& (this.cIter == other.cIter)
			&& (this.cOper == other.cOper);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 23 * hash + cInst;
		hash = 23 * hash + cIter;
		hash = 23 * hash + cOper.ordinal();
		return hash;
	}

	@Override
	public String toString() {
		return "[" + anInst + ", " + anIter + ", " + anOper + "]";
	}

	/** Remove the traces from the AnnotationSet */
	public void freeTraces() {
		this.anInst = null;
		this.anIter = null;
		this.anOper = null;
	}

	/** Append a string of this counter to a StringBuilder.
	 * @param sb StringBuilder to append to.
	 * @param completely if true, add the entire counter, otherwise just the instance counter
	 */
	public void appendToStringBuilder(StringBuilder sb, boolean completely) {
		if (cInst > 0) {
			sb.append(cInst);
		}
		if (!completely) return;
		
		sb.append('(');
		sb.append(cIter); sb.append(','); sb.append(cOper);
		sb.append(')');
	}
}
