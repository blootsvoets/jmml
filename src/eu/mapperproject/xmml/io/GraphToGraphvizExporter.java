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
	
	private void edgeTemplate(StringBuilder edge, StyledEdge e, boolean directed) {
		edge.append(tab);
		edge.append('"'); edge.append(e.getFrom()); edge.append('"');
		edge.append(directed ? "->" : "--");
		edge.append('"');
		edge.append(e.getTo());
		edge.append('"');

		String label = e.getLabel();
		String style = e.getStyle();

		if ((this.edgeLabel && label != null) || style != null) {
			edge.append('[');
			if (this.edgeLabel && label != null) {
				edge.append("label=\"");
				edge.append(label);
				edge.append('"');
				if (style != null) edge.append(',');
			}
			if (style != null) {
				edge.append(style);
			}
			edge.append(']');
		}

		edge.append(";\n");
	}
	
	
	private void nodeTemplate(StringBuilder node, StyledNode n) {
		String style = n.getStyle();
		node.append(tab);
		node.append('"');
		node.append(n.getName());
		node.append('"');
		if (style != null) {
			node.append('[');
			node.append(style);
			node.append(']');
		}
		node.append(";\n");
	}
		
	private String clusterContents(Cluster<StyledNode, StyledEdge> c, Tree<Cluster<StyledNode, StyledEdge>> clusters) {
		StringBuilder ret = new StringBuilder();
		PTGraph<StyledNode, StyledEdge> g = c.getGraph();
		if (g != null) {
			for (StyledNode n : g.getNodes()) {
				this.nodeTemplate(ret, n);
			}

			for (StyledEdge e : g.getEdges()) {
				this.edgeTemplate(ret, e, g.isDirected());
			}
		}

		for (Cluster<StyledNode, StyledEdge> subc : clusters.getChildren(c)) {
			ret.append("subgraph \"cluster_");
			ret.append(subc.getName());
			ret.append("\" {\n");
			tab.increase();
			ret.append(tab); ret.append("label=\"");
			ret.append(subc.getName());
			ret.append("\",labeljust=l"); ret.append(";\n");
			ret.append(tab); ret.append(subc.getStyle()); ret.append(";\n");
			ret.append(clusterContents(subc, clusters));
			tab.decrease();
			ret.append(tab); ret.append("}\n\n");
		}
		
		return ret.toString();
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
			ret += tab + "rankdir=\"LR\";\n";
		}

		ret += this.graphContents(input);
		
		tab.decrease();
		ret += tab + "}\n";
		
		return ret;
	}
}
