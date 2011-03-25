package eu.mapperproject.jmml.topology.algorithms;

import eu.mapperproject.jmml.topology.Coupling;
import eu.mapperproject.jmml.topology.Instance;
import eu.mapperproject.jmml.util.graph.AnnotatedStyledEdge;
import eu.mapperproject.jmml.util.graph.Category;
import eu.mapperproject.jmml.util.graph.GraphDecorator;
import eu.mapperproject.jmml.util.graph.SimpleNode;
import eu.mapperproject.jmml.util.graph.SimpleStyledEdge;
import eu.mapperproject.jmml.util.graph.StyledEdge;
import eu.mapperproject.jmml.util.graph.StyledNode;

/**
 * Adds decoration to a show a coupling topology using graphviz
 * @author Joris Borgdorff
 *
 */
public class CouplingTopologyDecorator extends GraphDecorator<Instance, Coupling> {
	public CouplingTopologyDecorator() {
		super(true);
	}

	@Override
	public StyledNode decorateNode(Instance node) {
		return new SimpleNode(node.getId(), "shape=rectangle", Category.getCategory(node.getDomain()));
	}

	@Override
	public StyledEdge decorateEdge(Coupling edge, StyledNode from, StyledNode to) {
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
		
		return new AnnotatedStyledEdge(from, to, style, edge.getName());
	}

	@Override
	public StyledEdge addSourceEdge(Instance node, StyledNode snode) {
		if (node.isInitial()) {
			return new SimpleStyledEdge(SimpleNode.START, snode);
		}
		return null;
	}

	@Override
	public StyledEdge addSinkEdge(Instance node, StyledNode snode) {
		if (node.isFinal()) {
			return new SimpleStyledEdge(snode, SimpleNode.END);
		}
		return null;
	}

	@Override
	public Category categorize(Instance object) {
		return Category.getCategory(object.getDomain());
	}
}
