package eu.mapperproject.jmml.util.graph;

/**
 * Can Categorize an object of type T.
 * @author Joris Borgdorff
 */
public interface Categorizer<T> {
	/** Return a non-null category based on some aspect of the object. */
	public Category categorize(T object);
}
