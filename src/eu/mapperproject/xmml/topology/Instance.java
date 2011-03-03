package eu.mapperproject.xmml.topology;

import eu.mapperproject.xmml.Identifiable;
import eu.mapperproject.xmml.definitions.ScaleMap;
import eu.mapperproject.xmml.definitions.Submodel;

/**
 * An instance of a submodel, with its own id, domain and scales
 * @author Joris Borgdorff
 *
 */
public class Instance implements Identifiable {

	private final String id;
	private final Submodel submodel;
	private final String domain;
	private final boolean initial;
	private final ScaleMap scales;

	/**
	 * @param id
	 * @param submodel
	 * @param domain
	 * @param initial
	 * @param scales
	 */
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

}
