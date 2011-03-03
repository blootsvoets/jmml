package eu.mapperproject.xmml.util.graph;

/** An edge interface to be able to plot edges */
public interface GraphvizEdge extends GraphvizElement, Edge<GraphvizNode> {
	/** A label for the edge. Returns null if no label should be used */
	public String getLabel();
}
