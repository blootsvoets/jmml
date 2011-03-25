/**
 * 
 */
package eu.mapperproject.jmml.util.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Takes a graph and designs it with a given decoration
 * @author Joris Borgdorff
 *
 */
public class GraphDesigner<T, E extends Edge<T>> {
	private final GraphDecorator<T,E> decor;
	
	public GraphDesigner(GraphDecorator<T,E> decor) {
		this.decor = decor;
	}
	
	/** Design a set of nodes and edges such that they can be formed into a graph */
	public PTGraph<StyledNode,StyledEdge> decorate(Collection<T> ts, Collection<E> es) {
		Map<T, StyledNode> nodes = new HashMap<T, StyledNode>((ts.size() * 3) / 2);
		PTGraph<StyledNode,StyledEdge> graph = new PTGraph<StyledNode,StyledEdge>(this.decor.isDirected());
		this.decorateNodes(ts, graph, nodes);
		this.decorateEdges(es, graph, nodes);
		return graph;
	}

	/** Decorate given nodes and add them to the graph and nodes list */
	protected void decorateNodes(Collection<T> ts, PTGraph<StyledNode, StyledEdge> graph, Map<T, StyledNode> nodes) {
		for (T t : ts) {
			StyledNode n = this.decor.decorateNode(t);
			graph.addNode(n);
			nodes.put(t, n);

			StyledEdge e = this.decor.addSourceEdge(t, n);
			if (e != null) graph.addEdge(e);
			e = this.decor.addSinkEdge(t, n);
			if (e != null) graph.addEdge(e);
		}
	}

	/** Decorate given edges and add them to the graph */
	protected void decorateEdges(Collection<E> es, PTGraph<StyledNode,StyledEdge> graph, Map<T, StyledNode> nodes) {
		for (E e : es) {
			StyledEdge ge = this.decor.decorateEdge(e, nodes);
			graph.addEdge(ge);
		}
	}

	/** Redesign a graph such that they can be formed into a styled graph */
	public PTGraph<StyledNode,StyledEdge> decorate(PTGraph<T,E> graph) {
		return this.decorate(graph.getNodes(), graph.getEdges());
	}
}
