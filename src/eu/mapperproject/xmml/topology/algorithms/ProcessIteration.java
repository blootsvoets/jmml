package eu.mapperproject.xmml.topology.algorithms;

import eu.mapperproject.xmml.definitions.Submodel.SEL;
import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.topology.Instance;
import eu.mapperproject.xmml.topology.algorithms.Annotation.AnnotationType;

public class ProcessIteration {
	private static final ProcessIterationCache cache = new ProcessIterationCache();
	Annotation<Instance> iter, inst, oper;
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
		if (!it.getType().equals(AnnotationType.ITERATION) || !nt.getType().equals(AnnotationType.INSTANCE) || !op.getType().equals(AnnotationType.OPERATOR)) {
			throw new IllegalArgumentException("ProcessIteration initialized with wrong Annotation types");
		}
		it.add(pd);
		nt.add(pd);
		op.add(pd);
		this.iter = new Annotation<Instance>(it);
		this.inst = new Annotation<Instance>(nt);
		this.oper = new Annotation<Instance>(op);
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

	public ProcessIteration progress(ProgressType instance) {		
		Instance pd = this.instance;
		
		Annotation<Instance> it = null;
		Annotation<Instance> nt = null;
		Annotation<Instance> op = null;

		switch (instance) {
			case ITERATION:
				nt = this.inst.current(pd);

				// Loop until the end condition is met
				if (this.oper.current(pd).getCounter() < SEL.S.ordinal() || pd.isCompleted(this.iter.current(pd).getCounter())) {
					op = this.oper.next(pd);
					it = this.iter.current(pd);
				}
				else {
					op = this.oper.set(pd, SEL.Oi.ordinal());
					it = this.iter.next(pd);
				}

				if (pd.isCompleted(it.getCounter() - 1)) {
					throw new IllegalStateException("Process '" + pd + "' was already finished but is called again, in iteration " + it);
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
	
	public ProcessIteration progress(Coupling cd, ProgressType instance) {		
		Instance pd = cd.getTo();
		
		Annotation<Instance> it = null;
		Annotation<Instance> nt = null;
		Annotation<Instance> op = null;

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

				if (pd.isCompleted(it.getCounter() - 1)) {
					throw new IllegalStateException("Process '" + pd + "' was already finished but is called again, in iteration " + it);
				}
				nt = this.inst.current(pd);
				break;
			case INSTANCE:
				it = this.iter.current(pd);
				nt = this.inst.next(pd);
				op = this.oper.set(pd, cd.getToOperator().getOperatorNum());
				break;
			default:
				throw new IllegalArgumentException("Only OPERATOR, ITERATION, PREVIOUS, INSTANCE and COPY operations are allowed for instance progress.");
		}
		
		ProcessIteration pnext = cache.getIteration(pd, it, nt, op);
//		pnext.iter.merge(it);
//		pnext.inst.merge(nt);
//		pnext.oper.merge(op);
		return pnext;
	}
	
	public Annotation<Instance> getAnnotation(AnnotationType at) {
		switch (at) {
		case INSTANCE:
			return this.inst;
		case ITERATION:
			return this.iter;
		case OPERATOR:
			return this.oper;
		default:
			throw new IllegalArgumentException("Annotation type not supported by ProcessIteration");
		}
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
		if (this.inst.getCounter() > 0) {
			ret += this.inst.counterString();
		}
		String count = "(" + this.iter.counterString() + "," + SEL.values()[this.oper.getCounter()] +  ")";
		
		return ret + count;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = 31*hashCode + this.inst.hashCode();
		hashCode = 31*hashCode + this.iter.hashCode();
		hashCode = 31*hashCode + this.oper.hashCode();
		return hashCode;
	}
}
