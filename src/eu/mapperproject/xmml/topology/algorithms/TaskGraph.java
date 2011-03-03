package eu.mapperproject.xmml.topology.algorithms;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import eu.mapperproject.xmml.topology.algorithms.CouplingDescription.CouplingType;
import eu.mapperproject.xmml.topology.algorithms.graph.PTGraph;

/** Describes the coupling topology of a model and can convert it to a task graph */ 
public class TaskGraph extends AbstractInstance<ModelDescription> {
	private final PTGraph graph;
	private final List<ProcessIteration> pis;
	private final Set<ModelComplexity> complexities;
	
	public TaskGraph(ModelDescription model) {
		this(model, false, false);
	}
	
	public TaskGraph(ModelDescription model, boolean horizontal, boolean subgraphs) {
		super(model, null);
		this.graph = new PTGraph(true);
		this.complexities = EnumSet.noneOf(ModelComplexity.class);
		this.pis = descriptionToIteration(model.getProcesses());
	}

	
	public enum ModelComplexity {
		PATH, TREE, ROOTED_DAG("rooted DAG"), DAG("DAG"), SYNC_DAG("synchronized DAG"), MERGE, MULTIPLICITY, LABEL_REMOVAL("label removal");
		private final String desc;
		
		ModelComplexity() {
			this.desc = this.name().replace('_', ' ').toLowerCase();
		}

		ModelComplexity(String desc) {
			this.desc = desc;
		}
		
		public String toString() {
			return "Model includes '" + this.desc + "'-type complexity.";
		}
	}

	public PTGraph getGraph() {
		if (this.graph.isEmpty()) {
			this.computeGraph();
		}
		
		return this.graph;
	}
	
	private void computeGraph() {
		TaskGraphState state = new TaskGraphState(this.desc);
		
		// Initialize processes with no initialization
		for (ProcessIteration pi : pis) {
			if (this.desc.isInitial(pi.getDescription())) {
				this.activateStart(state, pi);
			}
		}
		
		// As long as there are active processes, continue building the graph
		for (ProcessIteration pi : state) {
			boolean complete = pi.instanceCompleted();
			if (pi.getCouplingType().canSend(complete)) {		
				boolean hasNext = this.computeNextIteration(state, pi, complete);
					
				if (complete && !hasNext) {
					this.activateEnd(state, pi);
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
		CouplingType ct = complete ? CouplingType.OF : CouplingType.OI;
		List<CouplingDescription> cds = this.desc.fromCouplingsMatching(pi.getDescription(), ct);
		
		for (CouplingDescription cd : cds) {
			List<CouplingInstance> cis = CouplingInstance.calculateTo(pi, cd, state);
			this.activate(state, cis);
		}
		
		return !cds.isEmpty();
	}
	
	private void activateStart(TaskGraphState state, ProcessIteration pi) {
		this.activate(state, new CouplingInstance(null, pi, null, null));
	}
	
	private void activateStep(TaskGraphState state, ProcessIteration pi) {
		ProcessIteration pnext = pi.nextIteration(null);
		if (pnext != null) this.activate(state, new CouplingInstance(pi, pnext, null, null));		
	}
	
	private void activateEnd(TaskGraphState state, ProcessIteration pi) {
		this.activate(state, new CouplingInstance(pi, null, null, null));
	}

	private void activate(TaskGraphState state, CouplingInstance ci) {
		this.addComplexity(ci.getComplexity());

		ProcessIteration active = state.activate(ci);
		if (active != null) this.graph.addNode(active);

		this.graph.addEdge(ci);
	}
	
	private void activate(TaskGraphState state, List<CouplingInstance> cis) {
		for (CouplingInstance ci : cis) {
			this.activate(state, ci);
		}
	}
	
	private void addComplexity(ModelComplexity mc) {
		if (mc == null || this.complexities.contains(mc))
			return;
		
		this.complexities.add(mc);
		System.out.println(mc);
	}
	
	public static List<ProcessIteration> descriptionToIteration(List<ProcessReference> pds) {
		ArrayList<ProcessIteration> pis = new ArrayList<ProcessIteration>(pds.size());
		for (ProcessReference pd : pds) {
			pis.add(new ProcessIteration(pd));
		}
		return pis;
	}
}
