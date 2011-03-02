package eu.mapperproject.xmml.topology;

import java.util.Map;

import eu.mapperproject.xmml.definitions.Scale;
import eu.mapperproject.xmml.definitions.Submodel;

public class Instance {

	private final String id;
	private final Submodel submodel;
	private final String domain;
	private final boolean initial;
	private final Map<String, Scale> scales;

	/**
	 * @param id
	 * @param submodel
	 * @param domain
	 * @param initial
	 * @param scales
	 */
	public Instance(String id, Submodel submodel, String domain,
			boolean initial, Map<String, Scale> scales) {
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
