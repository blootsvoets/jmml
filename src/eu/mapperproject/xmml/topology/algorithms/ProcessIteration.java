package eu.mapperproject.xmml.topology.algorithms;

import eu.mapperproject.xmml.definitions.Submodel.SEL;
import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.topology.Instance;
import eu.mapperproject.xmml.topology.algorithms.Annotation.AnnotationType;

public class ProcessIteration {
	private static final ProcessIterationCache cache = new ProcessIterationCache();
	Annotation<Instance> iter, inst, oper;
	private AnnotationSet givenAnnot, currentAnnot;
	private Instance instance;
	private boolean isfinal;
	
	public enum ProgressType {
		OPERATOR, ITERATION, INSTANCE, CURRENT;
	}
	
	public ProcessIteration(Instance pd) {
		this(pd, new Annotation<Instance>(AnnotationType.ITERATION), new Annotation<Instance>(AnnotationType.INSTANCE), new Annotation<Instance>(AnnotationType.OPERATOR));
	}
	
	ProcessIteration(Instance pd, Annotation<Instance> it, Annotation<Instance> nt, Annotation<Instance> op) {
		this.instance = pd;
		it.setSubject(pd);
		nt.setSubject(pd);
		op.setSubject(pd);
		this.iter = it;
		this.inst = nt;
		this.oper = op;
		this.isfinal = false;
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
		return this.oper.getCounter() == SEL.Of.ordinal();
	}
	
	public boolean loopCompleted() {
		return this.instance.isCompleted(this.iter.getCounter()) && (this.oper.getCounter() >= SEL.S.ordinal());
	}
	
	public Instance getInstance() {
		return this.instance;
	}
	
	public boolean firstInstance() {
		return this.inst.getCounter() == 0;
	}
	
	public boolean initializing() {
		return this.oper.getCounter() == SEL.finit.ordinal();
	}
	
	public boolean needsState() {
		return initializing() && !firstInstance() && instance.getSubmodel().isStateful();
	}
	
	public SEL receivingType() {
		SEL op = SEL.values()[this.oper.getCounter()];
		if (op.isReceiving()) return op;
		else return null;
	}

	public SEL getCouplingType() {
		return SEL.values()[this.oper.getCounter()];
	}
	
	public ProcessIteration nextStep() {
		return this.progress(ProgressType.ITERATION);
	}
	
	public ProcessIteration nextState() {
		return this.progress(ProgressType.INSTANCE);
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
		
	public void merge(ProcessIteration pi) {
		iter.merge(pi.iter);
		inst.merge(pi.inst);
		oper.merge(pi.oper);
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
		Instance pd = this.instance;
		
		Annotation<Instance> it, nt, op;

		switch (instance) {
			case ITERATION:
				nt = this.inst.current(pd);

				if (this.oper.getCounter() == SEL.Of.ordinal()) {
					throw new IllegalStateException("Process '" + pd + "' was already finished but is called for a next step, in iteration " + this.iter);
				}

				// Loop until the end condition is met
				if (this.oper.current(pd).getCounter() < SEL.S.ordinal() || pd.isCompleted(this.iter.current(pd).getCounter())) {
					op = this.oper.next(pd);
					it = this.iter.current(pd);
				}
				else {
					op = this.oper.set(pd, SEL.Oi.ordinal());
					it = this.iter.next(pd);
				}
				break;
			case INSTANCE:
				nt = this.inst.next(pd);
				it = this.iter.reset(pd);
				op = this.oper.reset(pd);
				break;
			default:
				throw new IllegalArgumentException("Only OPERATOR, ITERATION, PREVIOUS, INSTANCE and COPY operations are allowed for instance progress.");
		}
		
		ProcessIteration pnext = cache.getIteration(pd, it, nt, op);
		pnext.iter.merge(it);
		pnext.inst.merge(nt);
		pnext.oper.merge(op);
		return pnext;
	}
	
	private ProcessIteration progress(Coupling cd, ProgressType instance) {		
		Instance pd = cd.getTo();
		
		Annotation<Instance> it, nt, op;

		switch (instance) {
			case ITERATION:
				int opnum = cd.getToOperator().getOperatorNum();
				if (opnum >= this.oper.current(pd).getCounter()) {
					it = this.iter.current(pd); 
				}
				else {
					it = this.iter.next(pd);
				}
				op = this.oper.set(pd, opnum);

				if (op.getCounter() == SEL.Of.ordinal()) {
					throw new IllegalStateException("Process '" + pd + "' was already finished but is called again, in iteration " + it);
				}
				nt = this.inst.current(pd);
				break;
			case INSTANCE:
				nt = this.inst.next(pd);
				System.out.println(nt);
				it = this.iter.reset(pd);
				op = this.oper.set(pd, cd.getToOperator().getOperatorNum());
				break;
			default:
				throw new IllegalArgumentException("Only OPERATOR, ITERATION, PREVIOUS, INSTANCE and COPY operations are allowed for instance progress.");
		}
		
		ProcessIteration pnext = cache.getIteration(pd, it, nt, op);
		pnext.iter.merge(it);
		pnext.inst.merge(nt);
		pnext.oper.merge(op);
		return pnext;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !this.getClass().equals(o.getClass())) return false;
		ProcessIteration other = (ProcessIteration)o;
		
		return this.instance.equals(other.instance) && inst.equals(other.inst) && iter.equals(other.iter) && oper.equals(other.oper);
	}

	@Override
	public String toString() {
		String ret = instance.getId();
		
		return ret + givenAnnot;
	}
	
	@Override
	public int hashCode() {
		int hashCode = this.instance.hashCode();
		return hashCode;
	}
	
	private static class AnnotationSet {
		private Annotation<Instance> inst;
		private Annotation<Instance> iter;
		private Annotation<Instance> oper;

		AnnotationSet(Annotation<Instance> inst, Annotation<Instance> iter, Annotation<Instance> oper) {
			if (!iter.getType().equals(AnnotationType.ITERATION) || !inst.getType().equals(AnnotationType.INSTANCE) || !oper.getType().equals(AnnotationType.OPERATOR)) {
				throw new IllegalArgumentException("AnnotationSet initialized with wrong Annotation types");
			}

			this.inst = inst;
			this.iter = iter;
			this.oper = oper;
		}
		
		/** Current operator. */
		SEL getOperator() {
			return SEL.values()[this.oper.getCounter()];
		}
		
		/** Current operator is larger than or equal to the given operator. */
		boolean operatorLE(SEL op) {
			return this.oper.getCounter() >= op.ordinal();
		}

		/** Current operator is larger than or equal to the given operator. */
		boolean operatorEq(SEL op) {
			return this.oper.getCounter() == op.ordinal();
		}

		@Override
		public int hashCode() {
			int hashCode = this.inst.hashCode();
			hashCode = 31*hashCode + this.iter.hashCode();
			hashCode = 31*hashCode + this.oper.hashCode();
			return hashCode;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null || !this.getClass().equals(o.getClass())) return false;
			AnnotationSet as = (AnnotationSet)o;
			return this.inst.equals(as.inst) && this.iter.equals(as.iter) && this.oper.equals(as.oper);
		}
		
		@Override
		public String toString() {
			String ret = "";
			if (this.inst.getCounter() > 0) {
				ret += this.inst.counterString();
			}
			ret += "(" + this.iter.counterString() + "," + SEL.values()[this.oper.getCounter()] +  ")";
			return ret;
		}
	}
}
