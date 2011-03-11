package eu.mapperproject.xmml.topology;

import java.util.List;

import eu.mapperproject.xmml.definitions.Submodel.SEL;
import eu.mapperproject.xmml.util.graph.Edge;

/**
 * A coupling from one instance port to another
 * @author Joris Borgdorff
 *
 */
public class Coupling implements Domainable, Edge<Instance> {
	private final String name;
	private final InstanceOperator from;
	private final InstanceOperator to;
	private final List<Filter> filters;

	/**
	 * @param name
	 * @param from
	 * @param to
	 * @param filters
	 */
	public Coupling(String name, InstancePort from, InstancePort to,
			List<Filter> filters) {
		this(name, from.getInstanceOperator(), to.getInstanceOperator(), filters);
	}

	/**
	 * @param name
	 * @param from
	 * @param to
	 * @param filters
	 */
	public Coupling(String name, InstanceOperator from, InstanceOperator to,
			List<Filter> filters) {
		this.name = name;
		this.from = from;
		this.to = to;
		this.filters = filters;
	}
	
	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.topology.Domainable#getDomain()
	 */
	@Override
	public Domain getDomain() {
		return Domain.getDomain(from.getInstance(), to.getInstance());
	}

	/** Return the instance where the coupling comes from */ 
	@Override
	public Instance getFrom() {
		return from.getInstance();
	}

	/** Return the id of the instance where the coupling comes from */ 
	public String getFromId() {
		return from.getInstance().getId();
	}

	/** Return the instance where the coupling goes to */ 
	@Override
	public Instance getTo() {
		return to.getInstance();
	}
	
	/** Return the instance where the coupling goes to */ 
	public String getToId() {
		return to.getInstance().getId();
	}	

	/** Get the port of the receiving end */
	public InstanceOperator getToOperator() {
		return to;
	}

	/** Get the port of the receiving end */
	public InstanceOperator getFromOperator() {
		return from;
	}

	/** Returns a copy of this coupling with a different receiving operator. */
	public Coupling copyWithToOperator(SEL op) {
		return new Coupling(this.name, this.from, new InstanceOperator(this.to.getInstance(), op), this.filters);
	}
	
	/** Get the name of the coupling.
	 * If the name was not specified this returns null */
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return from + " -> " + to; 
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !this.getClass().equals(o.getClass())) return false;
		Coupling c = (Coupling)o;
		return ((this.name == null && c.name == null) || this.name.equals(c.name)) && this.from.equals(c.from) && this.to.equals(c.to);
	}
	
	@Override
	public int hashCode() {
		int hashCode = this.name == null ? 0 : this.name.hashCode();
		hashCode = 31 * hashCode + this.from.hashCode();
		hashCode = 31 * hashCode + this.to.hashCode();
		return hashCode;
	}
	
	/**
	 * Get all filters applied to this coupling, in order
	 */
	public List<Filter> getFilters() {
		return this.filters;
	}
}
