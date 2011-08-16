package eu.mapperproject.jmml.util.graph;

import eu.mapperproject.jmml.specification.graph.Edge;

/**
 * The simplest implementation of an Edge
 * @author Joris Borgdorff
 *
 */
public class SimpleEdge<T> implements Edge<T> {
	private final T from, to;
	
	public SimpleEdge(T from, T to) {
		this.from = from;
		this.to = to;
	}
	
	@Override
	public T getFrom() {
		return from;
	}

	@Override
	public T getTo() {
		return to;
	}
}
