package eu.mapperproject.xmml.topology.algorithms;

import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.util.graph.Edge;

/**
 * Represents an actual data transfer over a coupling from one processiteration to the other.
 * @author Joris Borgdorff
 *
 */
public class CouplingInstance<T> implements Edge<T> {
	private T from;
	private T to;
	private Coupling coupling;
	
	/** Create an instance of a coupling between one process iteration and another */
	public CouplingInstance(T from, T to, Coupling cd) {
		this.coupling = cd;
		this.from = from;
		this.to = to;
	}
	
	/** Create an instance of a state coupling between one process iteration the next */
	public CouplingInstance(T from, T to) {
		this(from, to, null);
	}
	
	public boolean isState() {
		return this.coupling == null;
	}
	
	public Coupling getCoupling() {
		return this.coupling;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || this.getClass().equals(o.getClass())) return false;
		CouplingInstance ci = (CouplingInstance)o;
		
		return this.from.equals(ci.from) && this.to.equals(ci.to) && this.coupling.equals(ci.coupling);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 37 * hash + (this.from != null ? this.from.hashCode() : 0);
		hash = 37 * hash + (this.to != null ? this.to.hashCode() : 0);
		hash = 37 * hash + (this.coupling != null ? this.coupling.hashCode() : 0);
		return hash;
	}
	
	@Override
	public T getFrom() {
		return this.from;
	}

	@Override
	public T getTo() {
		return this.to;
	}

	@Override
	public String toString() {
		return this.from + " -> " + this.to;
	}
}
