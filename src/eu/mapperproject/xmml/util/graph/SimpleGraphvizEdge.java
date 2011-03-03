package eu.mapperproject.xmml.util.graph;

/**
 * The simplest implementation of an Edge
 * @author Joris Borgdorff
 *
 */
public class SimpleGraphvizEdge implements GraphvizEdge {
	private final GraphvizNode from, to;
	
	public SimpleGraphvizEdge(GraphvizNode from, GraphvizNode to) {
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
	public GraphvizNode getFrom() {
		return from;
	}

	@Override
	public GraphvizNode getTo() {
		return to;
	}

	@Override
	public String getLabel() {
		return null;
	}

}
