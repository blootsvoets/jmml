package eu.mapperproject.jmml.util.graph;

import eu.mapperproject.jmml.specification.graph.Edge;

/** An edge interface to be able to plot edges */
public interface StyledEdge extends StyledElement, Edge<StyledNode> {
	/** A label for the edge. Returns null if no label should be used */
	public String getLabel();
}
