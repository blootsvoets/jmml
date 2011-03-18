package eu.mapperproject.xmml.topology.algorithms;

import eu.mapperproject.xmml.util.graph.Category;
import eu.mapperproject.xmml.util.graph.Cluster;
import eu.mapperproject.xmml.util.graph.Edge;
import eu.mapperproject.xmml.util.graph.GraphDecorator;
import eu.mapperproject.xmml.util.graph.SimpleNode;
import eu.mapperproject.xmml.util.graph.SimpleStyledEdge;
import eu.mapperproject.xmml.util.graph.StyledEdge;
import eu.mapperproject.xmml.util.graph.StyledNode;

/**
 * Adds decoration to a show a coupling topology using graphviz
 * @author Joris Borgdorff
 *
 */
public class DomainDecorator extends GraphDecorator<Cluster<?,?>, Edge<Cluster<?,?>>> {
	public DomainDecorator() {
		super(true);
	}

	@Override
	public StyledNode decorateNode(Cluster<?,?> node) {
		return new SimpleNode(node.getName(), null, node.getCategory());
	}

	@Override
	public StyledEdge decorateEdge(Edge<Cluster<?,?>> edge,
			StyledNode fromNode, StyledNode toNode) {
		return new SimpleStyledEdge(fromNode, toNode);
	}

	@Override
	public StyledNode decorateMissingNode(Cluster<?,?> node) {
		return new SimpleNode("None", "shape=rectangle");
	}

	@Override
	public Category categorize(Cluster<?,?> node) {
		return node.getCategory();
	}
}

