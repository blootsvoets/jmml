package eu.mapperproject.xmml.io;

import java.io.File;
import java.io.IOException;

import eu.mapperproject.xmml.util.Indent;
import eu.mapperproject.xmml.util.graph.Category;
import eu.mapperproject.xmml.util.graph.Cluster;
import eu.mapperproject.xmml.util.graph.StyledEdge;
import eu.mapperproject.xmml.util.graph.StyledNode;
import eu.mapperproject.xmml.util.graph.PTGraph;
import eu.mapperproject.xmml.util.graph.Tree;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Can export a PTGraph to graphviz
 * @author Joris Borgdorff
 */
public class GraphToGraphvizExporter {
	private final Indent tab;
	private final boolean cluster, horizontal, edgeLabel;
	private final static String DOT_EXEC = "/usr/local/bin/dot";
	private final static int SB_NODES = 1000;
	private Writer out;
	
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
		Writer fout = null;
		try {
			fout = new OutputStreamWriter(new FileOutputStream(f));
			this.export(input, fout);
			fout.close();
		} finally {
			if (fout != null)
				fout.close();
		}
	}

	public void export(PTGraph<StyledNode, StyledEdge> input, Writer out) throws IOException {
		this.out = out;
		this.convert(input);
	}

	public void print(PTGraph<StyledNode, StyledEdge> input) {
		try {
			this.export(input, new OutputStreamWriter(System.out));
		} catch (IOException ex) {
			Logger.getLogger(GraphToGraphvizExporter.class.getName()).log(Level.SEVERE, "System.out could not be used for writing", ex);
		}
	}

	public void export(PTGraph<StyledNode, StyledEdge> input, File f, File pdf) throws IOException, InterruptedException {
		this.export(input, f);
		System.out.println("Converting to PDF...");
		Runtime.getRuntime().exec(new String[] {DOT_EXEC, "-Tpdf", "-o" + pdf.getAbsolutePath(), f.getAbsolutePath()}).waitFor();
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
		
	private void clusterContents(Cluster<StyledNode, StyledEdge> c, Tree<Cluster<StyledNode, StyledEdge>> clusters) throws IOException {
		StringBuilder sb = new StringBuilder(SB_NODES*30);
		PTGraph<StyledNode, StyledEdge> g = c.getGraph();
		int i = 0;
		if (g != null) {
			for (StyledNode n : g.getNodes()) {
				this.nodeTemplate(sb, n);
				if (SB_NODES == ++i) {
					print(sb);
					i = 0;
				}
			}

			print(sb);
			i = 0;

			for (StyledEdge e : g.getEdges()) {
				this.edgeTemplate(sb, e, g.isDirected());
				if ((SB_NODES-1)/3 == ++i) {
					print(sb);
					i = 0;
				}
			}
			print(sb);
		}

		for (Cluster<StyledNode, StyledEdge> subc : clusters.getChildren(c)) {
			sb.append("subgraph \"cluster_");
			sb.append(subc.getName());
			sb.append("\" {");
			tab.increase();
			sb.append(tab); sb.append("label=\"");
			sb.append(subc.getName());
			sb.append("\",labeljust=l"); sb.append(';');
			sb.append(tab); sb.append(subc.getStyle()); sb.append(";\n");
			print(sb);

			clusterContents(subc, clusters);
			tab.decrease();

			sb.append(tab); sb.append("}\n");
			print(sb);
		}
	}
	
	private void graphContents(PTGraph<StyledNode, StyledEdge> input) throws IOException {
		Tree<Cluster<StyledNode, StyledEdge>> clusters;

		if (this.cluster) {
			 clusters = PTGraph.partition(input);
		}
		else {
			clusters = new Tree<Cluster<StyledNode, StyledEdge>>();
			clusters.add(new Cluster<StyledNode, StyledEdge>(Category.NO_CATEGORY, input));
		}
		
		clusterContents(clusters.getRoot(), clusters);
	}
	
	public void convert(PTGraph<StyledNode, StyledEdge> input) throws IOException {
		StringBuilder sb = new StringBuilder(200);
		sb.append(input.isDirected() ? "digraph" : "graph");
		sb.append(" G {");
		tab.increase();
		
		if (this.horizontal) {
			sb.append(tab);
			sb.append("rankdir=\"LR\";\n");
		}
		print(sb);

		this.graphContents(input);
		
		tab.decrease();
		sb.append(tab);
		sb.append("}\n");
		print(sb);
	}

	private void print(StringBuilder sb) throws IOException {
		this.out.write(sb.toString());
		sb.setLength(0);
	}
}
