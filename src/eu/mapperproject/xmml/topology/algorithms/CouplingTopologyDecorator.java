package eu.mapperproject.xmml.topology.algorithms;

import java.util.Map;

import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.topology.Instance;
import eu.mapperproject.xmml.util.graph.Category;
import eu.mapperproject.xmml.util.graph.EdgeDecorator;
import eu.mapperproject.xmml.util.graph.GraphDecorator;
import eu.mapperproject.xmml.util.graph.NodeDecorator;
import eu.mapperproject.xmml.util.graph.SimpleNode;
import eu.mapperproject.xmml.util.graph.SimpleStyledEdge;
import eu.mapperproject.xmml.util.graph.StyledEdge;
import eu.mapperproject.xmml.util.graph.StyledNode;

/**
 * Adds decoration to a show a coupling topology using graphviz
 * @author Joris Borgdorff
 *
 */
public class CouplingTopologyDecorator implements GraphDecorator<Instance, Coupling> {
	@Override
	public boolean isDirected() {
		return true;
	}

	@Override
	public StyledNode decorateNode(Instance node) {
		return new NodeDecorator<Instance>(node, node.getId(), "shape=rectangle", new Category(node.getDomain()));
	}

	@Override
	public StyledEdge addSinkEdge(Instance node, StyledNode snode) {
		if (node.isFinal()) {
			return new SimpleStyledEdge(snode, SimpleNode.END);
		}
		return null;
	}

	@Override
	public StyledEdge decorateEdge(Coupling edge,
			Map<Instance, StyledNode> nodes) {
		String style = "dir=both arrowtail=";
		switch (edge.getFromOperator().getOperator()) {
		case Oi:
			style += "dot";
			break;
		case Of:
			style += "diamond";
			break;
		default:
			style += "none";
		}
		
		style += " arrowhead=";
		switch (edge.getToOperator().getOperator()) {
		case finit:
			style += "odiamond";
			break;
		case B:
			style += "onormal";
			break;
		case S:
			style += "odot";
			break;
		default:
			style += "vee";
		}
		
		StyledNode from = nodes.get(edge.getFrom());
		StyledNode to = nodes.get(edge.getTo());
		
		return new EdgeDecorator<Coupling>(edge, style, edge.getName(), from, to);
	}

	@Override
	public StyledEdge addSourceEdge(Instance node, StyledNode snode) {
		if (node.isInitial()) {
			return new SimpleStyledEdge(SimpleNode.START, snode);
		}
		return null;
	}
}
