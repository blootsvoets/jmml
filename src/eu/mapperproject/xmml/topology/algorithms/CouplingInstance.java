package eu.mapperproject.xmml.topology.algorithms;

import java.util.ArrayList;
import java.util.List;

import eu.mapperproject.xmml.topology.algorithms.CouplingDescription.CouplingType;
import eu.mapperproject.xmml.topology.algorithms.ProcessIteration.ProgressType;
import eu.mapperproject.xmml.topology.algorithms.TaskGraph.ModelComplexity;
import eu.mapperproject.xmml.topology.algorithms.graph.Category;
import eu.mapperproject.xmml.topology.algorithms.graph.Edge;

public class CouplingInstance extends AbstractInstance<CouplingDescription> implements GraphvizEdge {
	private ProcessIteration from;
	private ProcessIteration to;
	private ModelComplexity mc;
	
	public CouplingInstance(ProcessIteration from, ProcessIteration to, CouplingDescription cd, ModelComplexity mc) {
		super(cd, cd == null ? Domain.MULTIPLE : cd.getDomain());
		this.from = from;
		this.to = to;
		this.mc = mc;
	}
	
	public static List<CouplingInstance> calculateTo(ProcessIteration from, CouplingDescription cd, TaskGraphState state) {
		List<CouplingInstance> cis = new ArrayList<CouplingInstance>();
		
		if (cd.hasMultiple()) {
			ProcessIteration pnext = from.nextWorker(cd);
			cis.add(new CouplingInstance(from, pnext, cd, ModelComplexity.MULTIPLICITY));
			for (int i = 1; i < cd.getMultiplicity(); i++) {
				pnext = pnext.copyWorker(cd);
				cis.add(new CouplingInstance(from, pnext, cd, null));
			}
		}
		else {
			CouplingInstance ci;
			if (cd.toMatches(CouplingType.FINIT)) {
				ci = state.initInstance(cd);				
				if (ci != null) cis.add(ci);
			}
			
			ci = calculateSingleTo(from, cd);
			if (ci != null) cis.add(ci);
		}
		
		return cis;
	}
	
	private static CouplingInstance calculateSingleTo(ProcessIteration from, CouplingDescription cd) {
		ProgressType instance, worker;
		ModelComplexity mc = null;
		
		if (cd.toMatches(CouplingType.FINIT)) {
			instance = ProgressType.INSTANCE;
			mc = cd.fromMatches(CouplingType.OF) ? ModelComplexity.PATH : ModelComplexity.TREE;
		}
		else {
			instance = ProgressType.ITERATION;
			mc = ModelComplexity.ROOTED_DAG;
		}
		
		if (cd.removesMultiple()) {
			worker = ProgressType.RESET;
			mc = ModelComplexity.LABEL_REMOVAL;
		}
		else {
			worker = ProgressType.CURRENT;
		}

		ProcessIteration pnext = from.progress(cd, instance, worker);
		if (pnext == null) return null;
		else return new CouplingInstance(from, pnext, cd, mc);
	}
	
	public ModelComplexity getComplexity() {
		return this.mc;
	}
	
	public boolean equals(Object o) {
		if (!super.equals(o)) return false;
		CouplingInstance ci = (CouplingInstance)o;
		
		return this.from.equals(ci.from) && this.to.equals(ci.to);
	}

	@Override
	public ProcessIteration getFrom() {
		return this.from;
	}

	@Override
	public ProcessIteration getTo() {
		return this.to;
	}

	@Override
	public String getLabel() {
		if (this.desc == null) {
			if (this.from == null || this.to == null) return null;
			else return "step";
		}
		else {
			return this.desc.getLabel();
		}
	}
	
	@Override
	public String getStyle() {
		return (this.desc == null && this.from != null && this.to != null) ? "style=dashed" : null;
	}

	@Override
	public Category getCatagory() {
		return new Category(this.getDomain());
	}
	
	@Override
	public String toString() {
		return this.from + " -> " + this.to;
	}
}
