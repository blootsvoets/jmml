package eu.mapperproject.xmml.io;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import eu.mapperproject.xmml.util.Indent;
import eu.mapperproject.xmml.util.graph.Category;
import eu.mapperproject.xmml.util.graph.Cluster;
import eu.mapperproject.xmml.util.graph.StyledEdge;
import eu.mapperproject.xmml.util.graph.StyledNode;
import eu.mapperproject.xmml.util.graph.PTGraph;
import eu.mapperproject.xmml.util.graph.Tree;

/**
 * Can export a PTGraph to graphviz
 * @author Joris Borgdorff
 */
public class GraphToGraphvizExporter {
	private final Indent tab;
	private final boolean cluster, horizontal, edgeLabel;
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

	public void export(PTGraph<StyledNode, StyledEdge> input, File f) throws IOException {
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

	public void export(PTGraph<StyledNode, StyledEdge> input, PrintStream out) {
		out.println(this.convert(input));
	}

	public void print(PTGraph<StyledNode, StyledEdge> input) {
		this.export(input, System.out);
	}

	public void export(PTGraph<StyledNode, StyledEdge> input, File f, File pdf) throws IOException, InterruptedException {
		this.export(input, f);
		System.out.println("Converting to PDF...");
		Runtime.getRuntime().exec(new String[] {DOT_EXEC, "-Tpdf", "-o" + pdf.getAbsolutePath(), f.getAbsolutePath()}).waitFor();
	}
	
	private String edgeTemplate(StyledEdge e, boolean directed) {
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

		return lne("\"" + e.getFrom() + "\" " + arrow + " \"" + e.getTo() + "\"" + properties);
	}
	
	
	private String nodeTemplate(StyledNode n) {
		String style = n.getStyle();
		String properties = style == null ? "" : " [" + style + "]";
		
		return lne("\"" + n.getName() + "\"" + properties);
	}
		
	private String clusterContents(Cluster<StyledNode, StyledEdge> c, Tree<Cluster<StyledNode, StyledEdge>> clusters) {
		String ret = "";
		PTGraph<StyledNode, StyledEdge> g = c.getGraph();
		if (g != null) {
			for (StyledNode n : g.getNodes()) {
				ret += this.nodeTemplate(n);
			}

			for (StyledEdge e : g.getEdges()) {
				ret += this.edgeTemplate(e, g.isDirected());
			}
		}

		for (Cluster<StyledNode, StyledEdge> subc : clusters.getChildren(c)) {
			ret += ln("subgraph \"cluster_" + subc.getName() + "\" {");
			tab.increase();
			ret += lne("label=\"" + subc.getName() + "\",labeljust=l") + lne(subc.getStyle());
			ret += clusterContents(subc, clusters);
			tab.decrease();
			ret += ln("}") + ln();
		}
		
		return ret;
	}
	
	private String graphContents(PTGraph<StyledNode, StyledEdge> input) {
		Tree<Cluster<StyledNode, StyledEdge>> clusters;

		if (this.cluster) {
			 clusters = PTGraph.partition(input);
		}
		else {
			clusters = new Tree<Cluster<StyledNode, StyledEdge>>();
			clusters.add(new Cluster<StyledNode, StyledEdge>(Category.NO_CATEGORY, input));
		}
		
		return clusterContents(clusters.getRoot(), clusters);
	}
	
	public String convert(PTGraph<StyledNode, StyledEdge> input) {
		String g = input.isDirected() ? "digraph" : "graph";

		String ret = g + " G {";
		tab.increase();
		
		if (this.horizontal) {
			ret += lne("rankdir=\"LR\"");
		}

		ret += this.graphContents(input);
		
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
