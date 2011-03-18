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
public abstract class GraphDecorator<T,E extends Edge<T>> implements Categorizer<T,E> {
	private final boolean isDirected;

	protected GraphDecorator(boolean isDirected) {
		this.isDirected = isDirected;
	}
	
	public final boolean isDirected() {
		return this.isDirected;
	}

	public abstract StyledNode decorateNode(T node);
	protected abstract StyledEdge decorateEdge(E edge, StyledNode from, StyledNode to);


	public StyledEdge decorateEdge(E edge, Map<T, StyledNode> nodes) {
		StyledNode from, to;
		if (nodes == null) {
			from = this.decorateNode(edge.getFrom());
			to = this.decorateNode(edge.getTo());
		}
		else {
			from = nodes.get(edge.getFrom());
			to = nodes.get(edge.getTo());
		}
		return decorateEdge(edge, from, to);
	}

	public StyledEdge addSinkEdge(T node, StyledNode snode) {
		return null;
	}

	public StyledEdge addSourceEdge(T node, StyledNode snode) {
		return null;
	}

	public StyledNode decorateMissingNode(T node) {
		return null;
	}

	@Override
	public Category categorizeEdge(E edge) {
		return Tree.getCommonAncestor(categorize(edge.getFrom()), categorize(edge.getTo()));
	}
}
