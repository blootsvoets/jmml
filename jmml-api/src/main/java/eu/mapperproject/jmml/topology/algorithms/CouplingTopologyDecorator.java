package eu.mapperproject.jmml.topology.algorithms;

import eu.mapperproject.jmml.specification.Coupling;
import eu.mapperproject.jmml.specification.Instance;
import eu.mapperproject.jmml.specification.Mapper;
import eu.mapperproject.jmml.specification.Port;
import eu.mapperproject.jmml.specification.Submodel;
import eu.mapperproject.jmml.specification.YesNoChoice;
import eu.mapperproject.jmml.specification.annotated.AnnotatedCoupling;
import eu.mapperproject.jmml.specification.annotated.AnnotatedDomain;
import eu.mapperproject.jmml.specification.annotated.AnnotatedInstance;
import eu.mapperproject.jmml.specification.annotated.AnnotatedInstancePort;
import eu.mapperproject.jmml.specification.annotated.AnnotatedPort;
import eu.mapperproject.jmml.specification.annotated.AnnotatedTopology;
import eu.mapperproject.jmml.util.graph.AnnotatedStyledEdge;
import eu.mapperproject.jmml.util.graph.Category;
import eu.mapperproject.jmml.util.graph.GraphDecorator;
import eu.mapperproject.jmml.util.graph.PTGraph;
import eu.mapperproject.jmml.util.graph.SimpleNode;
import eu.mapperproject.jmml.util.graph.SimpleStyledEdge;
import eu.mapperproject.jmml.util.graph.StyledEdge;
import eu.mapperproject.jmml.util.graph.StyledNode;
import java.util.ArrayList;
import javax.xml.bind.JAXBElement;

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
	
	public static PTGraph<AnnotatedInstancePort, AnnotatedCoupling> constructTopologyGraph(AnnotatedTopology topology) {
		PTGraph<AnnotatedInstancePort, AnnotatedCoupling> graph = new PTGraph<AnnotatedInstancePort, AnnotatedCoupling>(true);
		for (Instance inst : topology.getInstance()) {
			AnnotatedInstance ainst = (AnnotatedInstance)inst;
			Iterable<JAXBElement<Port>> ports;
			
			if (ainst.ofSubmodel()) {
				Submodel sub = ainst.getSubmodelInstance();
				ports = sub.getPorts().getInOrOut();
			}
			else {
				Mapper map = ainst.getMapperInstance();
				ports = map == null ? new ArrayList<JAXBElement<Port>>(0) : map.getPorts().getInOrOut();
			}
			for (JAXBElement<Port> port : ports) {
				AnnotatedInstancePort ip = new AnnotatedInstancePort(ainst, (AnnotatedPort) port.getValue());
				graph.addNode(ip);
			}
		}
		
		for (Coupling c : topology.getCoupling()) {
			graph.addEdge((AnnotatedCoupling)c);
		}
		
		return graph;
	}
}
