/**
 * 
 */
package eu.mapperproject.jmml.util.graph;

/**
 * Decorates an object as an edge
 * @author Joris Borgdorff
 */
public class EdgeDecorator<T> extends AnnotatedStyledEdge {
	private final T object;

	/**
	 * Create an edge decorator of an object
	 * @param from
	 * @param to
	 */
	public EdgeDecorator(T object, StyledNode from, StyledNode to, String style, String label) {
		super(from, to, style, label);
		this.object = object;
	}

	/**
	 * Get the object that is decorated
	 * @return the object
	 */
	public T getObject() {
		return object;
	}
}
