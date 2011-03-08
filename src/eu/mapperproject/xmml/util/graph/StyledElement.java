package eu.mapperproject.xmml.util.graph;

/**
 * Element in a graph.
 * @author Joris Borgdorff
 *
 */
public interface StyledElement extends Categorizable {
	/** What style the edge should have */
	public String getStyle();
}