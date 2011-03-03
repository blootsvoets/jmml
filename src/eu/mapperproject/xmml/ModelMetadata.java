package eu.mapperproject.xmml;

import eu.mapperproject.xmml.util.Version;

/**
 * Stores the metadata of a model
 * @author Joris Borgdorff
 *
 */
public class ModelMetadata implements Identifiable {
	private final String id;
	private final String name;
	private final String description;
	private final Version version;
	
	public ModelMetadata(String id, String name, String description, Version version) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.version = version;
	}

	/**
	 * @return the id of the model
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the version
	 */
	public Version getVersion() {
		return version;
	}
}
