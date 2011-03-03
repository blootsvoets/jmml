package eu.mapperproject.xmml.topology;

import eu.mapperproject.xmml.Identifiable;
import eu.mapperproject.xmml.definitions.ScaleMap;
import eu.mapperproject.xmml.definitions.Submodel;

/**
 * An instance of a submodel, with its own id, domain and scales
 * @author Joris Borgdorff
 *
 */
public class Instance implements Identifiable, Domainable {

	private final String id;
	private final Submodel submodel;
	private final Domain domain;
	private boolean initial;
	private boolean isfinal;
	private final ScaleMap scales;

	public Instance(String id, Submodel submodel, String domain,
			boolean initial, ScaleMap scales) {
		this.id = id;
		this.submodel = submodel;
		this.domain = domain;
		this.initial = initial;
		this.scales = scales;
	}

	/**
	 * @return submodel of which this is an instance
	 */
	public Submodel getSubmodel() {
		return this.submodel;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.topology.Domainable#getDomain()
	 */
	@Override
	public Domain getDomain() {
		return this.domain;
	}

	/**
	 * Whether the instance will be among the ones that are initially active
	 */
	public boolean isInitial() {
		return initial;
	}

	/**
	 * Whether the instance will be among the final ones to exit
	 */
	public boolean isFinal() {
		return isfinal;
	}
}
