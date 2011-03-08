package eu.mapperproject.xmml;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import eu.mapperproject.xmml.definitions.XMMLDefinitions;
import eu.mapperproject.xmml.io.GraphToGraphvizExporter;
import eu.mapperproject.xmml.io.XMMLDocumentImporter;
import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.topology.CouplingTopology;
import eu.mapperproject.xmml.topology.Instance;
import eu.mapperproject.xmml.topology.algorithms.CouplingInstance;
import eu.mapperproject.xmml.topology.algorithms.CouplingTopologyDecorator;
import eu.mapperproject.xmml.topology.algorithms.ProcessIteration;
import eu.mapperproject.xmml.topology.algorithms.TaskGraph;
import eu.mapperproject.xmml.topology.algorithms.TaskGraphDecorator;
import eu.mapperproject.xmml.util.Version;
import eu.mapperproject.xmml.util.graph.Cluster;
import eu.mapperproject.xmml.util.graph.GraphDesigner;
import eu.mapperproject.xmml.util.graph.StyledEdge;
import eu.mapperproject.xmml.util.graph.StyledNode;
import eu.mapperproject.xmml.util.graph.PTGraph;
import eu.mapperproject.xmml.util.graph.Tree;

/**
 * An xMML document
 * @author Joris Borgdorff
 *
 */
public class XMMLDocument {
	private GraphToGraphvizExporter exporter;
	private final Map<GraphType, PTGraph<StyledNode, StyledEdge>> graphMap;

	public enum GraphType {
		DOMAIN, TASK, TOPOLOGY;
	}

	private final ModelMetadata model;
	private final Version xmmlVersion;
	
	private final XMMLDefinitions definitions;
	private final CouplingTopology topology;
	
	public XMMLDocument(ModelMetadata model, XMMLDefinitions definitions, CouplingTopology topology, Version xmmlVersion) {
		this.model = model;
		this.definitions = definitions;
		this.topology = topology;
		this.xmmlVersion = xmmlVersion;
		this.graphMap = new EnumMap<GraphType, PTGraph<StyledNode, StyledEdge>>(GraphType.class);
		this.exporter = new GraphToGraphvizExporter(false, false, false); 
	}
	
	public static void main(String[] args) {
		try {
			XMMLDocument doc = new XMMLDocumentImporter().parse(new File("/Users/jborgdo1/Desktop/canal.xmml"));
			doc.export(GraphType.TASK, "/Users/jborgdo1/Desktop/graphXmml.dot", "/Users/jborgdo1/Desktop/graphXmml.pdf");
		} catch (ValidityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void export(GraphType gt, String dotStr, String pdfStr) {
		PTGraph<StyledNode, StyledEdge> graph = this.getGraph(gt);
		
		System.out.println("Exporting graphviz file...");

		File dot = new File(dotStr);
		File pdf = new File(pdfStr);
		
		try {
			exporter.export(graph, dot, pdf);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(2);
		}
		
		System.out.println("Done.");		
	}
	
	public void print(GraphType gt) {
		exporter.print(this.getGraph(gt));
	}
	
	public PTGraph<StyledNode, StyledEdge> getGraph(GraphType gt) {
		PTGraph<StyledNode, StyledEdge> graph = this.graphMap.get(gt);
		GraphDesigner<ProcessIteration, CouplingInstance> tg;
		GraphDesigner<Instance, Coupling> cg;
		
		if (graph == null) {
			switch (gt) {
			case DOMAIN:
				graph = this.getGraph(GraphType.TOPOLOGY);
				Tree<Cluster<StyledNode, StyledEdge>> clTree = PTGraph.partition(graph);
				graph = PTGraph.graphFromTree(clTree);
				break;
			case TASK:
				tg = new GraphDesigner<ProcessIteration,CouplingInstance>(new TaskGraphDecorator());
				graph = tg.decorate(new TaskGraph(this.topology).getGraph());
				break;
			default:
				cg = new GraphDesigner<Instance,Coupling>(new CouplingTopologyDecorator());
				graph = cg.decorate(topology.getInstances(), topology.getCouplings());
				break;
			}
			
			this.graphMap.put(gt, graph);
		}
		
		return graph;
	}
}
