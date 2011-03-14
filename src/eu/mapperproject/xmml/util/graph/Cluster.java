package eu.mapperproject.xmml.util.graph;

/**
 * A hierarchical clustering, based on a category.
 * @author Joris Borgdorff
 */
public class Cluster<T,E extends Edge<T>> implements Child<Cluster<T,E>>, StyledNode {
	private final Category category;
	private final PTGraph<T,E> graph;
	private final Cluster<T,E> parent;
	private final String name;
	
	/** Create a cluster based on a category and with an induced subgraph with that category */
	public Cluster(Category c, PTGraph<T,E> g) {
		this(c, g, null);
	}
	
	/** Create a cluster based on a category and with an induced subgraph with that category, with a parent */
	public Cluster(Category c, PTGraph<T,E> g, Cluster<T,E> parent) {
		this.name = c.getName();
		this.category = c;
		this.graph = g;
		this.parent = parent;
	}

	@Override
	public Cluster<T,E> parent() {
		if (this.isRoot())
			throw new IllegalStateException("Root does not have parent");

		return this.parent;
	}

	@Override
	public boolean isRoot() {
		return this.parent == null;
	}
	
	/** Get the induced subgraph of only this cluster */
	public PTGraph<T,E> getGraph() {
		return this.graph;
	}

	@Override
	public String getStyle() {
		return "style=dashed; fontcolor=\"dimgray\"";
	}

	@Override
	public Category getCategory() {
		return this.category;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Cluster<?,?> c = (Cluster<?,?>)o;
		return this.name.equals(c.name) && this.category.equals(c.category);
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode() ^ this.category.hashCode();
	}
	
	@Override
	public String getName() {
		return this.name;
	}
}
