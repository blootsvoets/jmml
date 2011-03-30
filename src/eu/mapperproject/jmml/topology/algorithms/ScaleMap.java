package eu.mapperproject.jmml.topology.algorithms;

import cern.colt.list.IntArrayList;
import eu.mapperproject.jmml.definitions.ScaleSet;
import eu.mapperproject.jmml.definitions.Submodel;
import eu.mapperproject.jmml.topology.CouplingTopology;
import eu.mapperproject.jmml.topology.Instance;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.JPanel;

/**
 * Represent the scales of a coupling topology
 */
public class ScaleMap extends JPanel {
	private enum Alignment {
		CENTER, LEFT, RIGHT;
	}
	private Rectangle2D bounds;
	private final Collection<NamedRectangle> scales;
	private final static int maxTicks = 5;
	private final static int marginAxes = 120;
	private final static int marginAxesSide = marginAxes/2;

	private final static int margin = 50 + marginAxes;
	private final static int marginSide = margin/2;

	private final static Color[] colorwheel = {Color.red, Color.blue, Color.yellow.darker(), Color.cyan, Color.orange, Color.pink, Color.gray, Color.black};
	private final static char[] prefixMin2 = {'d', 'c'};
	private final static char[] prefixMin24 = {'m', 'µ', 'n', 'p', 'f', 'a', 'z', 'y'};
	private final static String[] prefixPlus2 = {"da", "h"};
	private final static char[] prefixPlus24 = {'k', 'M', 'G', 'T', 'P', 'E', 'Z', 'Y'};
	private final static String[] prefixTime = {"min", "hr", "day", "wk", "mon", "yr", "dec.", "cent.", "mill."};
	private final static float[] prefixTimeLog = {1.77815125f, 3.556302501f, 4.936513742f, 5.781611782f, 6.419922721f, 7.498806607f, 8.498806607f, 9.498806607f, 10.498806607f};

	public ScaleMap(Dimension d, CouplingTopology topology) {
		this.bounds = null;
		this.scales = new ArrayList<NamedRectangle>();

		this.addScales(topology.getInstances());
		this.setDoubleBuffered(true);
	}

	@Override
	public void paint(Graphics g) {
		Dimension drawDim = new Dimension(this.getWidth() - margin, this.getHeight() - margin);
		this.paintAxes(g, drawDim);
		Rectangle2D rectTransform = new Rectangle2D.Double(
				bounds.getX(),
				bounds.getY(),
				drawDim.width / bounds.getWidth(),
				drawDim.height / bounds.getHeight()
				);

		int icolor = 0;
		final int originy = drawDim.height + marginSide;
		for (NamedRectangle inst : scales) {
			Rectangle sc = inst.getRect(rectTransform);
			g.setColor(colorwheel[icolor++]);
			if (icolor == colorwheel.length) icolor = 0;

			g.drawRect(sc.x + marginSide, originy - sc.y, sc.width, sc.height);
			g.drawString(inst.getName(), sc.x + marginSide + 5, originy - sc.y + 15);
		}
	}

	private void paintAxes(Graphics g, Dimension drawDim) {
		Dimension axesDim = new Dimension(this.getWidth() - marginAxes, this.getHeight() - marginAxes);
		IntArrayList tickPlacement = new IntArrayList();
		final int baseX = marginAxesSide;
		final int baseY = marginAxesSide + axesDim.height;

		// paint x
		double baseMult = drawDim.width / this.bounds.getWidth();
		double base0 = baseMult * -this.bounds.getX();
		g.drawLine(baseX, baseY, baseX + axesDim.width, baseY);

		if (this.bounds.getWidth() < 1d) {
			int closest = (int)Math.round(this.bounds.getCenterX());
			tickPlacement.add(closest);
			if (this.bounds.getX() > closest || this.bounds.getMaxX() < closest) {
				base0 = axesDim.width / 2d;
				baseMult = Double.NaN;
			}
		}
		else {
			double from = this.bounds.getX();
			double to = this.bounds.getMaxX();

			if (to - from >= maxTicks) {
				if (from <= -3) {
					int start = -(int)Math.ceil(from/3d);
					int stop = -Math.min(0, (int)Math.floor(to / 3d));
					for (int i = start; i > stop; i--) {
						tickPlacement.add(-i*3);
					}
				}
			}
			else {
				for (int i = (int)Math.ceil(from); i <= Math.min(to, 0d); i++) {
					tickPlacement.add(i);
				}
			}
			if (from <= 0 && to >= 0) {
				tickPlacement.add(0);
			}
			for (int i = 0; i < prefixTime.length; i++) {
				if (prefixTimeLog[i] < from) continue;
				if (prefixTimeLog[i] > to) break;
				tickPlacement.add(i + 1);
			}
		}

		int baseTicks = marginSide;
		for (int i = 0; i < tickPlacement.size(); i++) {
			int t = tickPlacement.getQuick(i);
			String text;
			if (t <= -3) {
				text = prefixMin24[-(int)Math.floor(t/3d) - 1] + "s";
				int mod = t % 3;
				if (mod == -2) text = "10 " + text;
				if (mod == -1) text = "100 " + text;
			}
			else if (t == 0) {
				text = "s";
			}
			else {
				text = prefixTime[t - 1];
			}

			// No ticks
			if (baseMult == Double.NaN) {
				drawString(text, g, Alignment.CENTER, baseTicks + (int)Math.round(base0), baseY + 18);
			}
			else {
				int baseT = baseTicks + (int)Math.round(base0 + baseMult*(t <= 0 ? t : prefixTimeLog[t - 1]));
				g.drawLine(baseT, baseY + 3, baseT, baseY - 3);
				drawString(text, g, Alignment.CENTER, baseT, baseY + 18);
			}
		}

		// paint y
		baseMult = drawDim.height / this.bounds.getHeight();
		base0 = baseMult * -this.bounds.getY();
		g.drawLine(baseX, baseY, baseX, baseY - axesDim.height);
		tickPlacement.clear();
		if (this.bounds.getHeight() < 1d) {
			int closest = (int)Math.round(this.bounds.getCenterY());
			tickPlacement.add(closest);
			if (this.bounds.getY() > closest || this.bounds.getMaxY() < closest) {
				base0 = axesDim.height / 2d;
				baseMult = Double.NaN;
			}
		}
		else {
			int from = (int)Math.ceil(this.bounds.getY());
			int to = (int)Math.floor(this.bounds.getMaxY());
			
			if (to - from >= maxTicks) {
				if (from <= -3) {
					int start = -(int)Math.ceil(from/3d);
					int stop = -Math.min(0, (int)Math.floor(to / 3d));
					for (int i = start; i > stop; i--) {
						tickPlacement.add(-i*3);
					}
				}
				if (from < 0) {
					int start = from;
					int stop = -Math.min(0, to);
					for (int i = start; i > stop; i--) {
						tickPlacement.add(-i);
					}
				}
				if (from <= 0 && to >= 0) {
					tickPlacement.add(0);
				}
				if (to >= 3) {
					int start = Math.max(1, (int)Math.ceil(from/3d));
					int stop = (int)Math.floor(to/3d);
					for (int i = start; i <= stop; i++) {
						tickPlacement.add(i*3);
					}
				}

			}
			else {
				for (int i = from; i <= to; i++) {
					tickPlacement.add(i);
				}
			}
		}

		baseTicks = marginSide + drawDim.height;
		for (int i = 0; i < tickPlacement.size(); i++) {
			int t = tickPlacement.getQuick(i);
			int baseT = baseTicks - (int)Math.round(base0 + baseMult*t);
			g.drawLine(baseX - 3, baseT, baseX + 3, baseT);

			String text;
			if (t <= -3) {
				text = prefixMin24[-(int)Math.floor(t/3d) - 1] + "m";
				int mod = t % 3;
				if (mod == -2) text = "10 " + text;
				if (mod == -1) text = "100 " + text;
			}
			else if (t < 0) {
				text = prefixMin2[-t - 1] + "m";
			}
			else if (t == 0) {
				text = "m";
			}
			else if (t <= 2) {
				text = prefixPlus2[t - 1] + "m";
			}
			else {
				text = prefixPlus24[t/3 - 1] + "m";
				int mod = t % 3;
				if (mod == 1) text = "10 " + text;
				if (mod == 2) text = "100 " + text;
			}

			drawString(text, g, Alignment.RIGHT, baseX - 5, baseT + 5);
		}
	}

	private void addScales(Collection<Instance> insts) {
		Collection<Submodel> visited = new HashSet<Submodel>();
		for (Instance inst : insts) {
			Submodel sub = inst.getSubmodel();
			if (visited.contains(sub)) continue;
			System.out.println(sub);
			if (tryAddScale(sub.getScaleMap(), sub.getId())
					|| tryAddScale(inst.getScales(), sub.getId())) {
				visited.add(sub);
			}
		}
	}

	private boolean tryAddScale(ScaleSet sc, String name) {
		Rectangle2D scBounds = sc.getBounds();
		System.out.println(scBounds);
		if (scBounds != null) {
			if (this.bounds == null) {
				this.bounds = new Rectangle2D.Float(
						(float)scBounds.getX(),
						(float)scBounds.getY(),
						(float)scBounds.getWidth(),
						(float)scBounds.getHeight());
			}
			else this.bounds.add(scBounds);
			this.scales.add(new NamedRectangle(name, scBounds));
			return true;
		}
		return false;
	}

	private static class NamedRectangle {
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


	private void drawString(String text, Graphics g, Alignment align, int x, int y) {
		if (align != Alignment.LEFT) {
			Rectangle2D bounding = g.getFontMetrics().getStringBounds(text, g);
			if (align == Alignment.CENTER) {
				x -= (int)Math.round(bounding.getCenterX());
			}
			else {
				x -= (int)Math.round(bounding.getWidth());
			}
		}
		g.drawString(text, x, y);
	}
}
