package eu.mapperproject.jmml.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 * A JPanel that is painted by a painter
 * @author Joris Borgdorff
 */
public class PaintedPanel extends JPanel {
	private final Painter painter;

	public PaintedPanel(Painter p) {
		this.painter = p;
		this.setDoubleBuffered(true);
	}

	@Override
	public void paint(Graphics g) {
		this.painter.paint((Graphics2D)g, this.getSize());
	}
}
