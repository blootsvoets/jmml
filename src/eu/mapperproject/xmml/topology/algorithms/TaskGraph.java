package eu.mapperproject.xmml.topology.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.mapperproject.xmml.definitions.Submodel.SEL;
import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.topology.CouplingTopology;
import eu.mapperproject.xmml.topology.Instance;
import eu.mapperproject.xmml.topology.InstanceOperator;
import eu.mapperproject.xmml.topology.algorithms.ProcessIteration.ProgressType;
import eu.mapperproject.xmml.util.graph.PTGraph;

/** Describes the coupling topology of a model and can convert it to a task graph */ 
public class TaskGraph {
	private final PTGraph<ProcessIteration, CouplingInstance> graph;
	private final List<ProcessIteration> pis;
	private CouplingTopology desc;
	
	public TaskGraph(CouplingTopology topology) {
		this(topology, false, false);
	}
	
	public TaskGraph(CouplingTopology topology, boolean horizontal, boolean subgraphs) {
		this.desc = topology;
		this.graph = new PTGraph<ProcessIteration, CouplingInstance>(true);
		this.pis = descriptionToIteration(topology.getInstances());
	}

	
	public PTGraph<ProcessIteration, CouplingInstance> getGraph() {
		this.computeGraph();
		
		return this.graph;
	}
	
	private void computeGraph() {
		TaskGraphState state = new TaskGraphState(this.desc);
		
		// Initialize processes with no initialization
		for (ProcessIteration pi : pis) {
			if (pi.getInstance().isInitial()) {
				state.activate(pi);
				this.graph.addNode(pi);
			}
		}
		
		// As long as there are active processes, continue building the graph
		for (ProcessIteration pi : state) {
			boolean complete = pi.instanceCompleted();
			if (pi.getCouplingType().isSending()) {		
				boolean hasNext = this.computeNextIteration(state, pi, complete);
					
				if (complete && !hasNext) {
					pi.setFinal();
				}
			}
			
			if (complete) {
				state.addState(pi);
			}
			else {
				this.activateStep(state, pi);
			}
		}
		
		state.printDeadlock();
	}
	
	/**
	 * Computes the next iteration given the current process iteration
	 * @return whether a next iteration could be computed
	 */
	private boolean computeNextIteration(TaskGraphState state, ProcessIteration pi, boolean complete) {
		SEL ct = complete ? SEL.Of : SEL.Oi;
		Collection<Coupling> cds = this.desc.getFrom(new InstanceOperator(pi.getInstance(), ct));
		
		for (Coupling cd : cds) {
			this.calculateTo(pi, cd, state);
		}
		
		return !cds.isEmpty();
	}
	
	/** Create an instance of a normal coupling between one process iteration the next */
	private void calculateTo(ProcessIteration from, Coupling cd, TaskGraphState state) {
		CouplingInstance ci;
		if (cd.getToOperator().getOperator() == SEL.finit) {
			ci = state.initInstance(cd);
			if (ci != null) this.addToGraph(ci, state);
		}
		
		ci = from.calculateCouplingInstance(cd);
		if (ci != null) this.addToGraph(ci, state);
	}
		
	private void activateStep(TaskGraphState state, ProcessIteration pi) {
		ProcessIteration pnext = pi.nextStep();
		if (pnext != null) {
			this.addToGraph(new CouplingInstance(pi, pnext), state);
		}
	}
	
	private void addToGraph(CouplingInstance ci, TaskGraphState state) {
		ProcessIteration active = state.activate(ci);
		if (active != null) this.graph.addNode(active);
		this.graph.addEdge(ci);		
	}
	
	public static List<ProcessIteration> descriptionToIteration(Collection<Instance> pds) {
		ArrayList<ProcessIteration> pis = new ArrayList<ProcessIteration>(pds.size());
		for (Instance pd : pds) {
			pis.add(new ProcessIteration(pd));
		}
		return pis;
	}
}
