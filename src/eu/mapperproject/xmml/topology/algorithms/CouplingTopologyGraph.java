package eu.mapperproject.xmml.topology.algorithms;

import java.util.HashMap;
import java.util.Map;

import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.topology.CouplingTopology;
import eu.mapperproject.xmml.topology.Instance;
import eu.mapperproject.xmml.util.graph.Category;
import eu.mapperproject.xmml.util.graph.EdgeDecorator;
import eu.mapperproject.xmml.util.graph.GraphvizEdge;
import eu.mapperproject.xmml.util.graph.GraphvizNode;
import eu.mapperproject.xmml.util.graph.NodeDecorator;
import eu.mapperproject.xmml.util.graph.PTGraph;
import eu.mapperproject.xmml.util.graph.SimpleGraphvizEdge;
import eu.mapperproject.xmml.util.graph.SimpleNode;

public class CouplingTopologyGraph {
	private final PTGraph<GraphvizNode, GraphvizEdge> topology;
	private CouplingTopology desc;
	
	public CouplingTopologyGraph(CouplingTopology desc) {
		this.desc = desc;
		this.topology = new PTGraph<GraphvizNode, GraphvizEdge>(true);
		this.computeGraph();
	}
	
	private void computeGraph() {
		Map<String, NodeDecorator<Instance>> map = new HashMap<String, NodeDecorator<Instance>>();
		for (Instance i : desc.getInstances()) {
			NodeDecorator<Instance> n = new NodeDecorator<Instance>(i, i.getId(), null, new Category(i.getDomain()));
			map.put(i.getId(), n);
			this.topology.addNode(n);
			
			if (i.isFinal()) {
				this.topology.addEdge(new SimpleGraphvizEdge(n, SimpleNode.END));
				this.topology.setSink(SimpleNode.END);
			}
			if (i.isInitial()) {
				this.topology.addEdge(new SimpleGraphvizEdge(SimpleNode.START, n));
				this.topology.setSource(SimpleNode.START);
			}
		}
		
		for (Coupling cd : desc.getCouplings()) {
			String style = "dir=both arrowtail=";
			switch (cd.getFromPort().getOperator()) {
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
			switch (cd.getToPort().getOperator()) {
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
			
			EdgeDecorator<Coupling> e = new EdgeDecorator<Coupling>(cd, style, cd.getName(), map.get(cd.getFrom().getId()), map.get(cd.getTo().getId()));
			this.topology.addEdge(e);
		}
	}

	public PTGraph<GraphvizNode, GraphvizEdge> getGraph() {
		return this.topology;
	}
}
