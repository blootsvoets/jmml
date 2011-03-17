package eu.mapperproject.xmml;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;

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
import java.util.logging.Logger;

/**
 * An xMML document. Different graphs can be generated from this document,
 * a coupling topology or a task graph.
 *
 * In the future, this class may also be called to do verification and validation
 * of xMML documents.
 *
 * Calling this as an executable, it takes three arguments:
 * an xMML file, a Graphviz dot file and a pdf file which Graphviz will convert
 * the dot file to. The given xMML file is assumed to be pre-processed with the
 * command
 * <pre>
 * $ xmllint --xinclude FILE.xml &gt; NEWFILE.xmml
 * </pre>
 * part of the libxml2 package.
 * 
 * @author Joris Borgdorff
 *
 */
public class XMMLDocument {
	private final static Logger logger = Logger.getLogger(XMMLDocument.class.getName());
	private GraphToGraphvizExporter exporter;
	private final Map<GraphType, PTGraph<StyledNode, StyledEdge>> graphMap;

	public enum GraphType {
		DOMAIN, TASK, TOPOLOGY;
	}

	private final ModelMetadata model;
	private final Version xmmlVersion;
	
	private final XMMLDefinitions definitions;
	private final CouplingTopology topology;

	/** Create a new xMML document */
	public XMMLDocument(ModelMetadata model, XMMLDefinitions definitions, CouplingTopology topology, Version xmmlVersion) {
		this.model = model;
		this.definitions = definitions;
		this.topology = topology;
		this.xmmlVersion = xmmlVersion;
		this.graphMap = new EnumMap<GraphType, PTGraph<StyledNode, StyledEdge>>(GraphType.class);
		this.exporter = new GraphToGraphvizExporter(false, false, false); 
	}
	
	public static void main(String[] args) {
		// Argument verification
		if (args.length != 3) {
			logger.severe("XMMLDocument takes three arguments: an xMML file, a GraphViz dot file to write to and a pdf file for GraphViz to write to");
			System.exit(1);
		}
		File xmml = new File(args[0]);
		if (!xmml.exists()) {
			logger.log(Level.SEVERE, "given xMML document {0} does not exist", xmml);
			System.exit(2);
		}
		File dotParent = new File(args[1]).getParentFile();
		if (dotParent == null || !dotParent.exists()) {
			logger.log(Level.SEVERE, "directory of Graphviz file {0} does not exist", args[1]);
			System.exit(2);
		}
		File pdfParent = new File(args[2]).getParentFile();
		if (pdfParent == null || !pdfParent.exists()) {
			logger.log(Level.SEVERE, "directory of pdf file {0} does not exist", args[2]);
			System.exit(2);
		}

		// Generating an XMML document and exporting it
		try {
			XMMLDocument doc = new XMMLDocumentImporter().parse(xmml);
			doc.export(GraphType.TASK, args[1], args[2]);
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

	/** Export a graph of a given type of this document to a dot file
	 * and make a pdf file out of it.
	 */
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

	/** Print a graph of a given type to standard out, using GraphViz notation */
	public void print(GraphType gt) {
		exporter.print(this.getGraph(gt));
	}

	/** Generate a graph of given type */
	public PTGraph<StyledNode, StyledEdge> getGraph(GraphType gt) {
		PTGraph<StyledNode, StyledEdge> graph;
		GraphDesigner<ProcessIteration, CouplingInstance> tg;
		GraphDesigner<Instance, Coupling> cg;
		
		switch (gt) {
		case DOMAIN:
			graph = this.getGraph(GraphType.TOPOLOGY);
			Tree<Cluster<StyledNode, StyledEdge>> clTree = PTGraph.partition(graph);
			graph = PTGraph.graphFromTree(clTree);
			break;
		case TASK:
			tg = new GraphDesigner<ProcessIteration,CouplingInstance>(new TaskGraphDecorator());
			TaskGraph task = new TaskGraph(this.topology);
			task.computeGraph();
			graph = tg.decorate(task.getGraph());
			break;
		default:
			cg = new GraphDesigner<Instance,Coupling>(new CouplingTopologyDecorator());
			graph = cg.decorate(topology.getInstances(), topology.getCouplings());
			break;
		}
		
		return graph;
	}
}
