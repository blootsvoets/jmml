package eu.mapperproject.xmml.topology;

import java.util.List;

import eu.mapperproject.xmml.definitions.Port;
import eu.mapperproject.xmml.util.graph.Category;
import eu.mapperproject.xmml.util.graph.Edge;

/**
 * A coupling from one instance port to another
 * @author Joris Borgdorff
 *
 */
public class Coupling implements Domainable, Edge<Instance> {

	private final String name;
	private final InstancePort from;
	private final InstancePort to;
	private final List<Filter> filters;

	/**
	 * @param name
	 * @param from
	 * @param to
	 * @param filters
	 */
	public Coupling(String name, InstancePort from, InstancePort to,
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
	public Instance getFrom() {
		return from.getInstance();
	}

	/** Return the instance where the coupling goes to */ 
	public Instance getTo() {
		return to.getInstance();
	}
	
	/** Get the port of the sending end */
	public Port getFromPort() {
		return from.getPort();
	}

	/** Get the port of the receiving end */
	public Port getToPort() {
		return from.getPort();
	}
	
	/** Get the name of the coupling.
	 * If the name was not specified this returns null */
	public String getName() {
		return this.name;
	}

	@Override
	public Category getCategory() {
		return new Category(this.getDomain());
	}
}
