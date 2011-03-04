/**
 * 
 */
package eu.mapperproject.xmml.util.graph;

/**
 * Decorates an object as an edge
 * @author Joris Borgdorff
 */
public class EdgeDecorator<T> extends SimpleStyledEdge {

	private final T object;
	private final String style;
	private final String label;

	/**
	 * Create an edge decorator of an object
	 * @param from
	 * @param to
	 */
	public EdgeDecorator(T object, String style, String label, StyledNode from, StyledNode to) {
		super(from, to);
		this.object = object;
		this.label = label;
		this.style = style;
	}

	/**
	 * Get the object that is decorated
	 * @return the object
	 */
	public T getObject() {
		return object;
	}

	@Override
	public String getStyle() {
		return this.style;
	}

	@Override
	public String getLabel() {
		return label;
	}
}
