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
	private final Category category;

	/** Create a node with an underlying object
	 * @param name
	 * @param style
	 */
	public NodeDecorator(T object, String name, String style, Category category) {
		super(name, style);
		this.object = object;
		this.category = category;
	}

	/**
	 * Get the object that is decorated
	 * @return the object
	 */
	public T getObject() {
		return this.object;
	}

	@Override
	public Category getCategory() {
		return category;
	}
}
