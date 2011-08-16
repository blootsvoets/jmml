package eu.mapperproject.jmml.util.graph;

import eu.mapperproject.jmml.specification.graph.Edge;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.util.TreeMap;

/**
 * An internal graph.
 * It is convertible from and to a JUNG graph and from a tree.
 * @author Joris Borgdorff
 */
public class PTGraph<T, E extends Edge<T>> {
	private final boolean directed;
	private final Collection<E> edges;
	private final Collection<T> nodes;
	
	/** Create a PTGraph, with given (in)directionality. */
	public PTGraph(boolean directed) {
		this(directed, new ArrayList<T>(), new ArrayList<E>());
	}

	/** Create a PTGraph with given (in)directionality and starting with a collection of nodes and edges. */
	public PTGraph(boolean directed, Collection<T> nodes, Collection<E> edges) {
		this.directed = directed;
		this.nodes = new ArrayList<T>(nodes);
		this.edges = new ArrayList<E>(edges);
	}

	/**
	 * Create a PTGraph as a copy of given JUNG Graph.
	 * If any of the edges have null as from or to node, it will be replaced
	 * with given source or sink nodes respectively.
	 */
	public PTGraph(Graph<T, E> graph, T source, T sink) {
		this(graph instanceof DirectedGraph, graph.getVertices(), graph.getEdges());
	}

	/**
	 * Create a PTGraph from a tree.
	 * The created PTGraph will be directed from parents to children.
	 * @throws NullPointerException if given tree is null
	 */
	public static <T extends Child<T>> PTGraph<T, Edge<T>> graphFromTree(Tree<T> tree) {
		PTGraph<T, Edge<T>> graph = new PTGraph<T,Edge<T>>(true);
		
		for (T elem : tree) {
			graph.addNode(elem);
			if (!elem.isRoot()) {
				graph.addEdge(new SimpleEdge<T>(elem.parent(), elem));
			}
		}
		return graph;
	}
	
	/** Whether the PTGraph is a directed graph. */
	public boolean isDirected() {
		return this.directed;
	}
	
	/** Whether the PTGraph contains edges or nodes. */
	public boolean isEmpty() {
		return this.edges.isEmpty() && this.nodes.isEmpty();
	}

	/** Number of nodes the graph contains. */
	public int nodeCount() {
		return this.nodes.size();
	}
	
	/**
	 * Add an edge to the graph.
	 * @throws NullPointerException if e is null, or its from or to nodes are null.
	 */
	public void addEdge(E e) {
		if (e == null) throw new NullPointerException("Edge in PTGraph may not be null.");
		else if (e.getFrom() == null || e.getTo() == null) throw new NullPointerException("From and to node of an edge added to the graph may not be null.");
		this.edges.add(e);
	}

	/**
	 * Add a node to the graph.
	 * @throws NullPointerException if n is null.
	 */
	public void addNode(T n) {
		if (n == null) throw new NullPointerException("Node in PTGraph may not be null.");
		this.nodes.add(n);
	}

	/** Get all edges in the graph */
	public Collection<E> getEdges() {
		return this.edges;
	}

	/** Get all nodes in the graph */
	public Collection<T> getNodes() {
		return this.nodes;
	}

	/**
	 * Create a JUNG graph out of the current PTGraph.
	 * Any edges with a from or to node being null, will have those nodes
	 * replaced by given source and sink nodes respectively.
	 */
	public Graph<T,Edge<T>> getJungGraph(T source, T sink) {
		Graph<T, Edge<T>> graph;
		if (this.directed) {
			graph = new DirectedSparseGraph<T,Edge<T>>();
		}
		else {
			graph = new UndirectedSparseGraph<T,Edge<T>>();
		}
		
		for (T n : this.getNodes()) {
			graph.addVertex(n);
		}
		
		for (Edge<T> e : this.edges) {
			graph.addEdge(e, e.getFrom(), e.getTo());
		}
		
		return graph;
	}

	/**
	 * Partition a graph into clusters based on the category of the graph elements
	 * @param graph graph to partition
	 * @param categorizer assigns a category to each of the elements of the graph
	 * @return a tree of subgraphs, from upper level category to lower level category
	 * @throws NullPointerException if the graph or any of its elements is null or the categorizer is null
	 */
	public static <T, E extends Edge<T>> Tree<Cluster<T,E>> partition(PTGraph<T, E> graph, Categorizer<T> categorizer) {
		Map<Category,PTGraph<T,E>> map = partitionMap(graph, categorizer);
		Tree<Category> categories = new Tree<Category>(map.keySet());
		Map<Category,Cluster<T,E>> clusters = new TreeMap<Category,Cluster<T,E>>();
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

	/**
	 * Partition the graph into subgraphs per category
	 * @return all subgraphs of the graph as a map.
	 */
	private static <T,E extends Edge<T>> Map<Category,PTGraph<T,E>> partitionMap(PTGraph<T,E> graph, Categorizer<T> categorizer) {
		Map<Category,PTGraph<T,E>> map = new TreeMap<Category,PTGraph<T,E>>();
		final boolean directed = graph.isDirected();
		for (E e : graph.getEdges()) {
			Category cfrom = categorizer.categorize(e.getFrom());
			Category cto = categorizer.categorize(e.getTo());
			Category c = Tree.getCommonAncestor(cfrom, cto);
			subgraph(c, map, directed).addEdge(e);
		}
		for (T n : graph.getNodes()) {
			subgraph(categorizer.categorize(n), map, directed).addNode(n);
		}
		return map;
	}

	/**
	 * Get the subgraph in the map of the given category.
	 * If it does not exist, create a new one and add it to the map.
	 * @return a graph of the given category
	 */
	private static <T, E extends Edge<T>> PTGraph<T,E> subgraph(Category c, Map<Category, PTGraph<T,E>> map, boolean directed) {
		PTGraph<T,E> subg = map.get(c);
		if (subg == null) {
			subg = new PTGraph<T,E>(directed);
			map.put(c, subg);
		}
		return subg;
	}
}