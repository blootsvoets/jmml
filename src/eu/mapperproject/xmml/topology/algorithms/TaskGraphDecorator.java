package eu.mapperproject.xmml.topology.algorithms;

import java.util.Map;

import eu.mapperproject.xmml.topology.Coupling;
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
public class TaskGraphDecorator implements GraphDecorator<ProcessIteration, CouplingInstance> {
	@Override
	public boolean isDirected() {
		return true;
	}

	@Override
	public StyledNode decorateNode(ProcessIteration node) {
		if (node.hasDeadlock()) {
			return this.decorateMissingNode(node);
		}
		return new NodeDecorator<ProcessIteration>(node, node.toString(), null, new Category(node.getInstance().getDomain()));
	}

	@Override
	public StyledEdge decorateEdge(CouplingInstance edge,
			Map<ProcessIteration, StyledNode> nodes) {

		Coupling c = edge.getCoupling();
		ProcessIteration from = edge.getFrom(), to = edge.getTo();
		String label = null, style = null;
		if (from != null && to != null) {
			if (c == null) {
				label = "step";
				style = "style=dashed";
			}
			else {
				label = c.getName();
			}
		}
		
		StyledNode fromNode = nodes.get(from), toNode = nodes.get(to);

		return new EdgeDecorator<CouplingInstance>(edge, style, label, fromNode, toNode);
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
}
