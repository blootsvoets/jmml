package eu.mapperproject.jmml.io;

import eu.mapperproject.jmml.specification.annotated.AnnotatedTopology;
import eu.mapperproject.jmml.topology.algorithms.ScaleMap;
import eu.mapperproject.jmml.util.PaintedPanel;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JFrame;

/**
 * Converts a coupling topology to a scale separation map.
 * @author Joris Borgdorff
 */
public class CouplingTopologyToScaleMapExporter extends AbstractExporter {
	private final ScaleMap sm;

	public CouplingTopologyToScaleMapExporter(AnnotatedTopology ct) {
		this.sm = new ScaleMap(ct);
	}

	public void display() {
		JFrame frame = new JFrame();
		Dimension dim = new Dimension(500, 500);
		frame.setSize(dim);
		PaintedPanel p = new PaintedPanel(this.sm);
		frame.add(p);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	@Override
	protected void convert() throws IOException {
		// Get a DOMImplementation.
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // Ask the test to render into the SVG Graphics2D implementation.
        sm.paint(svgGenerator, new Dimension(500, 500));

        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
        svgGenerator.stream(out, useCSS);
	}
}
