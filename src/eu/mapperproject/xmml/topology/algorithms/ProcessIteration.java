package eu.mapperproject.xmml.topology.algorithms;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

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
        private final String asString;
	private boolean isfinal;
	private boolean stateFinished, initFinished;
	
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
                this.asString = this.instance.getId() + this.givenAnnot.counterString();
		this.isfinal = false;
		this.stateFinished = false;
		this.initFinished = false;
	}
	
	public boolean isFinal() {
		return this.isfinal;
	}
	
	public boolean isInitial() {
		return this.instance.isInitial() && firstInstance() && initializing();
	}
	
	public void setFinal() {
		this.isfinal = true;
	}
	
	public boolean instanceCompleted() {
		return this.annot.operatorEq(SEL.Of);
	}
	
	public boolean loopCompleted() {
		return this.instance.isCompleted(annot.getIteration()) && (!this.annot.operatorLE(SEL.B));
	}
	
	public Instance getInstance() {
		return this.instance;
	}
	
	public boolean firstInstance() {
		return this.annot.getInstance() == 0;
	}

	public boolean firstLoop() {
		return this.annot.getIteration() == 0 && this.annot.operatorLE(SEL.S);
	}
	
	public boolean initializing() {
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

	public SEL getCouplingType() {
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
		
		if (pnext == null) return null;
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

				// Loop until the end condition is met
				if (this.annot.operatorLE(SEL.B) || pd.isCompleted(set.getIteration())) {
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
		if (o == null || !this.getClass().equals(o.getClass())) return false;
		return this.asString.equals(o.toString());
	}

	@Override
	public String toString() {
		return this.asString;
	}
	
	@Override
	public int hashCode() {
		return this.asString.hashCode();
	}
	
	class AnnotationSet {
		private final Map<AnnotationType,Annotation<Instance>> map;
		private Instance inst;
        private int iterCounter, instCounter;
        private SEL op;

		/** Create a new empty annotationset */
		AnnotationSet() {
			this(new Annotation<Instance>(AnnotationType.INSTANCE), new Annotation<Instance>(AnnotationType.ITERATION), new Annotation<Instance>(AnnotationType.OPERATOR));
		}

		/** Apply the current counter to the trace of each annotation */
		public void applySubject() {
			for (Annotation<Instance> an : this.map.values()) {
				an.setSubject(this.inst);
			}
		}

		/** Create a copy of given annotationset */
		AnnotationSet(AnnotationSet old) {
			this(old.map.get(AnnotationType.INSTANCE), old.map.get(AnnotationType.ITERATION), old.map.get(AnnotationType.OPERATOR));
			this.inst = old.inst;
		}

		/** Create an annotationset consisting of given annotations */
		AnnotationSet(Annotation<Instance> inst, Annotation<Instance> iter, Annotation<Instance> oper) {
			if (!iter.getType().equals(AnnotationType.ITERATION) || !inst.getType().equals(AnnotationType.INSTANCE) || !oper.getType().equals(AnnotationType.OPERATOR)) {
				throw new IllegalArgumentException("AnnotationSet initialized with wrong Annotation types");
			}

			this.map = new EnumMap<AnnotationType, Annotation<Instance>>(AnnotationType.class);
			this.map.put(AnnotationType.ITERATION, iter);
			this.iterCounter = iter.getCounter();
			this.map.put(AnnotationType.INSTANCE, inst);
			this.instCounter = inst.getCounter();
			this.map.put(AnnotationType.OPERATOR, oper);
			this.op = SEL.values()[oper.getCounter()];
			this.inst = instance;
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
			return this.op.ordinal() <= op.ordinal();
		}
		
		/** Current operator is larger than or equal to the given operator. */
		boolean operatorEq(SEL op) {
			return this.op == op;
		}
		
		/** Set the operater */
		void setOperater(SEL op) {
			this.op = op;
			this.map.put(AnnotationType.OPERATOR, this.map.get(AnnotationType.OPERATOR).set(this.inst, op.ordinal()));
		}
		
		/** Go to the next value of the requested annotation */
		void next(AnnotationType at) {
                        Annotation<Instance> next = this.map.get(at).next(inst);
                        this.updateCounter(next);
		}
		
		/** Reset the value of the requested annotation */
		void reset(AnnotationType at) {
                        Annotation<Instance> reset = this.map.get(at).reset(inst);
                        this.updateCounter(reset);
		}

		private void updateCounter(Annotation<Instance> an) {
			AnnotationType at = an.getType();
			this.map.put(at, an);
			switch (at) {
				case ITERATION:
					this.iterCounter = an.getCounter();
					break;
				case INSTANCE:
					this.instCounter = an.getCounter();
					break;
				case OPERATOR:
					this.op = SEL.values()[an.getCounter()];
					break;
			}
		}
		
		/** Let the current instance be the subject of the annotation set */
		void setSubject(Instance inst) {
			for (Annotation<Instance> an : this.map.values()) {
				Annotation<Instance> cur = an.current(inst);
				this.updateCounter(cur);
			}
			this.inst = inst;
		}
		
		/** Merge the given set with the current one */
		public void merge(AnnotationSet set) {
			map.get(AnnotationType.INSTANCE).merge(set.map.get(AnnotationType.INSTANCE));
			List<Collection<Instance>> col = map.get(AnnotationType.ITERATION).merge(set.map.get(AnnotationType.ITERATION));
			map.get(AnnotationType.OPERATOR).merge(set.map.get(AnnotationType.OPERATOR), col.get(0));
			map.get(AnnotationType.OPERATOR).override(set.map.get(AnnotationType.OPERATOR), col.get(1));
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
			if (this == o) return true;
			if (o == null || !this.getClass().equals(o.getClass())) return false;
			AnnotationSet as = (AnnotationSet) o;
			return this.instCounter == as.instCounter && this.iterCounter == as.iterCounter && this.op == as.op;
		}
		
		@Override
		public String toString() {
			return this.map.values().toString();
		}
		
		/**
		 * Show a counter of this annotation set
		 */
		String counterString() {
			String ret = "";
			Annotation<Instance> an = this.map.get(AnnotationType.INSTANCE);
			if (an.getCounter() > 0) {
				ret += an.counterString();
			}
			ret += "(" + this.map.get(AnnotationType.ITERATION).counterString() + "," + this.getOperator() +  ")";
			return ret;
		}
	}
}
