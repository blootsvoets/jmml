package eu.mapperproject.jmml.specification.graph;

/**
 * An interface for uniquely numbered classes.
 * @author Joris Borgdorff
 */
public interface Numbered extends Identifiable {
	/** Get the number of the current class */
	public int getNumber();
}
