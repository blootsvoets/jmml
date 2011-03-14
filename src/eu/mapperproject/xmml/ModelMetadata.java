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

	@Override
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

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		return this.id.equals(((ModelMetadata)o).id);
	}

	@Override
	public int hashCode() {
		return (this.id != null ? this.id.hashCode() : 0);
	}

	@Override
	public boolean deepEquals(Object o) {
		if (!this.equals(o)) return false;
		ModelMetadata md = (ModelMetadata)o;
		return this.name.equals(md.name)
				&& ((this.description == null && md.description == null) || this.description.equals(md.description))
				&& ((this.version == null && md.version == null) || this.version.equals(md.version));
	}
}
