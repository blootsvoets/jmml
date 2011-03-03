package eu.mapperproject.xmml.topology.algorithms;

import java.util.ArrayList;
import java.util.List;

import eu.mapperproject.xmml.definitions.Submodel.SEL;
import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.topology.Domain;
import eu.mapperproject.xmml.topology.algorithms.ProcessIteration.ProgressType;
import eu.mapperproject.xmml.topology.algorithms.TaskGraph.ModelComplexity;
import eu.mapperproject.xmml.util.graph.Category;
import eu.mapperproject.xmml.util.graph.GraphvizEdge;

public class CouplingInstance implements GraphvizEdge {
	private ProcessIteration from;
	private ProcessIteration to;
	private ModelComplexity mc;
	private Domain domain;
	private Coupling desc;
	
	public CouplingInstance(ProcessIteration from, ProcessIteration to, Coupling cd, ModelComplexity mc) {
		this.desc = cd;
		this.domain = cd == null ? Domain.MULTIPLE : cd.getDomain();
		this.from = from;
		this.to = to;
		this.mc = mc;
	}
	
	public static List<CouplingInstance> calculateTo(ProcessIteration from, Coupling cd, TaskGraphState state) {
		List<CouplingInstance> cis = new ArrayList<CouplingInstance>();
		
		CouplingInstance ci;
		if (cd.getToPort().getOperator() == SEL.finit) {
			ci = state.initInstance(cd);				
			if (ci != null) cis.add(ci);
		}
		
		ci = calculateSingleTo(from, cd);
		if (ci != null) cis.add(ci);
		
		return cis;
	}
	
	private static CouplingInstance calculateSingleTo(ProcessIteration from, Coupling cd) {
		ProgressType instance, worker;
		ModelComplexity mc = null;
		
		if (cd.getToPort().getOperator() == SEL.finit) {
			instance = ProgressType.INSTANCE;
			mc = cd.getFromPort().getOperator() == SEL.Of ? ModelComplexity.PATH : ModelComplexity.TREE;
		}
		else {
			instance = ProgressType.ITERATION;
			mc = ModelComplexity.ROOTED_DAG;
		}
		
		worker = ProgressType.CURRENT;

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
			return this.desc.getName();
		}
	}
	
	@Override
	public String getStyle() {
		return (this.desc == null && this.from != null && this.to != null) ? "style=dashed" : null;
	}

	@Override
	public Category getCategory() {
		return new Category(this.domain);
	}
	
	@Override
	public String toString() {
		return this.from + " -> " + this.to;
	}
}
