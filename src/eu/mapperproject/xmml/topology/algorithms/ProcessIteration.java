package eu.mapperproject.xmml.topology.algorithms;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import eu.mapperproject.xmml.definitions.Submodel.SEL;
import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.topology.Instance;
import eu.mapperproject.xmml.topology.algorithms.Annotation.AnnotationType;

public class ProcessIteration {
	private static final ProcessIterationCache cache = new ProcessIterationCache();
	private final AnnotationSet givenAnnot, annot;
	private Instance instance;
	private boolean isfinal;
	private boolean stateFinished, initFinished;
	
	public enum ProgressType {
		NEXT, RESET, INSTANCE, ITERATION;
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
	public void merge(AnnotationSet key) {
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
		if (o == null || !this.getClass().equals(o.getClass())) return false;
		ProcessIteration other = (ProcessIteration)o;
		
		return this.instance.equals(other.instance) && this.givenAnnot.equals(other.givenAnnot);
	}

	@Override
	public String toString() {
		String ret = instance.getId();
		
		return ret + givenAnnot.counterString();
	}
	
	@Override
	public int hashCode() {
		int hashCode = this.instance.hashCode();
		return hashCode;
	}
	
	class AnnotationSet {
		private final Map<AnnotationType,Annotation<Instance>> map;
		private Instance inst;

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
			this(old.map.get(AnnotationType.INSTANCE).copy(), old.map.get(AnnotationType.ITERATION).copy(), old.map.get(AnnotationType.OPERATOR).copy());
			this.inst = old.inst;
		}

		/** Create an annotationset consisting of given annotations */
		AnnotationSet(Annotation<Instance> inst, Annotation<Instance> iter, Annotation<Instance> oper) {
			if (!iter.getType().equals(AnnotationType.ITERATION) || !inst.getType().equals(AnnotationType.INSTANCE) || !oper.getType().equals(AnnotationType.OPERATOR)) {
				throw new IllegalArgumentException("AnnotationSet initialized with wrong Annotation types");
			}

			this.map = new EnumMap<AnnotationType, Annotation<Instance>>(AnnotationType.class);
			this.map.put(AnnotationType.ITERATION, iter);
			this.map.put(AnnotationType.INSTANCE, inst);
			this.map.put(AnnotationType.OPERATOR, oper);
			this.inst = instance;
		}
		
		/** Current operator. */
		SEL getOperator() {
			return SEL.values()[this.map.get(AnnotationType.OPERATOR).getCounter()];
		}
		
		/** Get the value of the current iteration */
		int getIteration() {
			return this.map.get(AnnotationType.ITERATION).getCounter();
		}
		
		/** Get the value of the current iteration */
		int getInstance() {
			return this.map.get(AnnotationType.INSTANCE).getCounter();
		}

		/** Current operator is less than or equal to the given operator. */
		boolean operatorLE(SEL op) {
			return this.map.get(AnnotationType.OPERATOR).getCounter() <= op.ordinal();
		}
		
		/** Current operator is larger than or equal to the given operator. */
		boolean operatorEq(SEL op) {
			return this.map.get(AnnotationType.OPERATOR).getCounter() == op.ordinal();
		}
		
		/** Set the operater */
		void setOperater(SEL op) {
			this.map.put(AnnotationType.OPERATOR, this.map.get(AnnotationType.OPERATOR).set(this.inst, op.ordinal()));
		}
		
		/** Go to the next value of the requested annotation */
		void next(AnnotationType at) {
			this.map.put(at, this.map.get(at).next(inst));
		}
		
		/** Reset the value of the requested annotation */
		void reset(AnnotationType at) {
			this.map.put(at, this.map.get(at).reset(inst));
		}
		
		/** Let the current instance be the subject of the annotation set */
		void setSubject(Instance inst) {
			for (Map.Entry<AnnotationType,Annotation<Instance>> an : this.map.entrySet()) {
				map.put(an.getKey(), an.getValue().current(inst));
			}
			this.inst = inst;
		}
		
		/** Merge the given set with the current one */
		public void merge(AnnotationSet set) {
			map.get(AnnotationType.INSTANCE).merge(set.map.get(AnnotationType.INSTANCE));
			Collection<Instance> col = map.get(AnnotationType.ITERATION).merge(set.map.get(AnnotationType.ITERATION));
			map.get(AnnotationType.OPERATOR).override(set.map.get(AnnotationType.OPERATOR), col);
		}

		@Override
		public int hashCode() {
			return this.map.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null || !this.getClass().equals(o.getClass())) return false;
			AnnotationSet as = (AnnotationSet)o;
			return this.map.equals(as.map);
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
			Annotation<Instance> inst = this.map.get(AnnotationType.INSTANCE);
			if (inst.getCounter() > 0) {
				ret += inst.counterString();
			}
			ret += "(" + this.map.get(AnnotationType.ITERATION).counterString() + "," + this.getOperator() +  ")";
			return ret;
		}
	}
}
