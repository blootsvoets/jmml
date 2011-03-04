package eu.mapperproject.xmml.topology.algorithms;

import java.util.ArrayList;
import java.util.List;

import eu.mapperproject.xmml.definitions.Submodel.SEL;
import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.topology.algorithms.ProcessIteration.ProgressType;
import eu.mapperproject.xmml.util.graph.Edge;

public class CouplingInstance implements Edge<ProcessIteration> {
	private ProcessIteration from;
	private ProcessIteration to;
	private Coupling coupling;
	
	/** Create an instance of a coupling between one process iteration and another */
	public CouplingInstance(ProcessIteration from, ProcessIteration to, Coupling cd) {
		this.coupling = cd;
		this.from = from;
		this.to = to;
	}
	
	/** Create an instance of a state coupling between one process iteration the next */
	public CouplingInstance(ProcessIteration from, ProcessIteration to) {
		this(from, to, null);
	}
	
	public static List<CouplingInstance> calculateTo(ProcessIteration from, Coupling cd, TaskGraphState state) {
		List<CouplingInstance> cis = new ArrayList<CouplingInstance>();
		
		CouplingInstance ci;
		if (cd.getFromOperator().getOperator() == SEL.finit) {
			ci = state.initInstance(cd);				
			if (ci != null) cis.add(ci);
		}
		
		ci = calculateSingleTo(from, cd);
		if (ci != null) cis.add(ci);
		
		return cis;
	}
	
	private static CouplingInstance calculateSingleTo(ProcessIteration from, Coupling cd) {
		ProgressType instance;
		
		if (cd.getToOperator().getOperator() == SEL.finit) {
			instance = ProgressType.INSTANCE;
		}
		else {
			instance = ProgressType.ITERATION;
		}
		
		ProcessIteration pnext = from.progress(cd, instance);
		if (pnext == null) return null;
		else return new CouplingInstance(from, pnext, cd);
	}
	
	public boolean isState() {
		return this.coupling == null;
	}
	
	public Coupling getCoupling() {
		return this.coupling;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || this.getClass().equals(o.getClass())) return false;
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
	public String toString() {
		return this.from + " -> " + this.to;
	}
}
