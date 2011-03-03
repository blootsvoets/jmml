package eu.mapperproject.xmml.io;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import eu.mapperproject.xmml.util.Indent;
import eu.mapperproject.xmml.util.graph.Category;
import eu.mapperproject.xmml.util.graph.Cluster;
import eu.mapperproject.xmml.util.graph.GraphvizEdge;
import eu.mapperproject.xmml.util.graph.GraphvizNode;
import eu.mapperproject.xmml.util.graph.PTGraph;
import eu.mapperproject.xmml.util.graph.SimpleNode;
import eu.mapperproject.xmml.util.graph.Tree;

/**
 * Can export a PTGraph to graphviz
 * @author Joris Borgdorff
 */
public class GraphToGraphvizExporter {
	private final Indent tab;
	private final boolean cluster, horizontal, edgeLabel;
	private boolean hasStart, hasEnd;
	private final static String DOT_EXEC = "/usr/local/bin/dot";
	
	public GraphToGraphvizExporter(boolean cluster, boolean horizontal, boolean edgeLabel) {
		this.tab = new Indent(4);
		this.cluster = cluster;
		this.horizontal = horizontal;
		this.edgeLabel = edgeLabel;
	}
	
	public GraphToGraphvizExporter() {
		this(false, false, true);
	}

	public void export(PTGraph<GraphvizNode, GraphvizEdge> input, File f) throws IOException {
		PrintStream out = null;
		try {
			out = new PrintStream(f);
			this.export(input, out);
			out.close();
		} finally {
			if (out != null)
				out.close();	
		}
	}

	public void export(PTGraph<GraphvizNode, GraphvizEdge> input, PrintStream out) {
		out.println(this.convert(input));
	}

	public void print(PTGraph<GraphvizNode, GraphvizEdge> input) {
		this.export(input, System.out);
	}

	public void export(PTGraph<GraphvizNode, GraphvizEdge> input, File f, File pdf) throws IOException, InterruptedException {
		System.out.println("Converting to PDF...");
		Runtime.getRuntime().exec(new String[] {DOT_EXEC, "-Tpdf", "-o" + pdf.getAbsolutePath(), f.getAbsolutePath()}).waitFor();
	}
	
	private String edgeTemplate(GraphvizEdge e, boolean directed) {
		String label = e.getLabel();
		String style = e.getStyle();
		String properties = "";
		
		if (this.edgeLabel && label != null) properties += "label=\"" + label + "\"";
		if (style != null) {
			if (properties.length() > 0) properties += ", ";
			properties += style;
		}
		if (properties.length() > 0) {
			properties = " [" + properties + ']';
		}
		
		String arrow = directed ? "->" : "--";
		GraphvizNode from = e.getFrom(), to = e.getTo();
		if (from == null) {
			from = SimpleNode.START;
			this.hasStart = true;
		}
		if (to == null) {
			to = SimpleNode.END;
			this.hasEnd = true;
		}

		return lne("\"" + from + "\" " + arrow + " \"" + to + "\"" + properties);
	}
	
	
	private String nodeTemplate(GraphvizNode n) {
		String style = n.getStyle();
		String properties = style == null ? "" : " [" + style + "]";
		
		return lne("\"" + n.getName() + "\"" + properties);
	}
		
	private String clusterContents(Cluster<GraphvizNode, GraphvizEdge> c, Tree<Cluster<GraphvizNode, GraphvizEdge>> clusters) {
		String ret = "";
		PTGraph<GraphvizNode, GraphvizEdge> g = c.getGraph();
		if (g != null) {
			for (GraphvizNode n : g.getNodes()) {
				ret += this.nodeTemplate(n);
			}

			for (GraphvizEdge e : g.getEdges()) {
				ret += this.edgeTemplate(e, g.isDirected());
			}
		}

		for (Cluster<GraphvizNode, GraphvizEdge> subc : clusters.getChildren(c)) {
			ret += ln("subgraph \"cluster_" + subc.getName() + "\" {");
			tab.increase();
			ret += lne("label=\"" + subc.getName() + "\",labeljust=l") + lne(subc.getStyle());
			ret += clusterContents(subc, clusters);
			tab.decrease();
			ret += ln("}") + ln();
		}
		
		return ret;
	}
	
	private String graphContents(PTGraph<GraphvizNode, GraphvizEdge> input) {
		Tree<Cluster<GraphvizNode, GraphvizEdge>> clusters;

		if (this.cluster) {
			 clusters = PTGraph.partition(input);
		}
		else {
			clusters = new Tree<Cluster<GraphvizNode, GraphvizEdge>>();
			clusters.add(new Cluster<GraphvizNode, GraphvizEdge>(Category.NO_CATEGORY, input));
		}
		
		return clusterContents(clusters.getRoot(), clusters);
	}
	
	public String convert(PTGraph<GraphvizNode, GraphvizEdge> input) {
		this.hasStart = false;
		this.hasEnd = false;

		String g = input.isDirected() ? "digraph" : "graph";

		String ret = g + " G {";
		tab.increase();
		
		if (this.horizontal) {
			ret += lne("rankdir=\"LR\"");
		}

		ret += this.graphContents(input);
		
		if (this.hasStart) ret += nodeTemplate(SimpleNode.START);
		if (this.hasEnd) ret += nodeTemplate(SimpleNode.END);
		
		tab.decrease();
		ret += ln("}");
		
		return ret;
	}
	
	private String lne(String line) {
		return tab + line + ";";
	}
	
	private String ln(String line) {
		return tab + line;
	}

	private String ln() {
		return "\n";
	}
}
