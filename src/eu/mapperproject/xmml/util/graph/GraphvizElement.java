package eu.mapperproject.xmml.util.graph;

/**
 * Element in a graph.
 * @author Joris Borgdorff
 *
 */
public interface GraphvizElement extends Categorizable {
	/** What style the edge should have */
	public String getStyle();
}
