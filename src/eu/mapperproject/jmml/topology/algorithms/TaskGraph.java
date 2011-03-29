package eu.mapperproject.jmml.topology.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.mapperproject.jmml.definitions.Submodel.SEL;
import eu.mapperproject.jmml.topology.Coupling;
import eu.mapperproject.jmml.topology.CouplingTopology;
import eu.mapperproject.jmml.topology.Instance;
import eu.mapperproject.jmml.topology.InstanceOperator;
import eu.mapperproject.jmml.util.graph.PTGraph;

/** Describes the coupling topology of a model and can convert it to a task graph */ 
public class TaskGraph {
	private final PTGraph<ProcessIteration, CouplingInstance> graph;
	private CouplingTopology topology;
	
	public TaskGraph(CouplingTopology topology) {
		this(topology, false, false);
	}
	
	public TaskGraph(CouplingTopology topology, boolean horizontal, boolean subgraphs) {
		this.topology = topology;
		this.graph = new PTGraph<ProcessIteration, CouplingInstance>(true);
	}

	
	public PTGraph<ProcessIteration, CouplingInstance> getGraph() {
		return this.graph;
	}

	public void computeGraph() {
		TaskGraphState state = new TaskGraphState(this.topology);
		List<ProcessIteration> initProcs = descriptionToIteration(topology.getInitialInstances());
		int i = 0;

		// Initialize processes with no initialization
		for (ProcessIteration pi : initProcs) {
			state.activate(pi);
		}
		
		// As long as there are active processes, continue building the graph
		for (ProcessIteration pi : state) {
			i++;
			if (i % 10000 == 0) {
				System.out.println("After " + i + " iterations, processing node " + pi + ", which has " + pi.getInstance().getSubmodel().getScaleMap().getTimesteps() + " steps.");
			}
			// Only add to graph if it hasn't been added before
			if (pi.isSingle()) this.graph.addNode(pi);
			
			boolean complete = pi.instanceCompleted();
			// There are only couplings out if the operator can send
			if (pi.getOperator().isSending()) {
				boolean hasNext = this.computeNextIteration(state, pi, complete);

				if (complete && !hasNext) {
					pi.setFinal();
				}
			}

			// If complete, add the state of the iteration for later use, if necessary
			if (complete) {
				state.addState(pi);
			}
			// Otherwise, proceed to the next operator
			else {
				this.activateStep(state, pi);
			}
		}
		
		Collection<ProcessIteration> dead = state.printDeadlock();
		if (dead != null) {
			for (ProcessIteration pi : dead) {
				this.graph.addNode(pi);
			}
		}
	}
	
	/**
	 * Computes the next iteration given the current process iteration
	 * @return whether a next iteration could be computed
	 */
	private boolean computeNextIteration(TaskGraphState state, ProcessIteration pi, boolean complete) {
		SEL ct = complete ? SEL.Of : SEL.Oi;
		Collection<Coupling> cds = this.topology.getFrom(new InstanceOperator(pi.getInstance(), ct));
		
		for (Coupling cd : cds) {
			if (pi.firstLoop() && topology.needsInitInstances(cd.getTo())) {
				cd = cd.copyWithToOperator(SEL.finit);
			}

			this.calculateTo(pi, cd, state);
		}
		
		return !cds.isEmpty();
	}
	
	/** Create an instance of a normal coupling between one process iteration the next */
	private void calculateTo(ProcessIteration from, Coupling cd, TaskGraphState state) {
		CouplingInstance ci;
		if (cd.getToOperator().getOperator() == SEL.finit) {
			ci = state.tryStateful(cd.getTo());
			if (ci != null) this.addToGraph(ci, state);
		}
		
		ci = from.calculateCouplingInstance(cd);
		this.addToGraph(ci, state);
	}

	/** Create an instance of a stateful coupling between one process iteration and the next */
	private void activateStep(TaskGraphState state, ProcessIteration pi) {
		ProcessIteration pnext = pi.nextStep();
		if (pnext == null) {
			state.activate(pi);
		}
		else {
			this.addToGraph(new CouplingInstance(pi, pnext), state);
		}
	}

	/** Add the coupling to the graph and current state */
	private void addToGraph(CouplingInstance ci, TaskGraphState state) {
		state.activate(ci);
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
