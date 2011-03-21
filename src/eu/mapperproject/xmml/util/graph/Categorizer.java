package eu.mapperproject.xmml.util.graph;

/**
 * Can Categorize an object of type T.
 * @author jborgdo1
 */
public interface Categorizer<T> {
	/** Return a non-null category based on some aspect of the object. */
	public Category categorize(T object);
}
