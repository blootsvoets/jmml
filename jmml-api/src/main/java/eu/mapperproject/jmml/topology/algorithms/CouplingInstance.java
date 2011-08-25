package eu.mapperproject.jmml.topology.algorithms;

import eu.mapperproject.jmml.specification.annotated.AnnotatedCoupling;
import eu.mapperproject.jmml.util.graph.Edge;

/**
 * Represents an actual data transfer over a coupling from one processiteration to the other.
 * @author Joris Borgdorff
 *
 */
public class CouplingInstance implements Edge<ProcessIteration> {
	private final ProcessIteration from;
	private ProcessIteration to;
	private final AnnotatedCoupling coupling;
	private final int hashCode;
	
	/** Create an instance of a coupling between one process iteration and another */
	public CouplingInstance(ProcessIteration from, ProcessIteration to, AnnotatedCoupling cd) {
		if (from == null || to == null) {
			throw new IllegalArgumentException("Coupling instance may not go from or to null");
		}
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

	/**
	 * Whether this coupling instance represents a state transfered from one instance of a processiteration to the next
	 */
	public boolean isVirtual() {
		return this.coupling == null;
	}

	/**
	 * Whether this coupling instance represents a state transfered from one instance of a processiteration to the next
	 */
	public boolean isState() {
		return isVirtual() && !toSameInstance();
	}

	/**
	 * Whether this coupling instance represents a step from one operator of a processiteration to the next
	 */
	public boolean isStep() {
		return isVirtual() && toSameInstance();
	}

	/**
	 * Whether this coupling instance represents a step from one operator of a processiteration to the next
	 */
	public boolean toSameInstance() {
		return isVirtual() && from.getInstanceCounter() == to.getInstanceCounter();
	}

	/**
	 * Get the coupling associated to this coupling instance. If this couplinginstance
	 * represents a state or step null is returned.
	 */
	public AnnotatedCoupling getCoupling() {
		return this.coupling;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CouplingInstance ci = (CouplingInstance)o;
		
		return this.from.equals(ci.from)
			&& this.to.equals(ci.to)
			&& (this.coupling == null ? ci.coupling == null : this.coupling.equals(ci.coupling));
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	/** Pre-compute a hash code */
	private int computeHashCode() {
		int hash = 7;
		hash = 17 * hash + this.from.hashCode();
		hash = 17 * hash + this.to.hashCode();
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

	/**
	 * Set the receiving end of the coupling
	 * @param p Receiving processiteration
	 * @throws IllegalArgumentException if p is null
	 */
	public void setTo(ProcessIteration p) {
		if (p == null) {
			throw new IllegalArgumentException("Receiving end of coupling instance " + this + " may not be set to null");
		}
		this.to = p;
	}

	@Override
	public String toString() {
		return this.from + " -> " + this.to;
	}
}
