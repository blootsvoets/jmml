package eu.mapperproject.jmml.topology.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.mapperproject.jmml.specification.SEL;
import eu.mapperproject.jmml.specification.annotated.AnnotatedCoupling;
import eu.mapperproject.jmml.specification.annotated.AnnotatedInstance;
import eu.mapperproject.jmml.specification.annotated.AnnotatedScale;
import eu.mapperproject.jmml.specification.annotated.AnnotatedTopology;
import eu.mapperproject.jmml.util.graph.PTGraph;

/** Describes the coupling topology of a model and can convert it to a task graph */ 
public class TaskGraph {
	private final PTGraph<ProcessIteration, CouplingInstance> graph;
	private final AnnotatedTopology topology;
	private final boolean collapse;
	
	public TaskGraph(AnnotatedTopology topology) {
		this(topology, true, false, false);
	}
	
	public TaskGraph(AnnotatedTopology topology, boolean collapse, boolean horizontal, boolean subgraphs) {
		this.topology = topology;
		this.graph = new PTGraph<ProcessIteration, CouplingInstance>(true);
		this.collapse = collapse;
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
				System.out.print("After " + i + " iterations, processing node " + pi);
				AnnotatedScale sc = pi.getInstance().getTimescaleInstance();
				if (sc != null) {
					System.out.println(", which is at " + ((long)(1000l*pi.getIteration() / (double)pi.getInstance().getTimescaleInstance().getSteps()))/10d + "% of its computation.");
				}
				else {
					System.out.println();
				}
			}
			// Only add to graph if it hasn't been added before
			if (pi.isSingle()) this.graph.addNode(pi);
			
			boolean complete = pi.instanceCompleted();
			// There are only couplings out if the operator can send
			if (pi.isSending()) {
				boolean hasNext = this.computeNextIteration(state, pi);

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
	private boolean computeNextIteration(TaskGraphState state, ProcessIteration pi) {
		Collection<AnnotatedCoupling> couplingsFrom = this.topology.getCouplingsFrom(pi.getInstance(), pi.getOperator());
		AnnotatedCoupling[] cds = new AnnotatedCoupling[couplingsFrom.size()];
		couplingsFrom.toArray(cds);
		
		for (AnnotatedCoupling cd : cds) {
			boolean needInit = (pi.firstLoop() && topology.needsExternalInitialization(cd.getTo().getInstance()));

			this.calculateTo(pi, cd, state, needInit);
		}
		
		return cds.length > 0;
	}
	
	/** Create an instance of a normal coupling between one process iteration the next */
	private void calculateTo(ProcessIteration from, AnnotatedCoupling cd, TaskGraphState state, boolean needInit) {
		CouplingInstance ci;
		SEL nextOp = cd.getTo().getPort().getOperator();
		if (needInit || nextOp == SEL.FINIT) {
			ci = state.tryStateful(cd.getTo().getInstance());
			if (ci != null) this.addToGraph(ci, state);
		}
		
		ci = from.calculateCouplingInstance(cd, needInit);
		this.addToGraph(ci, state);
	}

	/** Create an instance of a stateful coupling between one process iteration and the next */
	private void activateStep(TaskGraphState state, ProcessIteration pi) {
		ProcessIteration pnext = pi.nextStep(this.collapse);
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
	
	public static List<ProcessIteration> descriptionToIteration(List<AnnotatedInstance> pds) {
		ArrayList<ProcessIteration> pis = new ArrayList<ProcessIteration>(pds.size());
		for (AnnotatedInstance pd : pds) {
			pis.add(new ProcessIteration(pd));
		}
		return pis;
	}
}
