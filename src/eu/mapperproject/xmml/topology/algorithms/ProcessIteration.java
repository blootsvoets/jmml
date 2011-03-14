package eu.mapperproject.xmml.topology.algorithms;

import eu.mapperproject.xmml.definitions.Submodel.SEL;
import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.topology.Instance;
import eu.mapperproject.xmml.topology.algorithms.Annotation.AnnotationType;
import java.util.List;

public class ProcessIteration {

	private static final ProcessIterationCache cache = new ProcessIterationCache();
	private final AnnotationSet givenAnnot, annot;
	private final Instance instance;
	// As equals is the most costly operation in processiteration
	// this cache was added
	private String asString;
	private final String origString;
	private boolean initial, isfinal, deadlock;
	private boolean stateFinished, initFinished;
	private SEL rangeFromOper, rangeToOper;
	private int rangeFromIter, rangeToIter;

	public enum ProgressType {
		INSTANCE, ITERATION;
	}
	
	public ProcessIteration(Instance pd) {
		this(pd, null);
	}
	
	ProcessIteration(Instance pd, AnnotationSet annot) {
		this.instance = pd;
		if (annot == null) {
			annot = new AnnotationSet();
			annot.setSubject(pd);
			annot.applySubject();
		}
		this.annot = annot;
		this.givenAnnot = new AnnotationSet(this.annot);
		String counter = this.givenAnnot.counterString();
		this.asString = this.instance.getId() + counter;
		this.origString = this.instance.getNumber() + counter;
		this.isfinal = false;
		this.stateFinished = false;
		this.initFinished = false;
		this.deadlock = false;
		this.initial = this.instance.isInitial() && firstInstance() && initializing();
		this.rangeFromIter = this.rangeToIter = this.getIteration();
		this.rangeFromOper = this.rangeToOper = this.getOperator();
	}
	
	public boolean isFinal() {
		return this.isfinal;
	}
	
	public boolean isInitial() {
		return this.initial;
	}

	public boolean hasDeadlock() {
		return this.deadlock;
	}
	
	public void setFinal() {
		this.isfinal = true;
	}

	public void setInitial() {
		this.initial = true;
	}

	public void setDeadlock() {
		this.deadlock = true;
	}

	final int getIteration() {
		return this.givenAnnot.getIteration();
	}
	
	public boolean instanceCompleted() {
		return this.annot.operatorEq(SEL.Of);
	}
	
	public boolean finalLoop() {
		return this.instance.isCompleted(annot.getIteration()) && !this.annot.operatorEq(SEL.finit);
	}
	
	public Instance getInstance() {
		return this.instance;
	}

	public int getInstanceCounter() {
		return this.annot.getInstance();
	}

	public final boolean firstInstance() {
		return this.annot.getInstance() == 0;
	}

	public boolean firstLoop() {
		return this.annot.getIteration() == 0 && this.annot.operatorLE(SEL.S);
	}
	
	public final boolean initializing() {
		return this.annot.operatorEq(SEL.finit);
	}
	
	public boolean needsState() {
		return initializing() && !firstInstance() && instance.getSubmodel().isStateful();
	}
	
	public SEL receivingType() {
		SEL op = this.givenAnnot.getOperator();
		if (op.isReceiving()) return op;
		else return null;
	}

	public final SEL getOperator() {
		return this.givenAnnot.getOperator();
	}
	
	public ProcessIteration nextStep() {
		return this.progress(ProgressType.ITERATION);
	}
	
	public ProcessIteration nextState() {
		return this.progress(ProgressType.INSTANCE);
	}

	/**
	 * Merge an annotationset with the current annotation sets
	 */
	void merge(AnnotationSet key) {
		if (this.initFinished) {
			throw new IllegalStateException("Can not add more initialization information after processiteration has sent information.");
		}

		this.annot.merge(key);
		this.givenAnnot.merge(key);
	}

	
	public ProcessIteration nextIteration(Coupling pd) {
		if (this.instance.equals(pd.getTo())) {
			throw new IllegalArgumentException("In a progression that is an internal iteration, an coupling may not be specified.");
		}
		return this.progress(pd, ProgressType.ITERATION);
	}
	
	public ProcessIteration nextInstance(Coupling pd) {
		return this.progress(pd, ProgressType.INSTANCE);
	}

	public CouplingInstance calculateCouplingInstance(Coupling cd) {
		ProcessIteration pnext;
		
		if (cd.getToOperator().getOperator() == SEL.finit) {
			pnext = this.nextInstance(cd);
		}
		else {
			pnext = this.nextIteration(cd);
		}
		
		return new CouplingInstance(this, pnext, cd);
	}

	private ProcessIteration progress(ProgressType instance) {		
		if (this.stateFinished) {
			throw new IllegalStateException("Can not progress to another state of " + this + " if the state has already progressed.");
		}
		this.stateFinished = true;
		this.initFinished = true;

		Instance pd = this.instance;
		
		AnnotationSet set = new AnnotationSet(this.annot);
		switch (instance) {
			case ITERATION:
				if (this.annot.operatorEq(SEL.Of)) {
					throw new IllegalStateException("Process '" + pd + "' was already finished but is called for a next step, in iteration " + this.annot.getInstance());
				}

				// Loop until the end condition is met or sequentially within the loop
				if (this.annot.operatorLE(SEL.B) || this.finalLoop()) {
					set.next(AnnotationType.OPERATOR);
				}
				else {
					set.setOperater(SEL.Oi);
					set.next(AnnotationType.ITERATION);
				}
				break;
			case INSTANCE:
				set.next(AnnotationType.INSTANCE);
				set.reset(AnnotationType.ITERATION);
				set.reset(AnnotationType.OPERATOR);
				break;
			default:
				throw new IllegalArgumentException("Only ITERATION and INSTANCE operations are allowed for instance progress.");
		}

		set.applySubject();

		// Since no more progress may be made, we can free the annotations
		this.annot.freeAnnotations();
		this.givenAnnot.freeAnnotations();

		return cache.getIteration(pd, set);
	}
	
	private ProcessIteration progress(Coupling cd, ProgressType instance) {		
		if (this.stateFinished) {
			throw new IllegalStateException("Can not progress to another processiteration " + cd + " if the state has already progressed.");
		}
		this.initFinished = true;

		Instance pd = cd.getTo();		
		AnnotationSet set = new AnnotationSet(this.givenAnnot);
		set.setSubject(pd);
		
		switch (instance) {
			case ITERATION:
				SEL op = cd.getToOperator().getOperator();
				if (op == SEL.Of) {
					throw new IllegalStateException("Process '" + pd + "' was already finished but is called again, in iteration " + set.getIteration());
				}

				if (!set.operatorLE(op) || set.operatorEq(op)) {
					set.next(AnnotationType.ITERATION);
				}

				set.setOperater(op);
				break;
			case INSTANCE:
				set.next(AnnotationType.INSTANCE);
				set.reset(AnnotationType.ITERATION);
				set.setOperater(cd.getToOperator().getOperator());
				break;
			default:
				throw new IllegalArgumentException("Only ITERATION and INSTANCE operations are allowed for instance progress.");
		}
		
		set.applySubject();
		this.annot.merge(set);

		return cache.getIteration(pd, set);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return this.origString.equals(((ProcessIteration)o).origString);
	}

	@Override
	public String toString() {
		return this.asString;
	}
	
	@Override
	public int hashCode() {
		return this.origString.hashCode();
	}

	public void updateRange(int iter, SEL oper) {
		if (iter < this.rangeFromIter) {
			this.rangeFromIter = iter;
			this.rangeFromOper = oper;
		}
		else if (iter == this.rangeFromIter && oper.compareTo(this.rangeFromOper) < 0) {
			this.rangeFromOper = oper;
		}

		if (iter == this.rangeToIter && oper.compareTo(this.rangeToOper) > 0) {
			this.rangeToOper = oper;
		}
		else if (iter > this.rangeToIter) {
			this.rangeToIter = iter;
			this.rangeToOper = oper;
		}

		String iterStr = null, operStr = null;
		String instString = this.getInstanceCounter() > 0 ? String.valueOf(this.getInstanceCounter()) : "";
		if (this.rangeFromIter < this.rangeToIter) {
			iterStr = this.rangeFromIter + "-" + this.rangeToIter;
			operStr = this.rangeFromOper + "-" + this.rangeToOper;
		}
		else {
			iterStr = String.valueOf(this.rangeFromIter);
			if (this.rangeFromOper.compareTo(this.rangeToOper) < 0) {
				operStr = this.rangeFromOper + "-" + this.rangeToOper;
			}
			else {
				operStr = this.rangeFromOper.toString();
			}
		}

		this.asString = this.instance.getId() + instString + "(" + iterStr + "," + operStr + ")";
	}

	public int getToIteration() {
		return this.rangeToIter;
	}

	public SEL getToOperator() {
		return this.rangeToOper;
	}

	public int getFromIteration() {
		return this.rangeFromIter;
	}

	public SEL getFromOperator() {
		return this.rangeFromOper;
	}
	
	class AnnotationSet {
		private Annotation<Instance> anInst, anIter, anOper;
		private Instance inst;
		private int iterCounter, instCounter;
		private SEL op;

		/** Create a new empty annotationset */
		AnnotationSet() {
			this(new Annotation<Instance>(AnnotationType.INSTANCE), new Annotation<Instance>(AnnotationType.ITERATION), new Annotation<Instance>(AnnotationType.OPERATOR));
		}

		/** Apply the current counter to the trace of each annotation */
		public void applySubject() {
			for (AnnotationType at : AnnotationType.values()) {
				this.getAnnotation(at).setSubject(this.inst);
			}
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

			this.updateCounter(inst);
			this.updateCounter(iter);
			this.updateCounter(oper);
		}
		
		/** Current operator. */
		SEL getOperator() {
			return this.op;
		}
		
		/** Get the value of the current iteration */
		int getIteration() {
			return this.iterCounter;
		}
		
		/** Get the value of the current iteration */
		int getInstance() {
			return this.instCounter;
		}

		/** Current operator is less than or equal to the given operator. */
		boolean operatorLE(SEL op) {
			return this.op.compareTo(op) <= 0;
		}
		
		/** Current operator is larger than or equal to the given operator. */
		boolean operatorEq(SEL op) {
			return this.op == op;
		}
		
		/** Set the operater */
		void setOperater(SEL op) {
			this.op = op;
			this.anOper.set(this.inst, op.ordinal());
		}
		
		/** Go to the next value of the requested annotation */
		void next(AnnotationType at) {
			Annotation<Instance> an = this.getAnnotation(at);
			an.next(inst);
			this.updateCounter(an);
		}
		
		/** Reset the value of the requested annotation */
		void reset(AnnotationType at) {
			Annotation<Instance> an = this.getAnnotation(at);
			an.reset(inst);
			this.updateCounter(an);
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
			AnnotationType at = an.getType();
			switch (at) {
				case ITERATION:
					this.anIter = an;
					this.iterCounter = an.getCounter();
					break;
				case INSTANCE:
					this.anInst = an;
					this.instCounter = an.getCounter();
					break;
				case OPERATOR:
					this.anOper = an;
					this.op = SEL.get(an.getCounter());
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
		
		/** Merge the given set with the current one */
		public void merge(AnnotationSet set) {
			this.anInst.merge(set.anInst, false);
			List<List<Instance>> col = this.anIter.merge(set.anIter, true);
			// Merge if the iteration was equal, override if it was larger
			this.anOper.merge(set.anOper, col.get(0));
			this.anOper.override(set.anOper, col.get(1));
		}

		/**
		 * Removes all annotations, only keeping the counters.
		 * From this point on, it can not be modified or copied
		 */
		public void freeAnnotations() {
			this.anInst = null;
			this.anIter = null;
			this.anOper = null;
			this.inst = null;
		}

		@Override
		public int hashCode() {
			int hashCode = this.instCounter;
			hashCode = 31 * hashCode + this.iterCounter;
			hashCode = 31 * hashCode + this.op.ordinal();
			return hashCode;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			return this.instCounter == ((AnnotationSet)o).instCounter && this.iterCounter == ((AnnotationSet)o).iterCounter && this.op == ((AnnotationSet)o).op;
		}
		
		@Override
		public String toString() {
			if (this.anInst == null && this.anInst == null && this.anOper == null) {
				return "[" + this.anInst + ", " + this.anIter + ", " + this.anOper + "]";
			}
			else {
				return this.counterString();
			}
		}
		
		/**
		 * Show a counter of this annotation set
		 */
		String counterString() {
			StringBuilder sb = new StringBuilder();
			if (this.instCounter > 0) {
				sb.append(this.instCounter);
			}
			sb.append('(');
			sb.append(this.iterCounter);
			sb.append(',');
			sb.append(this.op);
			sb.append(')');
			return sb.toString();
		}
	}
}
