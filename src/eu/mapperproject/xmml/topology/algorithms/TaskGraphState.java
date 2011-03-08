package eu.mapperproject.xmml.topology.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.mapperproject.xmml.definitions.Submodel.SEL;
import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.topology.CouplingTopology;
import eu.mapperproject.xmml.topology.Instance;
import eu.mapperproject.xmml.topology.InstanceOperator;
import eu.mapperproject.xmml.util.PTList;

/**
 * Keeps track of the current state in a task graph.
 * This state involves having active processiterations that can spawn new processiterations and snoozing
 * processiterations which have not received all data necessary to do their computation.
 * 
 * @author Joris Borgdorff
 *
 */
public class TaskGraphState implements Iterable<ProcessIteration> {
	private List<ProcessIteration> activeProcesses;
	private Set<ProcessIteration> visited;
	private Map<ProcessIteration,Set<Coupling>> snoozingProcesses;
	private Map<Instance,ProcessIteration> states;
	private CouplingTopology topology;

	public TaskGraphState(CouplingTopology desc) {
		this.states = new HashMap<Instance,ProcessIteration>();
		this.activeProcesses = new ArrayList<ProcessIteration>();
		this.snoozingProcesses = new HashMap<ProcessIteration,Set<Coupling>>();
		this.visited = new HashSet<ProcessIteration>();
		this.topology = desc;
	}
		
	/** Activate a processiteration which does not require any input, if possible */
	public void activate(ProcessIteration init) {
		if (!init.isInitial()) {
			throw new IllegalArgumentException("May only activate initial process iterations directly, not " + init);
		}

		this.activateIfNeeded(init, null);
	}
		
	/** Activate a coupling instance by generating the receiving end and calculating and adding that to the process pool */
	public ProcessIteration activate(CouplingInstance ci) {
		ProcessIteration to = ci.getTo();
		if (to == null) {
			throw new IllegalArgumentException("To activate a next state with coupling instance " + ci + ", the next may not be null.");
		}
		return (activateIfNeeded(to, ci.getCoupling()) ? to : null);
	}
	
	/** Tries to initialize a processiteration from a stateful transition. If this succeeds, the
	 * coupling instance that facilitates this stateful transition is returned
	 */
	public CouplingInstance initInstance(Coupling cd) {
		CouplingInstance ci = null;
		if (cd.getTo().getSubmodel().isStateful()) {
			ProcessIteration prev = states.remove(cd.getTo());
			if (prev != null) {
				ProcessIteration next = prev.nextState();
				ci = new CouplingInstance(prev, next);
			}
		}
		
		return ci;
	}
	
	/**
	 * Prints all deadlocks that are present
	 */
	public void printDeadlock() {
		if (this.activeProcesses.isEmpty() && !this.snoozingProcesses.isEmpty()) {
			System.out.println("Deadlock:");
			for (Map.Entry<ProcessIteration, Set<Coupling>> m : snoozingProcesses.entrySet()) {
				hasAllCouplings(m.getKey(), m.getValue(), true);
			}
		}
	}
	
	/** Add the state of the given processiteration to the task graph state */
	public void addState(ProcessIteration pi) {
		this.states.put(pi.getInstance(), pi);
	}

	/**
	 * Activate given processiteration if all incoming couplings have been fulfilled
	 * @throws IllegalStateException if the processiteration has already been activated
	 */
	private boolean activateIfNeeded(ProcessIteration pi, Coupling next) {
		if (this.visited.contains(pi)) {
			throw new IllegalStateException("Revisiting process iteration " + pi + " in coupling " + next);
		}

		Set<Coupling> cis = PTList.getSet(pi, snoozingProcesses);
		if (!cis.contains(next)) {
			cis.add(next);
			if (hasAllCouplings(pi, cis, false)) {
				snoozingProcesses.remove(pi);
				this.visited.add(pi);
				activeProcesses.add(pi);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Whether all incoming couplings of given process have been fulfilled
	 */
	private boolean hasAllCouplings(ProcessIteration pi, Set<Coupling> cds, boolean print) {
		SEL receivingType = pi.receivingType();
		
		if (receivingType != null) {
			if ((pi.needsState() || !pi.initializing()) 
				&& !cds.contains(null)) {
				if (print) System.out.println(pi + " is missing silent step");
				return false;
			}
			else if (pi.instanceCompleted()) {
				return true;
			}
			
			Instance inst = pi.getInstance();
			Collection<Coupling> cs;
			if (pi.initializing() && topology.needsInitInstances(inst)) {
				cs = topology.needsInitCouplings(inst);
			}
			else {
				cs = topology.getTo(new InstanceOperator(inst, receivingType));
			}
			for (Coupling cd : cs) {
				if (!cds.contains(cd)) {
					if (print) System.out.println(pi + " is missing: " + cd);
					return false;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public Iterator<ProcessIteration> iterator() {
		return new StateIterator();
	}
	
	@Override
	public String toString() {
		return "TaskGraphState " + this.activeProcesses.toString() + ":" + this.snoozingProcesses;
	}
	
	private class StateIterator implements Iterator<ProcessIteration> {
		@Override
		public boolean hasNext() {
			return !activeProcesses.isEmpty();
		}
		
		@Override
		public ProcessIteration next() {
			ProcessIteration pi = activeProcesses.remove(activeProcesses.size() - 1);
			//System.out.println("TaskGraphState(" + pi + ") " + activeProcesses.toString() + ":" + snoozingProcesses);
			return pi;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException("Remove not supported");
		}
	}
}
