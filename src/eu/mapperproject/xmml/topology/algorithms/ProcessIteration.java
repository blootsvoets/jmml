package eu.mapperproject.xmml.topology.algorithms;

import eu.mapperproject.xmml.topology.algorithms.Annotation.AnnotationType;
import eu.mapperproject.xmml.topology.algorithms.CouplingDescription.CouplingType;
import eu.mapperproject.xmml.topology.algorithms.graph.Category;
import eu.mapperproject.xmml.topology.algorithms.graph.Node;

public class ProcessIteration extends AbstractInstance<ProcessReference> implements GraphvizNode {
	Annotation<ProcessReference> iter, inst, oper;
	LabelStack label;
	
	public enum ProgressType {
		OPERATOR, ITERATION, INSTANCE, CURRENT, COPY, RESET;
	}
	
	public ProcessIteration(ProcessReference pd) {
		this(pd, new Annotation<ProcessReference>(AnnotationType.ITERATION), new Annotation<ProcessReference>(AnnotationType.INSTANCE), new Annotation<ProcessReference>(AnnotationType.OPERATOR), new LabelStack());
	}
	
	ProcessIteration(ProcessReference pd, Annotation<ProcessReference> it, Annotation<ProcessReference> nt, Annotation<ProcessReference> op, LabelStack label) {
		super(pd, pd.getDomain());
		if (!it.getType().equals(AnnotationType.ITERATION) || !nt.getType().equals(AnnotationType.INSTANCE) || !op.getType().equals(AnnotationType.OPERATOR)) {
			throw new IllegalArgumentException("ProcessIteration initialized with wrong Annotation types");
		}
		it.add(pd);
		nt.add(pd);
		op.add(pd);
		this.iter = new Annotation<ProcessReference>(it);
		this.inst = new Annotation<ProcessReference>(nt);
		this.oper = new Annotation<ProcessReference>(op);
		this.label = label;
	}
	
	public boolean instanceCompleted() {
		return this.desc.isCompleted(this.iter.getCounter()) && (this.oper.getCounter() == 1 || this.desc.isMicromodel());
	}
	
	public boolean firstInstance() {
		return this.inst.getCounter() == 0;
	}
	
	public boolean firstIteration() {
		return this.iter.getCounter() == 0 && this.oper.getCounter() == 0;
	}
	
	public boolean needsState() {
		return firstIteration() && !firstInstance() && getDescription().getDescription().stateful();
	}
	
	public CouplingType receivingType() {
		if (firstIteration()) return CouplingType.FINIT;
		else if (this.oper.getCounter() == 1) return CouplingType.S;
		else return null;
	}

	public CouplingType getCouplingType() {
		return CouplingType.getCoupling(this.oper.getCounter());
	}
	
	public ProcessIteration nextIteration(CouplingDescription pd) {
		return this.progress(pd, ProgressType.ITERATION, ProgressType.CURRENT);
	}
	
	public ProcessIteration nextInstance(CouplingDescription pd) {
		return this.progress(pd, ProgressType.INSTANCE, ProgressType.CURRENT);
	}
	
	public ProcessIteration nextWorker(CouplingDescription pd) {
		return this.progress(pd, ProgressType.INSTANCE, ProgressType.INSTANCE);
	}
	
	public ProcessIteration copyWorker(CouplingDescription pd) {
		return this.progress(pd, ProgressType.COPY, ProgressType.COPY);
	}
	
	public ProcessIteration relieveWorkerInstance(CouplingDescription pd) {
		return this.progress(pd, ProgressType.INSTANCE, ProgressType.RESET);
	}

	public ProcessIteration relieveWorkerIteration(CouplingDescription pd) {
		return this.progress(pd, ProgressType.ITERATION, ProgressType.RESET);
	}
	
	public void merge(ProcessIteration pi) {
		iter.merge(pi.iter);
		inst.merge(pi.inst);
		oper.merge(pi.oper);
	}

	public ProcessIteration progress(CouplingDescription cd, ProgressType instance, ProgressType worker) {		
		AnnotationMapping<ProcessReference> am = AnnotationMappingKB.getInstance().get(this, cd);
		if (am != null) {
			System.out.println("Using annotatation mapping");
			return am.map(this, cd);
		}

		ProcessReference pd;
		if (cd == null) {
			pd = this.desc;
		}
		else {
			pd = cd.getTo();
		}
		
		Annotation<ProcessReference> it = this.getFromFollowing(AnnotationType.ITERATION, cd);
		Annotation<ProcessReference> nt = this.getFromFollowing(AnnotationType.INSTANCE, cd);
		Annotation<ProcessReference> op = this.getFromFollowing(AnnotationType.OPERATOR, cd);

		LabelStack lb;
		switch (instance) {
			case ITERATION:
				if (op == null) {
					if (cd == null) {
						if (this.oper.current(pd).getCounter() == 0 && !pd.isMicromodel()) {
							op = this.oper.next(pd);
							it = it == null ? this.iter.current(pd) : it;		
						}
						else {
							op = this.oper.reset(pd);
							it = it == null ? this.iter.next(pd) : it;
						}
					}
					else {
						int opnum = cd.getToType().getNum();
						if (it == null) {
							it = (opnum > this.oper.current(pd).getCounter() && !pd.isMicromodel()) ? this.iter.current(pd) : this.iter.next(pd);
						}
						op = this.oper.set(pd, opnum);
					}
				}
				else if (it == null){
					it = op.current(pd).getCounter() == 0 || pd.isMicromodel() ? this.iter.next(pd) : this.iter.current(pd);
				}
				if (pd.isCompleted(it.getCounter() - 1)) {
					throw new IllegalStateException("Process '" + pd + "' was already finished but is called again, in iteration " + it);
				}
				nt = nt == null ? this.inst.current(pd) : nt;
				break;
			case INSTANCE:
				it = it == null ? this.iter.current(pd) : it;
				if (nt == null) {
					nt = this.inst.next(pd);
//					AnnotationFactory af = AnnotationFactory.getInstance();
//					nt = af.getAnnotation(pd, this.inst).next(pd);
//					af.maxAnnotation(pd, nt);
				}
				if (op == null) op = (cd == null) ? this.oper.reset(pd) : this.oper.set(pd, cd.getToType().getNum());
				break;
			case COPY:
				it = it == null ? new Annotation<ProcessReference>(this.iter) : it;
				nt = nt == null ? new Annotation<ProcessReference>(this.inst) : nt;
				op = op == null ? new Annotation<ProcessReference>(this.oper) : op;
				break;
			default:
				throw new IllegalArgumentException("Only OPERATOR, ITERATION, PREVIOUS, INSTANCE and COPY operations are allowed for instance progress.");
		}
		
		switch (worker) {
			case RESET:
				lb = this.label.decreaseLevel();
				break;
			case INSTANCE:
				lb = this.label.increaseLevel();
				break;
			case CURRENT:
				lb = this.label.copy();
				break;
			case COPY:
				lb = this.label.next();
				break;
			default:
				throw new IllegalArgumentException("Only INSTANCE, RESET, CURRENT and COPY operations are allowed for worker progress.");
		}
		
		ProcessIteration pnext = ProcessIterationFactory.getInstance().getIteration(pd, it, nt, op, lb);
		pnext.iter.merge(it);
		pnext.inst.merge(nt);
		pnext.oper.merge(op);
		return pnext;
	}
	
	/** see if the annotation has a following annotation, otherwise return null */
	private Annotation<ProcessReference> getFromFollowing(AnnotationType to, CouplingDescription cd) {
		if (cd == null) return null;
		AnnotationType ft = cd.follow(to);
		if (ft == null) return null;
		return new Annotation<ProcessReference>(this.getAnnotation(ft));
	}
	
	public Annotation<ProcessReference> getAnnotation(AnnotationType at) {
		switch (at) {
		case INSTANCE:
			return this.inst;
		case ITERATION:
			return this.iter;
		default:
			throw new IllegalArgumentException("Annotation type not supported by ProcessIteration");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!super.equals(o)) return false;
		ProcessIteration other = (ProcessIteration)o;
		
		return inst.equals(other.inst) && iter.equals(other.iter) && oper.equals(other.oper) && label.equals(other.label);
	}

	@Override
	public String toString() {
		String ret = "";
		if (!this.label.isEmpty()) ret += this.label + ":";
		String pdString = this.inst.getCounter() > 0 ? desc.toString() + this.inst.counterString() : desc.toString();
		String count;
		if (desc.isMicromodel()) {
			count = "";
		}
		else {
			count = "(";
			if (this.desc.multistep()) count += this.iter.counterString() + ",";
			count += CouplingType.getCoupling(this.oper.getCounter()) +  ")";
		}
		
		return ret + pdString + count;
	}
	
	@Override
	public String getName() {
		return this.toString();
	}

	@Override
	public String getStyle() {
		return null;
	}
	
	@Override
	public Category getCatagory() {
		return new Category(this.getDomain());
	}
	
	@Override
	protected int otherHashCode() {
		int hashCode = 1;
		hashCode = 31*hashCode + this.inst.hashCode();
		hashCode = 31*hashCode + this.iter.hashCode();
		hashCode = 31*hashCode + this.oper.hashCode();
		return hashCode;
	}
}
