package eu.mapperproject.xmml;

import java.io.File;
import java.io.IOException;
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
import eu.mapperproject.xmml.topology.algorithms.DomainDecorator;
import eu.mapperproject.xmml.topology.algorithms.ProcessIteration;
import eu.mapperproject.xmml.topology.algorithms.TaskGraph;
import eu.mapperproject.xmml.topology.algorithms.TaskGraphDecorator;
import eu.mapperproject.xmml.util.Version;
import eu.mapperproject.xmml.util.graph.Cluster;
import eu.mapperproject.xmml.util.graph.Edge;
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
	public enum GraphType {
		DOMAIN, TASK, TOPOLOGY;
	}
	
	private final static Logger logger = Logger.getLogger(XMMLDocument.class.getName());
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
	}

	/**
	 * Export a graph of a given type of this document to a dot file
	 * and make a pdf file out of it.
	 */
	public void export(GraphType gt, String dotStr, String pdfStr) throws IOException, InterruptedException {
		GraphToGraphvizExporter<?,?> exporter;
		switch (gt) {
			case DOMAIN:
				exporter = new GraphToGraphvizExporter(new DomainDecorator(), this.getDomainGraph());
				break;
			case TASK:
				exporter = new GraphToGraphvizExporter(new TaskGraphDecorator(), this.getTaskGraph());
				break;
			default:
				exporter = new GraphToGraphvizExporter(new CouplingTopologyDecorator(), this.topology.getGraph(), true, false, true);
				break;
		}
		
		System.out.println("Exporting graphviz file...");

		File dot = new File(dotStr);
		File pdf = new File(pdfStr);
		
		exporter.export(dot, pdf);
		
		System.out.println("Done.");		
	}

	/** Generate a task graph */
	public PTGraph<ProcessIteration, CouplingInstance> getTaskGraph() {
		TaskGraph task = new TaskGraph(this.topology);
		task.computeGraph();
		return task.getGraph();
	}

	/** Generate a domain graph */
	public PTGraph<Cluster<Instance,Coupling>,Edge<Cluster<Instance,Coupling>>> getDomainGraph() {
		PTGraph<Instance, Coupling> graph;
		graph = this.topology.getGraph();
		Tree<Cluster<Instance,Coupling>> clTree = PTGraph.partition(graph, new CouplingTopologyDecorator());
		return PTGraph.graphFromTree(clTree);
	}

	public static void main(String[] args) throws IOException {
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
		XMMLDocument doc = null;
		try {
			doc = new XMMLDocumentImporter().parse(xmml);
		} catch (ValidityException e) {
			logger.log(Level.SEVERE, "The xMML file provided did not contain valid XML: {}", e);
			System.exit(1);
		} catch (ParsingException e) {
			logger.log(Level.SEVERE, "The xMML file could not be parsed: {}", e);
			System.exit(2);
		}
		
		try {
			doc.export(GraphType.TASK, args[1], args[2]);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "An error occurred while trying to write to graphviz file: {}", e);
			System.exit(3);
		} catch (InterruptedException ex) {
			logger.log(Level.SEVERE, "A pdf document could not be created as the process was interrupted: {}", ex);
			System.exit(4);
		}
	}
}
