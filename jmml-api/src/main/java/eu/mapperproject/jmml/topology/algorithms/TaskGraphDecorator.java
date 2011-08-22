package eu.mapperproject.jmml.topology.algorithms;

import eu.mapperproject.jmml.specification.annotated.AnnotatedDomain;
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
public class TaskGraphDecorator extends GraphDecorator<ProcessIteration, CouplingInstance> {
	/** A start node. */
	private final static SimpleNode START = new SimpleNode("start", "shape=Mdiamond");
	/** An end node. */
	private final static SimpleNode END = new SimpleNode("end", "shape=Msquare");	

	public TaskGraphDecorator() {
		super(true);
	}
	
	@Override
	public StyledNode decorateNode(ProcessIteration node) {
		if (node.hasDeadlock()) {
			return this.decorateMissingNode(node);
		}
		return new SimpleNode(node.toString(), node.getInstance().ofSubmodel() ? null : "shape=hexagon", categorize(node));
	}

	@Override
	public StyledEdge decorateEdge(CouplingInstance edge,
			StyledNode fromNode, StyledNode toNode) {

		String label, style;
		if (edge.isVirtual()) {
			label = edge.toSameInstance() ? "step" : "state";
			style = "style=dashed";
		}
		else {
			label = edge.getCoupling().getName();
			style = null;
		}
		
		return new AnnotatedStyledEdge(fromNode, toNode, style, label);
	}

	@Override
	public StyledEdge addSourceEdge(ProcessIteration node, StyledNode snode) {
		if (node.isInitial()) {
			return new SimpleStyledEdge(START, snode);
		}
		return null;
	}

	@Override
	public StyledEdge addSinkEdge(ProcessIteration node, StyledNode snode) {
		if (node.isFinal()) {
			return new SimpleStyledEdge(snode, END);
		}
		return null;
	}

	@Override
	public StyledNode decorateMissingNode(ProcessIteration node) {
		return new SimpleNode("Deadlock[" + node + "]", "shape=octagon,fontcolor=white,style=filled,color=red,fillcolor=red");
	}

	@Override
	public Category categorize(ProcessIteration pi) {
		return Category.getCategory((AnnotatedDomain)pi.getInstance().getDomain());
	}
}
