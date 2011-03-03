package eu.mapperproject.xmml.topology.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.mapperproject.xmml.topology.algorithms.CouplingDescription.CouplingType;
import eu.mapperproject.xmml.topology.algorithms.util.PTList;

public class TaskGraphState implements Iterable<ProcessIteration> {
	private List<ProcessIteration> activeProcesses;
	private Set<ProcessIteration> visited;
	private Map<ProcessIteration,Set<CouplingDescription>> snoozingProcesses;
	private Map<ProcessReference,ProcessIteration> states;
	private ModelDescription model;

	public TaskGraphState(ModelDescription desc) {
		states = new HashMap<ProcessReference,ProcessIteration>();
		activeProcesses = new ArrayList<ProcessIteration>();
		snoozingProcesses = new HashMap<ProcessIteration,Set<CouplingDescription>>();
		visited = new HashSet<ProcessIteration>();
		this.model = desc;
	}
	
	public ProcessIteration activate(CouplingInstance ci) {
		ProcessIteration to = ci.getTo();
		if (to != null) {
			if (this.visited.contains(to)) {
				//throw new IllegalStateException("Revisiting pi " + ci);
			}
			CouplingDescription cd = ci.getDescription();
			Set<CouplingDescription> cis = PTList.getSet(to, snoozingProcesses);
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
		}
		return null;
	}
	
	private boolean hasAllCouplings(ProcessIteration pi, Set<CouplingDescription> cds, boolean print) {
		CouplingType receivingType = pi.receivingType();
		
		if (receivingType != null) {
			if ((pi.needsState() || (!pi.firstIteration() &&
					   !model.initInIteration(pi.getDescription()))) 
				&& !cds.contains(null)) {
				if (print) System.out.println(pi + " is missing silent step");
				return false;
			}
			else if (pi.instanceCompleted() && model.initInIteration(pi.getDescription())) {
				return true;
			}
				
			for (CouplingDescription cd : model.toCouplingMatching(pi.getDescription(), receivingType)) {
				if (!cds.contains(cd)) {
					if (print) System.out.println(pi + " is missing: " + cd);
					return false;
				}
			}
		}
		
		return true;
	}
	
	public CouplingInstance initInstance(CouplingDescription cd) {
		CouplingInstance ci = null;
		if (cd.getTo().getDescription().stateful()) {
			ProcessIteration prev = states.remove(cd.getTo());
			if (prev != null) {
				ProcessIteration next = prev.nextInstance(null);
				ci = new CouplingInstance(prev, next, null, null);
				activate(ci);
			}
		}
		
		return ci;
	}
	
	public void printDeadlock() {
		if (this.activeProcesses.isEmpty() && !this.snoozingProcesses.isEmpty()) {
			System.out.println("Deadlock:");
			for (Map.Entry<ProcessIteration, Set<CouplingDescription>> m : snoozingProcesses.entrySet()) {
				hasAllCouplings(m.getKey(), m.getValue(), true);
			}
		}
	}
	
	public void addState(ProcessIteration pi) {
		this.states.put(pi.getDescription(), pi);
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
