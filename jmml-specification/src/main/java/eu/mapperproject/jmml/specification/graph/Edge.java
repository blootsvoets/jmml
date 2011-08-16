/**
 * 
 */
package eu.mapperproject.jmml.specification.graph;

/**
 * A basic edge of a graph
 * @author Joris Borgdorff
 */
public interface Edge<T> {
	/** Get the tail node of the edge */
	public T getFrom();

	/** Get the head node of the edge */
	public T getTo();
}
