package eu.mapperproject.xmml.topology.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.mapperproject.xmml.definitions.Submodel.SEL;
import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.topology.CouplingTopology;
import eu.mapperproject.xmml.topology.Instance;
import eu.mapperproject.xmml.topology.InstanceOperator;
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
			List<CouplingInstance> cis = CouplingInstance.calculateTo(pi, cd, state);
			this.activate(state, cis);
		}
		
		return !cds.isEmpty();
	}
	
	private void activateStep(TaskGraphState state, ProcessIteration pi) {
		ProcessIteration pnext = pi.nextStep();
		if (pnext != null) this.activate(state, new CouplingInstance(pi, pnext));		
	}
	
	private void activate(TaskGraphState state, CouplingInstance ci) {
		ProcessIteration active = state.activate(ci);
		if (active != null) this.graph.addNode(active);

		this.graph.addEdge(ci);
	}
	
	private void activate(TaskGraphState state, List<CouplingInstance> cis) {
		for (CouplingInstance ci : cis) {
			this.activate(state, ci);
		}
	}
	
	public static List<ProcessIteration> descriptionToIteration(Collection<Instance> pds) {
		ArrayList<ProcessIteration> pis = new ArrayList<ProcessIteration>(pds.size());
		for (Instance pd : pds) {
			pis.add(new ProcessIteration(pd));
		}
		return pis;
	}
}
