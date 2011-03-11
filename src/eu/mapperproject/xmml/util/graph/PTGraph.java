package eu.mapperproject.xmml.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.util.HashSet;
import java.util.List;

/**
 * An internal graph.
 * It is convertible to a JUNG graph
 * @author Joris Borgdorff
 *
 */
public class PTGraph<T, E extends Edge<T>> {
	private final boolean directed;
	private final Collection<E> edges;
	private final Map<T, List<Collection<E>>> edgesPerNode;
	private boolean[] hasExtremity;
	
	public PTGraph(boolean directed) {
		this.directed = directed;
		this.edges = new HashSet<E>();
		this.edgesPerNode = new HashMap<T, List<Collection<E>>>();
		this.hasExtremity = new boolean[] {false, false};
	}
	
	public PTGraph(Graph<T, E> graph, T source, T sink) {
		this.directed = graph instanceof DirectedGraph;
		this.edgesPerNode = new HashMap<T, List<Collection<E>>>();
		for (T node : graph.getVertices()) {
			List<Collection<E>> list = new ArrayList<Collection<E>>(2);
			list.add(new HashSet<E>(graph.getInEdges(node)));
			list.add(new HashSet<E>(graph.getOutEdges(node)));
			this.edgesPerNode.put(node, list);
		}
		
		this.edges = new HashSet<E>(graph.getEdges());

		for (Edge<T> e : this.edges) {
			if (e.getFrom() == null) {
				this.setSource(source);
			}
			if (e.getTo() == null) {
				this.setSink(sink);
			}
		}
	}
		
	public static <T extends StyledNode & Child<T>> PTGraph<StyledNode, StyledEdge> graphFromTree(Tree<T> tree) {
		PTGraph<StyledNode, StyledEdge> graph = new PTGraph<StyledNode,StyledEdge>(true);

		for (T elem : tree) {
			graph.addNode(elem);
			if (!elem.isRoot()) {
				StyledEdge edge = new SimpleStyledEdge(elem.parent(), elem);
				graph.addEdge(edge);
			}
		}
		return graph;
	}
	
	public boolean isDirected() {
		return this.directed;
	}
	
	public boolean isEmpty() {
		return this.edges.isEmpty() && this.edgesPerNode.isEmpty();
	}
	

	
	public void addEdge(E e) {
		List<Collection<E>> list;
		
		list = this.addNode(e.getFrom());
		list.get(1).add(e);
		list = this.addNode(e.getTo());
		list.get(0).add(e);
		this.edges.add(e);
	}
	
	public final boolean setSource(T src) {
		return this.setExtremity(0, src);
	}
	
	public final boolean setSink(T snk) {
		return this.setExtremity(1, snk);
	}
	
	private boolean setExtremity(int i, T node) {
		if (!this.hasExtremity[i]) {
			this.addNode(node);
			this.hasExtremity[i] = true;
			return true;
		}
		return false;
	}

	public List<Collection<E>> addNode(T n) {
		List<Collection<E>> list = this.edgesPerNode.get(n);
		if (list == null) {
			list = new ArrayList<Collection<E>>(2);
			list.add(new HashSet<E>(6));
			list.add(new HashSet<E>(5));
			this.edgesPerNode.put(n, list);
		}
		return list;
	}

	public void removeNode(T n) {
		Collection<E> list = this.getEdgesIn(n);
		this.edges.removeAll(list);
		for (E e : list) {
			this.getEdgesOut(e.getFrom()).remove(e);
		}
		list = this.getEdgesOut(n);
		this.edges.removeAll(list);
		for (E e : list) {
			this.getEdgesIn(e.getTo()).remove(e);

		}
		this.edgesPerNode.remove(n);
	}
	
	public Collection<E> getEdges() {
		return this.edges;
	}

	public Collection<E> getEdges(T node) {
		List<Collection<E>> list = this.getEdgesList(node);
		Collection<E> all = new ArrayList<E>(list.get(0).size() + list.get(1).size());
		all.addAll(list.get(0)); all.addAll(list.get(1));
		return all;
	}

	public Collection<E> getEdgesIn(T node) {
		return this.getEdgesList(node).get(0);
	}

	public Collection<E> getEdgesOut(T node) {
		return this.getEdgesList(node).get(1);
	}

	private List<Collection<E>> getEdgesList(T node) {
		List<Collection<E>> list = this.edgesPerNode.get(node);
		if (list == null) {
			throw new IllegalArgumentException("Node " + node + " not added to graph");
		}
		return list;
	}

	public Collection<T> getNodes() {
		return this.edgesPerNode.keySet();
	}
	
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
			T from = e.getFrom(), to = e.getTo();
			
			graph.addEdge(e, from == null ? source : from, to == null ? sink : to);
		}
		
		return graph;
	}
	
	public static <T extends Categorizable, E extends Edge<T> & Categorizable> Tree<Cluster<T,E>> partition(PTGraph<T, E> graph) {
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
	
	private static <T extends Categorizable, E extends Edge<T> & Categorizable> Map<Category,PTGraph<T,E>> partitionMap(PTGraph<T,E> graph) {
		Map<Category,PTGraph<T,E>> map = new HashMap<Category,PTGraph<T,E>>();
		for (E e : graph.getEdges()) {
			subgraph(graph, e, map).addEdge(e);
		}
		for (T n : graph.getNodes()) {
			subgraph(graph, n, map).addNode(n);
		}
		return map;
	}
	
	private static <T, E extends Edge<T>> PTGraph<T,E> subgraph(PTGraph<T,E> graph, Categorizable elem, Map<Category, PTGraph<T,E>> map) {
		Category c = elem.getCategory();
		PTGraph<T,E> subg = map.get(c);
		if (subg == null) {
			subg = new PTGraph<T,E>(graph.isDirected());
			map.put(c, subg);
		}
		return subg;
	}
}