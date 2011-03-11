package eu.mapperproject.xmml.topology.algorithms;

import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.util.graph.Edge;

/**
 * Represents an actual data transfer over a coupling from one processiteration to the other.
 * @author Joris Borgdorff
 *
 */
public class CouplingInstance implements Edge<ProcessIteration> {
	private final ProcessIteration from;
	private ProcessIteration to;
	private final Coupling coupling;
	private final int hashCode;
	
	/** Create an instance of a coupling between one process iteration and another */
	public CouplingInstance(ProcessIteration from, ProcessIteration to, Coupling cd) {
		this.coupling = cd;
		this.from = from;
		this.to = to;
		// Pre-compute as an optimization
		this.hashCode = this.computeHashCode();
	}
	
	/** Create an instance of a state coupling between one process iteration the next */
	public CouplingInstance(ProcessIteration from, ProcessIteration to) {
		this(from, to, null);
	}
	
	public boolean isState() {
		return this.coupling == null && from.getInstance().equals(to.getInstance()) && from.getInstanceCounter() + 1 == to.getInstanceCounter();
	}

	public boolean isStep() {
		return this.coupling == null && from.getInstance().equals(to.getInstance()) && from.getInstanceCounter() == to.getInstanceCounter();
	}

	public Coupling getCoupling() {
		return this.coupling;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || !this.getClass().equals(o.getClass())) return false;
		CouplingInstance ci = (CouplingInstance)o;
		
		return this.from.equals(ci.from) && this.to.equals(ci.to) && ((this.coupling == null && ci.coupling == null) || this.coupling.equals(ci.coupling));
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	/** Pre-compute a hash code */
	private int computeHashCode() {
		int hash = 7;
		hash = 17 * hash + (this.from != null ? this.from.hashCode() : 0);
		hash = 17 * hash + (this.to != null ? this.to.hashCode() : 0);
		hash = 17 * hash + (this.coupling != null ? this.coupling.hashCode() : 0);
		return hash;
	}
	
	@Override
	public ProcessIteration getFrom() {
		return this.from;
	}

	@Override
	public ProcessIteration getTo() {
		return this.to;
	}

	/** Set the receiving end of the coupling */
	public void setTo(ProcessIteration p) {
		this.to = p;
	}

	@Override
	public String toString() {
		return this.from + " -> " + this.to;
	}
}
