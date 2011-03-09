/**
 * 
 */
package eu.mapperproject.xmml.util.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
		Map<T, StyledNode> nodes = new HashMap<T, StyledNode>();
		Set<StyledNode> missingNodes = new HashSet<StyledNode>();
		PTGraph<StyledNode,StyledEdge> graph = new PTGraph<StyledNode,StyledEdge>(this.decor.isDirected());

		for (T t : ts) {
			StyledNode n = this.decor.decorateNode(t);
			graph.addNode(n);
			nodes.put(t, n);
			
			StyledEdge e = this.decor.addSourceEdge(t, n);
			if (e != null) {
				graph.setSource(e.getFrom());
				graph.addEdge(e);
			}
			e = this.decor.addSinkEdge(t, n);
			if (e != null) {
				graph.setSink(e.getTo());
				graph.addEdge(e);
			}
		}
		
		for (E e : es) {
			checkMissing(e.getFrom(), nodes, missingNodes, graph);
			checkMissing(e.getTo(), nodes, missingNodes, graph);
			StyledEdge ge = this.decor.decorateEdge(e, nodes);
			graph.addEdge(ge);
		}
		
		return graph;
	}

	/** Add a missing node to the graph if necessary and return it */
	private void checkMissing(T t, Map<T, StyledNode> nodes, Set<StyledNode> missingNode, PTGraph<StyledNode,StyledEdge> graph) {
		if (!nodes.containsKey(t)) {
			StyledNode missing = this.decor.decorateMissingNode(t);
			nodes.put(t, missing);
			if (!missingNode.contains(missing)) {
				missingNode.add(missing);
				graph.addNode(missing);
			}
		}
	}
	
	/** Redesign a graph such that they can be formed into a styled graph */
	public PTGraph<StyledNode,StyledEdge> decorate(PTGraph<T,E> graph) {
		return this.decorate(graph.getNodes(), graph.getEdges());
	}
}
