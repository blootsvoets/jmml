package eu.mapperproject.jmml.topology.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.mapperproject.jmml.definitions.Submodel.SEL;
import eu.mapperproject.jmml.topology.Coupling;
import eu.mapperproject.jmml.topology.CouplingTopology;
import eu.mapperproject.jmml.topology.Instance;
import eu.mapperproject.jmml.topology.InstanceOperator;
import eu.mapperproject.jmml.util.PTList;

import java.util.TreeMap;

/**
 * Keeps track of the current state in a task graph.
 * This state involves having active processiterations that can spawn new processiterations and snoozing
 * processiterations which have not received all data necessary to do their computation.
 * 
 * @author Joris Borgdorff
 *
 */
public class TaskGraphState implements Iterable<ProcessIteration> {
	private final List<ProcessIteration> activeProcesses;
	private final Map<ProcessIteration,Collection<Coupling>> snoozingProcesses;
	private final Map<Instance,ProcessIteration> states;
	private final CouplingTopology topology;

	public TaskGraphState(CouplingTopology desc) {
		this.states = new TreeMap<Instance,ProcessIteration>();
		this.activeProcesses = new ArrayList<ProcessIteration>();
		this.snoozingProcesses = new HashMap<ProcessIteration,Collection<Coupling>>();
		this.topology = desc;
	}
		
	/** Activate a processiteration which does not require any input, if possible */
	public void activate(ProcessIteration init) {
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
	public CouplingInstance tryStateful(Instance inst) {
		CouplingInstance ci = null;
		if (inst.getSubmodel().isStateful()) {
			ProcessIteration prev = states.remove(inst);
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
	public Collection<ProcessIteration> printDeadlock() {
		if (this.activeProcesses.isEmpty() && !this.snoozingProcesses.isEmpty()) {
			System.out.println("Deadlock:");
			for (Map.Entry<ProcessIteration, Collection<Coupling>> m : snoozingProcesses.entrySet()) {
				hasAllCouplings(m.getKey(), m.getValue(), true);
			}
			return snoozingProcesses.keySet();
		}
		return null;
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
		Collection<Coupling> cis = PTList.getSet(pi, snoozingProcesses);
		if (!cis.contains(next)) {
			cis.add(next);
			if (hasAllCouplings(pi, cis, false)) {
				snoozingProcesses.remove(pi);
				activeProcesses.add(pi);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Whether all incoming couplings of given process have been fulfilled
	 */
	private boolean hasAllCouplings(ProcessIteration pi, Collection<Coupling> cds, boolean print) {
		SEL op = pi.getOperator();
		
		if (op.isReceiving()) {
			// State or operator step
			if ((pi.needsState() || !pi.initializing()) 
				&& !cds.contains(null)) {
				if (print) {
					System.out.println(pi + " is missing state");
					pi.setDeadlock();
				}
				return false;
			}
			else if (pi.instanceCompleted()) {
				return true;
			}
			
			Instance inst = pi.getInstance();
			boolean needInit = topology.needsInitInstances(inst);
			Collection<Coupling> cs;
			if (needInit && pi.initializing()) {
				cs = topology.needsInitCouplings(inst);
			}
			else {
				cs = topology.getTo(new InstanceOperator(inst, op));
			}
			for (Coupling cd : cs) {
				if (!cds.contains(cd)) {
					if (print) {
						System.out.println(pi + " is missing: " + cd);
						pi.setDeadlock();
					}
					return false;
				}
			}
		}
		
		return true;
	}

	/** Removes a process that is finalizing and can not get additional input from
	 * snoozingprocesses and returns it. Returns null if none is found.
	 */
	private ProcessIteration removeFinalProcess() {
		Iterator<ProcessIteration> i = snoozingProcesses.keySet().iterator();
		while (i.hasNext()) {
			ProcessIteration pi = i.next();
			boolean needInit = topology.needsInitInstances(pi.getInstance());
			if (needInit && pi.finalLoop()) {
				i.remove();
				return pi;
			}
		}
		return null;
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
		private ProcessIteration finalProcess;

		@Override
		public boolean hasNext() {
			if (!activeProcesses.isEmpty()) {
				return true;
			}
			this.finalProcess = removeFinalProcess();
			return this.finalProcess != null;
		}
		
		@Override
		public ProcessIteration next() {
			ProcessIteration pi;
			if (this.finalProcess != null) {
				pi = this.finalProcess;
				this.finalProcess = null;
			}
			else {
				pi = activeProcesses.remove(activeProcesses.size() - 1);
			}

			return pi;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException("Remove not supported");
		}
	}
}
