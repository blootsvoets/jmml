package eu.mapperproject.jmml.topology.algorithms;

import eu.mapperproject.jmml.specification.YesNoChoice;
import eu.mapperproject.jmml.specification.annotated.AnnotatedCoupling;
import eu.mapperproject.jmml.specification.annotated.AnnotatedDomain;
import eu.mapperproject.jmml.specification.annotated.AnnotatedInstance;
import eu.mapperproject.jmml.specification.annotated.AnnotatedInstancePort;
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
public class CouplingTopologyDecorator extends GraphDecorator<AnnotatedInstancePort, AnnotatedCoupling> {
	/** A start node. */
	private final static SimpleNode START = new SimpleNode("start", "label=\"\",shape=circle,fill=black,style=filled,fillcolor=black,width=0.25");
	/** An end node. */
	private final static SimpleNode END = new SimpleNode("end", "label=\"\",shape=doublecircle,style=filled,fillcolor=black,width=0.25");

	public CouplingTopologyDecorator() {
		super(true);
	}

	@Override
	public StyledNode decorateNode(AnnotatedInstancePort node) {
		Category cat = Category.getCategory((AnnotatedDomain)node.getInstance().getDomain());
		return new SimpleNode(node.getInstance().getId(), "shape=rectangle", cat);
	}

	@Override
	public StyledEdge decorateEdge(AnnotatedCoupling edge, StyledNode from, StyledNode to) {
		String style = "dir=both arrowtail=";
		switch (edge.getFrom().getPort().getOperator()) {
		case OI:
			style += "dot";
			break;
		case OF:
			style += "diamond";
			break;
		default:
			style += "none";
		}
		
		style += " arrowhead=";
		switch (edge.getTo().getPort().getOperator()) {
		case FINIT:
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
	public StyledEdge addSourceEdge(AnnotatedInstancePort node, StyledNode snode) {
		if (node.getInstance().getInit() == YesNoChoice.YES) {
			return new SimpleStyledEdge(START, snode);
		}
		return null;
	}

	@Override
	public StyledEdge addSinkEdge(AnnotatedInstancePort node, StyledNode snode) {
		if (node.getInstance().isFinal()) {
			return new SimpleStyledEdge(snode, END);
		}
		return null;
	}

	@Override
	public Category categorize(AnnotatedInstancePort object) {
		return Category.getCategory((AnnotatedDomain)object.getInstance().getDomain());
	}
}
