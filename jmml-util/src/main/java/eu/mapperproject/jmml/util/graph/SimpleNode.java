package eu.mapperproject.jmml.util.graph;

/**
 * The simplest implementation of a node
 * @author Joris Borgdorff
 *
 */
public class SimpleNode implements StyledNode {
	private final String name;
	private final String style;
	private final Category category;
	
	public SimpleNode(String name, String style) {
		this(name, style, Category.NO_CATEGORY);
	}

	public SimpleNode(String name, String style, Category category) {
		this.name = name;
		this.style = style;
		this.category = category;
	}

	@Override
	public String getStyle() {
		return this.style;
	}

	@Override
	public Category getCategory() {
		return category;
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
		if (o == null || getClass() != o.getClass()) return false;
		return this.getName().equals(((StyledNode)o).getName());
	}
	
	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}
}
