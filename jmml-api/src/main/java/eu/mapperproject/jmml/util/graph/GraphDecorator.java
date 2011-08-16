package eu.mapperproject.jmml.util.graph;

import eu.mapperproject.jmml.specification.graph.Edge;
import java.util.Map;

/**
 * An interface for a class that can decorate nodes and edges
 * @author Joris Borgdorff
 *
 */
public abstract class GraphDecorator<T,E extends Edge<T>> implements Categorizer<T> {
	private final boolean isDirected;

	/** Create a GraphDecorator.
	 * The resulting graph will be directed based on the given boolean.
	 * @param isDirected whether the output graph will be directed
	 */
	protected GraphDecorator(boolean isDirected) {
		this.isDirected = isDirected;
	}

	/** Whether the output graph will be directed. */
	public final boolean isDirected() {
		return this.isDirected;
	}

	/**
	 * Decorate given node to a StyledNode.
	 * May throw a NullPointerException if the given node is null.
	 * @throws NullPointerException if the given node is null
	 */
	public abstract StyledNode decorateNode(T node);

	/**
	 * Creates a StyledEdge out of the given edge, where the from and to nodes
	 * are already converted to StyledNodes
	 * @param edge edge to convert
	 * @param from from node of the edge converted to a StyledNode
	 * @param to to node of the edge converted to a StyledNode
	 * @throws NullPointerException if given edge is null
	 */
	protected abstract StyledEdge decorateEdge(E edge, StyledNode from, StyledNode to);

	/**
	 * Decorate given edge to a StyledEdge.
	 * A map of nodes may be supplied which contains all converted from and to nodes.
	 * @param edge edge to convert to StyledEdge
	 * @param nodes a map of previously instantiated nodes, null if not used
	 * @throws NullPointerException if given edge is null
	 */
	public StyledEdge decorateEdge(E edge, Map<T, StyledNode> nodes) {
		final T from = edge.getFrom(), to = edge.getTo();
		StyledNode sfrom, sto;
		if(nodes == null) {
			sfrom = from == null ? this.decorateMissingNode(null) : this.decorateNode(from);
			sto =   to == null   ? this.decorateMissingNode(null) : this.decorateNode(to);
		}
		else {
			sfrom = nodes.get(from);
			sto   = nodes.get(to);

			if (sfrom == null) sfrom = this.decorateMissingNode(from);
			if (sto   == null) sto   = this.decorateMissingNode(to);
		}
		return decorateEdge(edge, sfrom, sto);
	}

	/**
	 * Create a StyledEdge from given node to the sink node, if applicable.
	 * @param node node which needs an edge to a sink
	 * @param snode StyledNode version of the same node
	 * @return StyledEdge if node needs sink, null otherwise
	 */
	public StyledEdge addSinkEdge(T node, StyledNode snode) {
		return null;
	}

	/**
	 * Create a StyledEdge from the source node to given node, if applicable.
	 * @param node node which needs an edge to a sink
	 * @param snode StyledNode version of the same node
	 * @return StyledEdge if node needs source, null otherwise
	 */
	public StyledEdge addSourceEdge(T node, StyledNode snode) {
		return null;
	}

	/**
	 * Create a StyledEdge from the source node to given node, if applicable.
	 * @param node node which needs an edge to a sink
	 * @param snode StyledNode version of the same node
	 * @return StyledEdge if node needs source, null otherwise
	 */
	public StyledNode decorateMissingNode(T node) {
		return null;
	}
}
