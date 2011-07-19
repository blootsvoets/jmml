package eu.mapperproject.jmml;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import eu.mapperproject.jmml.definitions.MMLDefinitions;
import eu.mapperproject.jmml.io.CouplingTopologyToScaleMapExporter;
import eu.mapperproject.jmml.io.GraphToGraphvizExporter;
import eu.mapperproject.jmml.io.XMMLDocumentImporter;
import eu.mapperproject.jmml.topology.Coupling;
import eu.mapperproject.jmml.topology.CouplingTopology;
import eu.mapperproject.jmml.topology.Instance;
import eu.mapperproject.jmml.topology.algorithms.CouplingInstance;
import eu.mapperproject.jmml.topology.algorithms.CouplingTopologyDecorator;
import eu.mapperproject.jmml.topology.algorithms.DomainDecorator;
import eu.mapperproject.jmml.topology.algorithms.ProcessIteration;
import eu.mapperproject.jmml.topology.algorithms.TaskGraph;
import eu.mapperproject.jmml.topology.algorithms.TaskGraphDecorator;
import eu.mapperproject.jmml.util.Version;
import eu.mapperproject.jmml.util.graph.Cluster;
import eu.mapperproject.jmml.util.graph.Edge;
import eu.mapperproject.jmml.util.graph.PTGraph;
import eu.mapperproject.jmml.util.graph.Tree;

import java.util.logging.Logger;

/**
 * An MML document.
 * Different graphs can be generated from this document,
 * a coupling topology, task graph, overview of domains used or a scale
 * separation map.
 *
 * In the future, this class may also be called to do more detailed 
 * verification and validation of xMML documents.
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
public class MMLDocument {
	public enum GraphType {
		DOMAIN, TASK, TOPOLOGY;
	}
	
	private final static Logger logger = Logger.getLogger(MMLDocument.class.getName());
	private final ModelMetadata model;
	private final Version xmmlVersion;
	
	private final MMLDefinitions definitions;
	private final CouplingTopology topology;

	/** Create a new xMML document */
	public MMLDocument(ModelMetadata model, MMLDefinitions definitions, CouplingTopology topology, Version xmmlVersion) {
		this.model = model;
		this.definitions = definitions;
		this.topology = topology;
		this.xmmlVersion = xmmlVersion;
	}

	/**
	 * Export a graph of a given type of this document to a dot file
	 * and make a pdf file out of it.
	 */
	public void export(GraphType gt, File dot, File pdf, boolean collapse) throws IOException, InterruptedException {
		GraphToGraphvizExporter<?,?> exporter;
		switch (gt) {
			case DOMAIN:
				exporter = new GraphToGraphvizExporter(new DomainDecorator(), this.getDomainGraph());
				break;
			case TASK:
				exporter = new GraphToGraphvizExporter(new TaskGraphDecorator(), this.getTaskGraph(collapse));
				break;
			default:
				exporter = new GraphToGraphvizExporter(new CouplingTopologyDecorator(), this.topology.getGraph(), true, false, true);
				break;
		}
		
		System.out.println("Exporting graphviz file visualizing the " + gt + "...");
		
		exporter.export(dot, pdf);
		
		System.out.println("Done.");		
	}

	/** Generate a task graph */
	public PTGraph<ProcessIteration, CouplingInstance> getTaskGraph(boolean collapse) {
		TaskGraph task = new TaskGraph(this.topology, collapse, false, false);
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
		MMLOptions opt = new MMLOptions(args);
		
		if (opt.wantsOutput()) {
			File xmml = opt.getXMMLFile();
			File dot = opt.getDotFile();
			
			// Generating an XMML document and exporting it
			MMLDocument doc = null;
			try {
				doc = new XMMLDocumentImporter().parse(xmml);
			} catch (ValidityException e) {
				logger.log(Level.SEVERE, "The xMML file provided did not contain valid XML: {}", e);
				System.exit(2);
			} catch (ParsingException e) {
				logger.log(Level.SEVERE, "The xMML file could not be parsed: {}", e);
				System.exit(3);
			}
			
			try {
				File out;
				if (opt.topology != null) {
					doc.export(GraphType.TOPOLOGY, dot, opt.topology, false);
				}
				if (opt.taskgraph != null) {
					doc.export(GraphType.TASK, dot, opt.taskgraph, !opt.nocollapse);
				}
				if (opt.domain != null) {
					doc.export(GraphType.DOMAIN, dot, opt.domain, false);
				}
				if (opt.ssm != null) {
					CouplingTopologyToScaleMapExporter exp = new CouplingTopologyToScaleMapExporter(doc.topology);
					exp.export(opt.ssm);
				}
			} catch (IOException e) {
				logger.log(Level.SEVERE, "An error occurred while trying to write to graphviz file: {}", e);
				System.exit(4);
			} catch (InterruptedException ex) {
				logger.log(Level.SEVERE, "A pdf document could not be created as the process was interrupted: {}", ex);
				System.exit(5);
			}
		}
	}
}
