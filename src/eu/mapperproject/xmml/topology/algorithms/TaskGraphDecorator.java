package eu.mapperproject.xmml.topology.algorithms;

import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.util.graph.AnnotatedStyledEdge;
import eu.mapperproject.xmml.util.graph.Category;
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
public class TaskGraphDecorator extends GraphDecorator<ProcessIteration, CouplingInstance> {
	public TaskGraphDecorator() {
		super(true);
	}
	
	@Override
	public StyledNode decorateNode(ProcessIteration node) {
		if (node.hasDeadlock()) {
			return this.decorateMissingNode(node);
		}
		return new SimpleNode(node.toString(), null, categorize(node));
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
			return new SimpleStyledEdge(SimpleNode.START, snode);
		}
		return null;
	}

	@Override
	public StyledEdge addSinkEdge(ProcessIteration node, StyledNode snode) {
		if (node.isFinal()) {
			return new SimpleStyledEdge(snode, SimpleNode.END);
		}
		return null;
	}

	@Override
	public StyledNode decorateMissingNode(ProcessIteration node) {
		return new SimpleNode("Deadlock[" + node + "]", "shape=octagon,fontcolor=white,style=filled,color=red,fillcolor=red");
	}

	@Override
	public Category categorize(ProcessIteration pi) {
		return Category.getCategory(pi.getInstance().getDomain());
	}
}
