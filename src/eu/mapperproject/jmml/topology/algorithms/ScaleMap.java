package eu.mapperproject.jmml.topology.algorithms;

import eu.mapperproject.jmml.definitions.ScaleSet;
import eu.mapperproject.jmml.topology.CouplingTopology;
import eu.mapperproject.jmml.topology.Instance;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JPanel;

/**
 * Represent the scales of a coupling topology
 */
public class ScaleMap extends JPanel {
	private Rectangle2D bounds;
	private Collection<NamedRectangle> scales;
	private final static int margin = 50;
	private final static Color[] colorwheel = {Color.red, Color.blue, Color.yellow.darker(), Color.cyan, Color.orange, Color.pink, Color.gray, Color.black};

	public ScaleMap(Dimension d, CouplingTopology topology) {
		this.addScales(topology.getInstances());
		this.setDoubleBuffered(true);
	}

	@Override
	public void paint(Graphics g) {
		Dimension csize = this.getSize();
		Dimension axesDim = new Dimension(csize.width - margin, csize.height - margin);
		Rectangle2D rectTransform = new Rectangle2D.Double(
				bounds.getX(),
				bounds.getY(),
				axesDim.width / bounds.getWidth(),
				axesDim.height / bounds.getHeight()
				);

		int icolor = 0;
		for (NamedRectangle inst : scales) {
			Rectangle sc = inst.getRect(rectTransform);
			g.setColor(colorwheel[icolor++]);
			if (icolor == colorwheel.length) icolor = 0;
			g.drawRect(sc.x + margin/2, axesDim.height - sc.y + margin/2, sc.width, sc.height);
			g.drawString(inst.getName(), sc.x + margin/2 + 5, axesDim.height - sc.y + margin/2 + 15);
		}
	}

	private void addScales(Collection<Instance> insts) {
		double x = Double.POSITIVE_INFINITY,
			y = Double.POSITIVE_INFINITY,
			xmax = Double.NEGATIVE_INFINITY,
			ymax = Double.NEGATIVE_INFINITY;
		this.scales = new ArrayList<NamedRectangle>((insts.size()));
		for (Instance inst : insts) {
			ScaleSet sc = inst.getScales();
			Rectangle2D scBounds = sc.getBounds();
			System.out.println(scBounds);
			if (scBounds != null) {
				if (scBounds.getX() < x) x = scBounds.getX();
				if (scBounds.getMaxX() > xmax) xmax = scBounds.getMaxX();
				if (scBounds.getY() < y) y = scBounds.getY();
				if (scBounds.getMaxY() > ymax) ymax = scBounds.getMaxY();
				this.scales.add(new NamedRectangle(inst.getId(), scBounds));
			}
		}
		this.bounds = new Rectangle2D.Double(x, y, xmax - x, ymax - y);
	}

	private class NamedRectangle {
		private final String name;
		private final Rectangle2D rect;

		NamedRectangle(String name, Rectangle2D rect) {
			this.name = name;
			this.rect = rect;
		}
		String getName() {
			return name;
		}
		Rectangle getRect(Rectangle2D scaling) {
			int x = (int)Math.round((rect.getX() - scaling.getX()) * scaling.getWidth());
			int y = (int)Math.round((rect.getY() - scaling.getY() + rect.getHeight()) * scaling.getHeight());
			int w = (int)Math.round(rect.getWidth() * scaling.getWidth());
			int h = (int)Math.round(rect.getHeight() * scaling.getHeight());
			if (w == 0) w = 1;
			if (h == 0) h = 1;
			return new Rectangle(x, y, w, h);
		}
	}
}
