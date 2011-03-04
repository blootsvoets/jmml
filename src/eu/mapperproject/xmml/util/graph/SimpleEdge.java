package eu.mapperproject.xmml.util.graph;

/**
 * The simplest implementation of an Edge
 * @author Joris Borgdorff
 *
 */
public class SimpleEdge<T> implements Edge<T>, Categorizable {
	private final T from, to;
	private final Category category;
	
	public SimpleEdge(T from, T to, Category category) {
		this.from = from;
		this.to = to;
		this.category = category;
	}
	
	@Override
	public T getFrom() {
		return from;
	}

	@Override
	public T getTo() {
		return to;
	}
	
	@Override
	public Category getCategory() {
		return this.category;
	}
}
