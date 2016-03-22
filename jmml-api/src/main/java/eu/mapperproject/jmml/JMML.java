package eu.mapperproject.jmml;

import eu.mapperproject.jmml.io.CouplingTopologyToScaleMapExporter;
import eu.mapperproject.jmml.io.GraphToGraphvizExporter;
import eu.mapperproject.jmml.io.MUSCLECodeGenerator;
import eu.mapperproject.jmml.io.MUSCLECxAExporter;
import eu.mapperproject.jmml.specification.annotated.AnnotatedCoupling;
import eu.mapperproject.jmml.specification.annotated.AnnotatedInstancePort;
import eu.mapperproject.jmml.specification.annotated.AnnotatedModel;
import eu.mapperproject.jmml.specification.annotated.AnnotatedTopology;
import eu.mapperproject.jmml.topology.algorithms.*;
import eu.mapperproject.jmml.util.graph.Cluster;
import eu.mapperproject.jmml.util.graph.Edge;
import eu.mapperproject.jmml.util.graph.PTGraph;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;

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
public class JMML {
	public enum GraphType {
		TASK, TOPOLOGY;
	}
	
	private final static Logger logger = Logger.getLogger(JMML.class.getName());
	
	private final AnnotatedTopology topology;
	private final AnnotatedModel model;

	/** Create a new xMML document */
	public JMML(AnnotatedModel model) {
		this.model = model;
		this.topology = model.getTopology();
	}
	
	/**
	 * Export a graph of a given type of this document to a dot file
	 * and make a pdf file out of it.
	 */
	public void export(GraphType gt, File dot, File pdf, boolean collapse) throws IOException, InterruptedException {
		GraphToGraphvizExporter<?,?> exporter;
		switch (gt) {
			case TASK:
				exporter = new GraphToGraphvizExporter<ProcessIteration, CouplingInstance>(new TaskGraphDecorator(), this.getTaskGraph(collapse));
				break;
			default:
				PTGraph<AnnotatedInstancePort, AnnotatedCoupling> graph = CouplingTopologyDecorator.constructTopologyGraph(topology);
				exporter = new GraphToGraphvizExporter<AnnotatedInstancePort, AnnotatedCoupling>(new CouplingTopologyDecorator(true), graph, true, false, true);
				break;
		}
		
		System.out.println("Exporting graphviz file visualizing the " + gt + " to " + pdf.getAbsolutePath() + "...");
		
		exporter.export(dot, pdf);
		
		System.out.println("Done.");		
	}

	/** Generate a task graph */
	public PTGraph<ProcessIteration, CouplingInstance> getTaskGraph(boolean collapse) {
		TaskGraph task = new TaskGraph(this.topology, collapse, false, false);
		try {
			task.computeGraph();
		} catch (IllegalStateException ex) {
			logger.log(Level.WARNING, "The taskgraph was not correctly constructed.", ex);
		}
		return task.getGraph();
	}

	public static void main(String[] args) throws IOException {
		readConfiguration();
		
		JMMLOptions opt = new JMMLOptions(args);
		if (opt.wantsOutput()) {
			File xmml = opt.getXMMLFile();
			File dot = opt.getDotFile();
			
			// Generating an XMML document and exporting it
			JMML doc = null;
			try {
				AnnotatedModel model = AnnotatedModel.getModel(xmml);
				doc = new JMML(model);
			} catch (JAXBException ex) {
				logger.log(Level.SEVERE, "The xMML file could not be parsed or loaded.", ex);
			}
			
			try {
				if (opt.taskgraph != null) {
					doc.export(GraphType.TASK, dot, new File(opt.taskgraph).getAbsoluteFile(), !opt.nocollapse);
				}
				if (opt.topology != null) {
					doc.export(GraphType.TOPOLOGY, dot, new File(opt.topology).getAbsoluteFile(), false);
				}
				if (opt.ssm != null) {
					CouplingTopologyToScaleMapExporter exp = new CouplingTopologyToScaleMapExporter(doc.topology);
					exp.export(new File(opt.ssm).getAbsoluteFile());
				}
				if (opt.muscle != null) {
					MUSCLECodeGenerator gen = new MUSCLECodeGenerator(doc.model);
					gen.create(new File(opt.muscle).getAbsoluteFile());
				}
				if (opt.cxa != null) {
					MUSCLECxAExporter exp = new MUSCLECxAExporter(doc.topology);
					exp.export(new File(opt.cxa).getAbsoluteFile());
				}
			} catch (IOException e) {
				logger.log(Level.SEVERE, "An error occurred while trying to write to graphviz file.", e);
				System.exit(4);
			} catch (InterruptedException ex) {
				logger.log(Level.SEVERE, "A PDF document could not be created as the process was interrupted.", ex);
				System.exit(5);
			}
		}
		else {
			System.out.println("No output method requested.");
			opt.printUsage();
		}
	}
	
	private static void readConfiguration() {
		ClassLoader cl = JMML.class.getClassLoader();
		InputStream is = cl.getResourceAsStream("META-INF/application.properties");
		Properties inputProperties = new Properties(); 

		try {
			inputProperties.load(is);
			System.getProperties().putAll(inputProperties);
		} 
		catch (IOException ex) {
			logger.log(Level.WARNING, "External properties not loaded.", ex);
		}
		
		is = cl.getResourceAsStream( "META-INF/logging.properties" ); 
		
		try {
			LogManager.getLogManager().readConfiguration(is);
		} catch (Exception ex) {
			logger.log(Level.WARNING, "Custom logging properties not loaded.", ex);
		}
	}
}
