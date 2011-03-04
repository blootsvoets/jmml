package eu.mapperproject.xmml.util.graph;

/**
 * The simplest implementation of a node
 * @author Joris Borgdorff
 *
 */
public class SimpleNode implements StyledNode {
	private final String name;
	private final String style;
	
	/** A prototypical start node. */
	public final static SimpleNode START = new SimpleNode("start", "shape=Mdiamond");
	/** A prototypical end node. */
	public final static SimpleNode END = new SimpleNode("end", "shape=Msquare");
	
	public SimpleNode(String name, String style) {
		this.name = name;
		this.style = style;
	}

	@Override
	public String getStyle() {
		return this.style;
	}

	@Override
	public Category getCategory() {
		return Category.NO_CATEGORY;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || !this.getClass().equals(o.getClass())) return false;
		return this.getName().equals(((StyledNode)o).getName());
	}
	
	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}
}
