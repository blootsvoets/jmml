package eu.mapperproject.xmml.topology.algorithms;

import java.util.ArrayList;
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

public class TaskGraphState implements Iterable<ProcessIteration> {
	private List<ProcessIteration> activeProcesses;
	private Set<ProcessIteration> visited;
	private Map<ProcessIteration,Set<Coupling>> snoozingProcesses;
	private Map<Instance,ProcessIteration> states;
	private CouplingTopology model;

	public TaskGraphState(CouplingTopology desc) {
		states = new HashMap<Instance,ProcessIteration>();
		activeProcesses = new ArrayList<ProcessIteration>();
		snoozingProcesses = new HashMap<ProcessIteration,Set<Coupling>>();
		visited = new HashSet<ProcessIteration>();
		this.model = desc;
	}
	
	public void activate(ProcessIteration to) {
		if (!to.isInitial()) {
			throw new IllegalArgumentException("May only activate initial process iterations directly, not " + to);
		}
		if (this.visited.contains(to)) {
			throw new IllegalStateException("Revisiting pi " + to);
		}

		Set<Coupling> cis = PTList.getSet(to, snoozingProcesses);
		if (!cis.contains(null)) {
			cis.add(null);
			if (hasAllCouplings(to, cis, false)) {
				snoozingProcesses.remove(to);
				this.visited.add(to);
				activeProcesses.add(to);
			}
		}
	}
	
	public ProcessIteration activate(CouplingInstance ci) {
		ProcessIteration to = ci.getTo();
		if (to == null) {
			throw new IllegalArgumentException("To activate a next state with coupling instance " + ci + ", the next may not be null.");
		}
		if (this.visited.contains(to)) {
			throw new IllegalStateException("Revisiting pi " + ci);
		}

		Coupling cd = ci.getCoupling();
		Set<Coupling> cis = PTList.getSet(to, snoozingProcesses);
		if (!cis.contains(cd)) {
			cis.add(cd);
			if (hasAllCouplings(to, cis, false)) {
				snoozingProcesses.remove(to);
				this.visited.add(to);
				activeProcesses.add(to);
				System.out.println(this);
				return to;
			}
		}
		return null;
	}
	
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
				
			for (Coupling cd : model.getTo(new InstanceOperator(pi.getInstance(), receivingType))) {
				if (!cds.contains(cd)) {
					if (print) System.out.println(pi + " is missing: " + cd);
					return false;
				}
			}
		}
		
		return true;
	}
	
	public CouplingInstance initInstance(Coupling cd) {
		CouplingInstance ci = null;
		if (cd.getTo().getSubmodel().isStateful()) {
			ProcessIteration prev = states.remove(cd.getTo());
			if (prev != null) {
				ProcessIteration next = prev.nextInstance(null);
				ci = new CouplingInstance(prev, next);
				activate(ci);
			}
		}
		
		return ci;
	}
	
	public void printDeadlock() {
		if (this.activeProcesses.isEmpty() && !this.snoozingProcesses.isEmpty()) {
			System.out.println("Deadlock:");
			for (Map.Entry<ProcessIteration, Set<Coupling>> m : snoozingProcesses.entrySet()) {
				hasAllCouplings(m.getKey(), m.getValue(), true);
			}
		}
	}
	
	public void addState(ProcessIteration pi) {
		this.states.put(pi.getInstance(), pi);
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
			System.out.println("TaskGraphState " + activeProcesses.toString() + ":" + snoozingProcesses);
			return pi;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException("Remove not supported");
		}
	}
}
