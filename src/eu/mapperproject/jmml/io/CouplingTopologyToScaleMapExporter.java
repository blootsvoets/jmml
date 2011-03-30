package eu.mapperproject.jmml.io;

import eu.mapperproject.jmml.topology.CouplingTopology;
import eu.mapperproject.jmml.topology.algorithms.ScaleMap;
import java.awt.Dimension;
import javax.swing.JFrame;

/**
 * Converts a coupling topology to a scale separation map.
 * @author Joris Borgdorff
 */
public class CouplingTopologyToScaleMapExporter {
	private final ScaleMap sm;

	public CouplingTopologyToScaleMapExporter(CouplingTopology ct) {
		JFrame frame = new JFrame();
		Dimension dim = new Dimension(500, 500);
		frame.setSize(dim);
		this.sm = new ScaleMap(dim, ct);
		frame.add(sm);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
