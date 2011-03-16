/**
 * 
 */
package eu.mapperproject.xmml.util.graph;

/**
 * A node decoration of an object
 * @author Joris Borgdorff
 * @param <T> type of object that gets decorated
 */
public class NodeDecorator<T> extends SimpleNode {

	private final T object;

	/** Create a node with an underlying object
	 * @param name
	 * @param style
	 */
	public NodeDecorator(T object, String name, String style, Category category) {
		super(name, style, category);
		this.object = object;
	}

	/**
	 * Get the object that is decorated
	 * @return the object
	 */
	public T getObject() {
		return this.object;
	}
}
