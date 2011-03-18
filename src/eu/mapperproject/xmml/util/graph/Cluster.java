package eu.mapperproject.xmml.util.graph;

/**
 * A hierarchical clustering, based on a category.
 * @author Joris Borgdorff
 */
public class Cluster<T,E extends Edge<T>> implements Child<Cluster<T,E>>, Categorizable {
	private final Category category;
	private final PTGraph<T,E> graph;
	private final Cluster<T,E> parent;
	private final String name;
	
	/** Create a cluster based on a category and with an induced subgraph with that category
	 * @throws NullPointerException if c is null
	 */
	public Cluster(Category c, PTGraph<T,E> g) {
		this(c, g, null);
	}
	
	/** Create a cluster based on a category and with an induced subgraph with that category, with a parent
	 * @throws NullPointerException if c is null
	 * @throws IllegalArgumentException if parent is null while c is not root
	 */
	public Cluster(Category c, PTGraph<T,E> g, Cluster<T,E> parent) {
		if (parent == null && !c.isRoot()) {
			throw new IllegalArgumentException("Can not create root cluster out of non-root category " + c);
		}
		else if (parent != null) {
			if (c.isRoot()) {
				throw new IllegalArgumentException("Can not create non-root cluster out of root category " + c);
			}
			else if (!c.parent().equals(parent.category)) {
				throw new IllegalArgumentException("Can not create cluster with a parent category " + c.parent() + " not matching category of the parent cluster " + parent.category);
			}
		}
		this.name = c.getName();
		this.category = c;
		this.graph = g;
		this.parent = parent;
	}

	@Override
	public Cluster<T,E> parent() {
		if (this.isRoot()) {
			throw new IllegalStateException("Root does not have parent");
		}

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
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Cluster<?,?> c = (Cluster<?,?>)o;
		return this.category.equals(c.category);
	}
	
	@Override
	public int hashCode() {
		return this.category.hashCode();
	}

	@Override
	public String toString() {
		return this.name;
	}

	/** Get the name of this cluster */
	public String getName() {
		return this.name;
	}

	@Override
	public Category getCategory() {
		return this.category;
	}

	@Override
	public int compareTo(Cluster<T, E> t) {
		return this.name.compareTo(t.name);
	}
}
