package eu.mapperproject.jmml.util;

import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 * Paints on a graphics object within a certain size
 * @author Joris Borgdorff
 */
public interface Painter {
	/**
	 * Paint using given Graphics2D object within the boundaries of given Dimension.
	 */
	public void paint(Graphics2D g, Dimension d);
}
