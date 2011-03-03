package eu.mapperproject.xmml.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * An internal graph.
 * It is convertible to a JUNG graph
 * @author Joris Borgdorff
 *
 */
public class PTGraph<T, E extends Edge<T>> {
	private final boolean directed;
	private final Collection<E> edges;
	private final Collection<T> nodes;
	private boolean[] hasExtremity;
	
	public PTGraph(boolean directed) {
		this.directed = directed;
		this.edges = new ArrayList<E>();
		this.nodes = new ArrayList<T>();
		this.hasExtremity = new boolean[] {false, false};
	}
	
	public PTGraph(Graph<T, E> graph, T source, T sink) {
		this.directed = graph instanceof DirectedGraph;
		this.nodes = new ArrayList<T>(graph.getVertices());
		this.edges = new ArrayList<E>(graph.getEdges());

		for (Edge<T> e : this.edges) {
			if (e.getFrom() == null) {
				this.setSource(source);
			}
			if (e.getTo() == null) {
				this.setSink(sink);
			}
		}
	}
		
	public static <T extends Child<T>> PTGraph<T, ? extends Edge<T>> graphFromTree(Tree<T> tree) {
		PTGraph<T, SimpleEdge<T>> graph = new PTGraph<T,SimpleEdge<T>>(true);
		
		for (T elem : tree) {
			graph.nodes.add(elem);
			if (!elem.isRoot()) {
				graph.edges.add(new SimpleEdge<T>(elem.parent(), elem, Category.NO_CATEGORY));
			}
		}
		return graph;
	}
	
	public boolean isDirected() {
		return this.directed;
	}
	
	public boolean isEmpty() {
		return this.edges.isEmpty() && this.nodes.isEmpty();
	}
	
	public void addEdge(E e) {
		this.edges.add(e);
	}
	
	public boolean setSource(T src) {
		return this.setExtremity(0, src);
	}
	
	public boolean setSink(T snk) {
		return this.setExtremity(1, snk);
	}
	
	private boolean setExtremity(int i, T node) {
		if (!this.hasExtremity[i]) {
			this.nodes.add(node);
			this.hasExtremity[i] = true;
			return true;
		}
		return false;
	}

	public void addNode(T n) {
		this.nodes.add(n);
	}
	
	public Collection<E> getEdges() {
		return this.edges;
	}

	public Collection<T> getNodes() {
		return this.nodes;
	}
	
	public Graph<T,Edge<T>> getJungGraph(T source, T sink) {
		Graph<T, Edge<T>> graph;
		if (this.directed) {
			graph = new DirectedSparseGraph<T,Edge<T>>();
		}
		else {
			graph = new UndirectedSparseGraph<T,Edge<T>>();
		}
		
		for (T n : this.nodes) {
			graph.addVertex(n);
		}
		
		for (Edge<T> e : this.edges) {
			T from = e.getFrom(), to = e.getTo();
			
			graph.addEdge(e, from == null ? source : from, to == null ? sink : to);
		}
		
		return graph;
	}
	
	public static <T extends Categorizable, E extends Edge<T>> Tree<Cluster<T,E>> partition(PTGraph<T, E> graph) {
		Map<Category,PTGraph<T,E>> map = partitionMap(graph);		
		Tree<Category> categories = new Tree<Category>(map.keySet());
		Map<Category,Cluster<T,E>> clusters = new HashMap<Category,Cluster<T,E>>();
		Tree<Cluster<T,E>> tree = new Tree<Cluster<T,E>>();
		
		for (Category c : categories) {
			Cluster<T,E> cl;
			if (c.isRoot()) {
				cl = new Cluster<T,E>(c, map.get(c));
			}
			else {
				cl = new Cluster<T,E>(c, map.get(c), clusters.get(c.parent()));
			}
			clusters.put(c, cl);
			tree.add(cl);
		}
		
		return tree;
	}
	
	private static <T extends Categorizable, E extends Edge<T>> Map<Category,PTGraph<T,E>> partitionMap(PTGraph<T,E> graph) {
		Map<Category,PTGraph<T,E>> map = new HashMap<Category,PTGraph<T,E>>();
		for (E e : graph.getEdges()) {
			subgraph(graph, e, map).addEdge(e);
		}
		for (T n : graph.getNodes()) {
			subgraph(graph, n, map).addNode(n);
		}
		return map;
	}
	
	private static <T extends Categorizable, E extends Edge<T>> PTGraph<T,E> subgraph(PTGraph<T,E> graph, Categorizable elem, Map<Category, PTGraph<T,E>> map) {
		Category c = elem.getCategory();
		PTGraph<T,E> subg = map.get(c);
		if (subg == null) {
			subg = new PTGraph<T,E>(graph.isDirected());
			map.put(c, subg);
		}
		return subg;
	}
}