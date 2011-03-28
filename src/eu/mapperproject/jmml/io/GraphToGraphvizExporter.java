package eu.mapperproject.jmml.io;

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

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Can export a PTGraph to graphviz
 * @author Joris Borgdorff
 */
public class GraphToGraphvizExporter<V, E extends Edge<V>> {
	private final Indent tab;
	private final boolean cluster, horizontal, edgeLabel;
	private final static String DOT_EXEC = "/usr/local/bin/dot";
	private final static int SB_NODES = 1000;
	private final GraphDecorator<V, E> decorator;
	private Writer out;
	private final PTGraph<V,E> graph;
	private boolean hasSink, hasSource;
	
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

	public void export(File f) throws IOException {
		Writer fout = null;
		try {
			fout = new OutputStreamWriter(new FileOutputStream(f));
			this.export(fout);
			fout.close();
		} finally {
			if (fout != null)
				fout.close();
		}
	}

	public void export(Writer out) throws IOException {
		this.out = out;
		this.convert();
	}

	public void print() {
		try {
			this.export(new OutputStreamWriter(System.out));
		} catch (IOException ex) {
			Logger.getLogger(GraphToGraphvizExporter.class.getName()).log(Level.SEVERE, "System.out could not be used for writing: {}", ex);
		}
	}

	public void export(File f, File pdf) throws IOException, InterruptedException {
		this.export(f);
		System.out.println("Converting to PDF...");
		String[] dot = {DOT_EXEC, "-Tpdf", "-o" + pdf.getAbsolutePath(), f.getAbsolutePath()};
		Runtime.getRuntime().exec(dot).waitFor();
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
			for (V n : g.getNodes()) {
				StyledNode sn = this.decorator.decorateNode(n);
				this.nodeTemplate(sb, sn);
				StyledEdge extremity = this.decorator.addSinkEdge(n, sn);
				if (extremity != null) {
					if (!this.hasSink) {
						this.hasSink = true;
						this.nodeTemplate(sb, extremity.getTo());
					}
					this.edgeTemplate(sb, extremity, directed);
				}
				extremity = this.decorator.addSourceEdge(n, sn);
				if (extremity != null) {
					if (!this.hasSource) {
						this.hasSource = true;
						this.nodeTemplate(sb, extremity.getFrom());
					}
					this.edgeTemplate(sb, extremity, directed);
				}

				if (SB_NODES == ++i) {
					print(sb);
					i = 0;
				}
			}

			print(sb);
			i = 0;

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
			sb.append("subgraph \"cluster_");
			sb.append(subc.getName());
			sb.append("\" {");
			tab.increase();
			sb.append(tab); sb.append("label=\"");
			sb.append(subc.getName());
			sb.append("\",labeljust=l"); sb.append(';');
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
	
	public void convert() throws IOException {
		this.hasSink = this.hasSource = false;
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
