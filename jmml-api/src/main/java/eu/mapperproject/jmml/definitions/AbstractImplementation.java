package eu.mapperproject.jmml.definitions;

import eu.mapperproject.jmml.Identifiable;
import eu.mapperproject.jmml.ModelMetadata;

/**
 *
 * @author Joris Borgdorff
 */
public abstract class AbstractImplementation implements Identifiable {
	protected final ModelMetadata meta;
	
	public AbstractImplementation(ModelMetadata meta) {
		this.meta = meta;
	}
	
	@Override
	public String getId() {
		return meta.getId();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		return this.meta.equals(((Submodel)o).meta);
	}
	
	@Override
	public int hashCode() {
		return this.meta.hashCode();
	}
}
