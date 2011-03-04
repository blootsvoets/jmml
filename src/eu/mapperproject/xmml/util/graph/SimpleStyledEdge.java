package eu.mapperproject.xmml.util.graph;

/**
 * The simplest implementation of an Edge
 * @author Joris Borgdorff
 *
 */
public class SimpleStyledEdge implements StyledEdge {
	private final StyledNode from, to;
	
	public SimpleStyledEdge(StyledNode from, StyledNode to) {
		this.from = from;
		this.to = to;
	}
	
	@Override
	public String getStyle() {
		return null;
	}

	@Override
	public Category getCategory() {
		Category c = Tree.getCommonAncestor(from.getCategory(), to.getCategory());
		return c == null ? Category.NO_CATEGORY : c;
	}

	@Override
	public StyledNode getFrom() {
		return from;
	}

	@Override
	public StyledNode getTo() {
		return to;
	}

	@Override
	public String getLabel() {
		return null;
	}

}
