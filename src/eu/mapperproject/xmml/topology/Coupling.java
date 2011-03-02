package eu.mapperproject.xmml.topology;

import java.util.List;

/**
 * A coupling from one instance port to another
 * @author Joris Borgdorff
 *
 */
public class Coupling {

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

}
