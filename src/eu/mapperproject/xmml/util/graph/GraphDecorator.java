/**
 * 
 */
package eu.mapperproject.xmml.util.graph;

import java.util.Map;

/**
 * An interface for a class that can decorate nodes and edges
 * @author Joris Borgdorff
 *
 */
public interface GraphDecorator<T,E extends Edge<T>> {
	public boolean isDirected();
	public StyledNode decorateNode(T node);
	public StyledEdge addSinkEdge(T node, StyledNode snode);
	public StyledEdge addSourceEdge(T node, StyledNode snode);
	public StyledEdge decorateEdge(E edge, Map<T, StyledNode> nodes);
	public StyledNode decorateMissingNode(T node);
}
