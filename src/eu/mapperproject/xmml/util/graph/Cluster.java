package eu.mapperproject.xmml.util.graph;

/**
 * A hierarchical clustering, based on a category.
 * @author Joris Borgdorff
 */
public class Cluster implements Child<Cluster>, GraphvizNode {
	private final Category category;
	private final PTGraph graph;
	private final Cluster parent;
	private final String name;
	
	/** Create a cluster based on a category and with an induced subgraph with that category */
	public Cluster(Category c, PTGraph g) {
		this(c, g, null);
	}
	
	/** Create a cluster based on a category and with an induced subgraph with that category, with a parent */
	public Cluster(Category c, PTGraph g, Cluster parent) {
		this.name = c.getName();
		this.category = c;
		this.graph = g;
		this.parent = parent;
	}

	@Override
	public Cluster parent() {
		if (this.isRoot())
			throw new IllegalStateException("Root does not have parent");

		return this.parent;
	}

	@Override
	public boolean isRoot() {
		return this.parent == null;
	}
	
	/** Get the induced subgraph of only this cluster */
	public PTGraph getGraph() {
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
		if (o == null || !this.getClass().equals(o.getClass())) return false;
		Cluster c = (Cluster)o;
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
