package eu.mapperproject.xmml.util.graph;

/**
 * Can Categorize an object of type T.
 * @author jborgdo1
 */
public interface Categorizer<T,E extends Edge<T>> {
	/** Return a non-null category based on some aspect of the object. */
	public Category categorize(T object);

	/**
	 * Return a non-null category based on the nodes of an edge, returning
	 * the first common ancestor.
	 */
	public Category categorizeEdge(E edge);
}
