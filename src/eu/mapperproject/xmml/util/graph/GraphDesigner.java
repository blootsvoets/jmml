/**
 * 
 */
package eu.mapperproject.xmml.util.graph;

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
		Map<T, StyledNode> nodes = new HashMap<T, StyledNode>();
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
				graph.setSink(e.getFrom());
				graph.addEdge(e);
			}
		}
		
		for (E e : es) {
			StyledEdge ge = this.decor.decorateEdge(e, nodes);
			graph.addEdge(ge);
		}
		
		return graph;
	}
	
	/** Redesign a graph such that they can be formed into a styled graph */
	public PTGraph<StyledNode,StyledEdge> decorate(PTGraph<T,E> graph) {
		return this.decorate(graph.getNodes(), graph.getEdges());
	}
}
