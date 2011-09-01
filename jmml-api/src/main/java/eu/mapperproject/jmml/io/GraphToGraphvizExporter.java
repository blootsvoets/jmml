package eu.mapperproject.jmml.io;

import eu.mapperproject.jmml.util.FastArrayList;
import java.io.File;
import java.io.IOException;

import eu.mapperproject.jmml.util.Indent;
import eu.mapperproject.jmml.util.graph.Category;
import eu.mapperproject.jmml.util.graph.Cluster;
import eu.mapperproject.jmml.util.graph.Edge;
import eu.mapperproject.jmml.util.graph.GraphDecorator;
import eu.mapperproject.jmml.util.graph.PTGraph;
import eu.mapperproject.jmml.util.graph.StyledEdge;
import eu.mapperproject.jmml.util.graph.StyledNode;
import eu.mapperproject.jmml.util.graph.Tree;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Can export a PTGraph to graphviz
 * @author Joris Borgdorff
 */
public class GraphToGraphvizExporter<V, E extends Edge<V>> extends AbstractExporter {
	private final Indent tab;
	private final boolean cluster, horizontal, edgeLabel;
	private final static String DOT_EXEC = System.getProperty("eu.mapperproject.jmml.io.dot.path", "/usr/local/bin/dot");
	private final static int SB_NODES = 1000;
	private final GraphDecorator<V, E> decorator;
	private final PTGraph<V,E> graph;
	private List<StyledEdge> sink, source;
	private final static Logger logger = Logger.getLogger(GraphToGraphvizExporter.class.getName());
	
	public GraphToGraphvizExporter(GraphDecorator<V,E> decorator, PTGraph<V,E> graph, boolean cluster, boolean horizontal, boolean edgeLabel) {
		this.tab = new Indent(4);
		this.cluster = cluster;
		this.horizontal = horizontal;
		this.edgeLabel = edgeLabel;
		this.decorator = decorator;
		this.graph = graph;
	}
	
	public GraphToGraphvizExporter(GraphDecorator<V,E> decorator, PTGraph<V,E> graph) {
		this(decorator, graph, false, false, true);
	}

	@SuppressWarnings("LoggerStringConcat")
	public void export(File f, File pdf) throws IOException, InterruptedException {
		this.export(f);
		logger.info("Converting to PDF...");
		String[] dot = {DOT_EXEC, "-Tpdf", "-o" + pdf.getAbsolutePath(), f.getAbsolutePath()};
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Executing " + join(Arrays.asList(dot), " "));
		}
		BufferedReader output = null;
		try {
			Process exe = new ProcessBuilder().command(dot).redirectErrorStream(true).start();
			output = new BufferedReader(new InputStreamReader(exe.getInputStream()));
			exe.waitFor();
		}
		catch(IOException e) {
			logger.log(Level.SEVERE, "Graphviz is not installed in /usr/local. Please provide a correct location for dot by specifying\n-Deu.mapperproject.jmml.io.dot.path=/path/to/bin/dot on the command-line when running JMML.", e);
		}
		
		if (output != null) {
			String line;
			while (output.ready()) {
				line = output.readLine();
				if (line.contains("png")) {
					logger.severe("Graphviz is not installed with PDF support. Before installing graphviz, please ensure that cairo and pango are installed.");
				}
				else {
					logger.warning(line);
				}
			}
		}
	}
	
	private void edgeTemplate(StringBuilder sb, StyledEdge e, boolean directed) {
		sb.append(tab);
		sb.append('"'); sb.append(e.getFrom()); sb.append('"');
		sb.append(directed ? "->" : "--");
		sb.append('"');
		sb.append(e.getTo());
		sb.append('"');

		String label = e.getLabel();
		String style = e.getStyle();

		if ((this.edgeLabel && label != null) || style != null) {
			sb.append('[');
			if (this.edgeLabel && label != null) {
				sb.append("label=\"");
				sb.append(label);
				sb.append('"');
				if (style != null) sb.append(',');
			}
			if (style != null) {
				sb.append(style);
			}
			sb.append(']');
		}

		sb.append(';');
	}
	
	
	private void nodeTemplate(StringBuilder sb, StyledNode n) {
		String style = n.getStyle();
		sb.append(tab);
		sb.append('"');
		sb.append(n.getName());
		sb.append('"');
		if (style != null) {
			sb.append('[');
			sb.append(style);
			sb.append(']');
		}
		sb.append(';');
	}
		
	private void clusterContents(Cluster<V,E> c, Tree<Cluster<V,E>> clusters) throws IOException {
		StringBuilder sb = new StringBuilder(SB_NODES*30);
		PTGraph<V,E> g = c.getGraph();
		boolean directed = this.decorator.isDirected();
		int i = 0;
		if (g != null) {
			Collection<String> sns = new HashSet<String>();
			// Add nodes
			for (V n : g.getNodes()) {
				StyledNode sn = this.decorator.decorateNode(n);
				if (sns.contains(sn.getName())) continue;
				else sns.add(sn.getName());
				
				this.nodeTemplate(sb, sn);

				// Add source or sink if necessary
				StyledEdge extremity = this.decorator.addSinkEdge(n, sn);
				if (extremity != null) {
					this.sink.add(extremity);
				}
				extremity = this.decorator.addSourceEdge(n, sn);
				if (extremity != null) {
					this.source.add(extremity);
				}

				// Output temporary results to file
				if (SB_NODES == ++i) {
					print(sb);
					i = 0;
				}
			}

			print(sb);
			i = 0;

			// Add edges
			for (E e : g.getEdges()) {
				StyledEdge se = this.decorator.decorateEdge(e, null);
				this.edgeTemplate(sb, se, directed);
				if ((SB_NODES-1)/3 == ++i) {
					print(sb);
					i = 0;
				}
			}
			print(sb);
		}

		for (Cluster<V,E> subc : clusters.getChildren(c)) {
			sb.append(tab);
			sb.append("subgraph \"cluster_");
			sb.append(subc.getName());
			sb.append("\" {");
			tab.increase();
			sb.append(tab); sb.append("label=\"");
			sb.append(subc.getName());
			sb.append("\"; labeljust=l;");
			sb.append(tab); sb.append("style=dashed; fontcolor=\"dimgray\";\n");
			print(sb);

			clusterContents(subc, clusters);
			tab.decrease();

			sb.append(tab); sb.append("}\n");
			print(sb);
		}
	}
	
	private void graphContents(PTGraph<V,E> input) throws IOException {
		Tree<Cluster<V,E>> clusters;

		if (this.cluster) {
			 clusters = PTGraph.partition(input, decorator);
		}
		else {
			clusters = new Tree<Cluster<V,E>>();
			clusters.add(new Cluster<V,E>(Category.NO_CATEGORY, input));
		}
		
		clusterContents(clusters.getRoot(), clusters);
	}

	@Override
	protected void convert() throws IOException {
		this.sink = new FastArrayList<StyledEdge>();
		this.source = new FastArrayList<StyledEdge>();
		
		StringBuilder sb = new StringBuilder(200);
		sb.append(decorator.isDirected() ? "digraph" : "graph");
		sb.append(" G {");
		tab.increase();
		
		if (this.horizontal) {
			sb.append(tab);
			sb.append("rankdir=\"LR\";\n");
		}
		print(sb);

		this.graphContents(graph);
		
		if (!sink.isEmpty()) {
			this.nodeTemplate(sb, sink.get(0).getTo());
			for (StyledEdge edge : sink) {
				this.edgeTemplate(sb, edge, decorator.isDirected());
			}
		}
		if (!source.isEmpty()) {
			this.nodeTemplate(sb, source.get(0).getFrom());
			for (StyledEdge edge : source) {
				this.edgeTemplate(sb, edge, decorator.isDirected());
			}
		}
		
		tab.decrease();
		sb.append(tab);
		sb.append("}\n");
		print(sb);
	}
	
	private static String join(Iterable<? extends CharSequence> s, String delimiter) {
		StringBuilder buffer = new StringBuilder();
		Iterator<? extends CharSequence> iter = s.iterator();
		if (iter.hasNext()) {
			buffer.append(iter.next());
			while (iter.hasNext()) {
				buffer.append(delimiter);
				buffer.append(iter.next());
			}
		}
		return buffer.toString();
    }
}
