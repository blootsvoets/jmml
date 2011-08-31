package eu.mapperproject.jmml.topology.algorithms;

import cern.colt.list.IntArrayList;
import eu.mapperproject.jmml.topology.ScaleSet;
import eu.mapperproject.jmml.specification.Instance;
import eu.mapperproject.jmml.specification.Submodel;
import eu.mapperproject.jmml.specification.annotated.AnnotatedInstance;
import eu.mapperproject.jmml.specification.annotated.AnnotatedTopology;
import eu.mapperproject.jmml.util.ArraySet;
import eu.mapperproject.jmml.util.Painter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represent the scales of a coupling topology
 */
public class ScaleMap implements Painter {
	private enum Alignment {
		CENTER, LEFT, RIGHT;
	}
	private Rectangle2D bounds;
	private final Collection<NamedRectangle> scales;
	private final static int prefTicks = 5;
	private final static int maxTicks = 8;
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
	private final IntArrayList tickPlacement;

	public ScaleMap(AnnotatedTopology topology) {
		this.bounds = null;
		this.scales = new ArrayList<NamedRectangle>();
		this.tickPlacement = new IntArrayList();

		this.addScales(topology.getInstance());
	}

	@Override
	public void paint(Graphics2D g, Dimension dim) {
		Dimension drawDim = new Dimension(dim.width - margin, dim.height - margin);
		this.paintAxes(g, dim, drawDim);
		Rectangle2D rectTransform = new Rectangle2D.Double(
				bounds.getX(),
				bounds.getY(),
				drawDim.width / bounds.getWidth(),
				drawDim.height / bounds.getHeight()
				);

		int icolor = 0;
		float[] rgb = new float[3];
		final int originy = drawDim.height + marginSide;
		for (NamedRectangle inst : scales) {
			Rectangle sc = inst.getRect(rectTransform);
			Color color = colorwheel[icolor++];
			if (icolor == colorwheel.length) icolor = 0;
			g.setColor(color);
			
			g.drawRect(sc.x + marginSide, originy - sc.y, sc.width, sc.height);
			g.drawString(inst.getName(), sc.x + marginSide + 5, originy - sc.y + 15);

			// Re-use the same color to fill but transparently
			color.getRGBColorComponents(rgb);
			color = new Color(rgb[0], rgb[1], rgb[2], 0.1f);
			g.setColor(color);
			g.fillRect(sc.x + marginSide, originy - sc.y, sc.width, sc.height);
		}
	}

	private void paintAxes(Graphics2D g, Dimension dim, Dimension drawDim) {
		Dimension axesDim = new Dimension(dim.width - marginAxes, dim.height - marginAxes);
		double boundsWidth = this.bounds.getWidth() * axesDim.width / (double)drawDim.width;
		double boundsHeight = this.bounds.getHeight() * axesDim.height / (double)drawDim.height;
		Rectangle2D axesBounds = new Rectangle2D.Double(
				this.bounds.getX() - (boundsWidth - this.bounds.getWidth()) / 2d,
				this.bounds.getY() - (boundsHeight - this.bounds.getHeight()) / 2d,
				boundsWidth, boundsHeight);
		final int baseX = marginAxesSide;
		final int baseY = marginAxesSide + axesDim.height;
		tickPlacement.clear();

		// paint x axes
		double baseMult = axesDim.width / axesBounds.getWidth();
		double base0 = baseMult * -axesBounds.getX();
		g.drawLine(baseX, baseY, baseX + axesDim.width, baseY);

		// Calculate tick placement
		// When no or one tick is applicable
		if (axesBounds.getWidth() < 1d) {
			int closest = (int)Math.round(axesBounds.getCenterX());
			tickPlacement.add(closest);
			if (axesBounds.getX() > closest || axesBounds.getMaxX() < closest) {
				base0 = axesDim.width / 2d;
				baseMult = Double.NaN;
			}
		}
		// When multiple ticks are applicable
		else {
			// Allow a small margin around the drawn area to also show ticks in proximity
			double from = Math.max(axesBounds.getX(), Math.floor(this.bounds.getX() - .5d));
			double to = Math.min(axesBounds.getMaxX(), Math.ceil(this.bounds.getMaxX() + .5d));

			if (to - from >= prefTicks) {
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
			// Add ticks while there are time units left
			for (int i = 0; i < prefixTime.length; i++) {
				if (prefixTimeLog[i] < from) continue;
				if (prefixTimeLog[i] > to) break;
				tickPlacement.add(i + 1);
			}
		}
		// Remove superfluous ticks
		while (tickPlacement.size() > maxTicks) {
			for (int i = tickPlacement.size() - 2; i > 0; i -= 2) {
				if (tickPlacement.get(i) != 0)
					tickPlacement.delete(i);
			}
		}

		// Assign labels to ticks and draw them
		int baseTicks = marginAxesSide;
		for (int i = 0; i < tickPlacement.size(); i++) {
			int t = tickPlacement.getQuick(i);
			String text;
			if (t <= -3) {
				int prefixIndex = -(int)Math.floor(t/3d) - 1;
				if (prefixIndex < prefixMin24.length) {
					text = prefixMin24[prefixIndex] + "s";
					int mod = t % 3;
					if (mod == -2) text = "10 " + text;
					if (mod == -1) text = "100 " + text;
				}
				else {
					text = "10^" + t + " s";
				}
			}
			else if (t == 0) {
				text = "s";
			}
			else {
				// Can not overflow, as method of adding ticks is based on prefixTime array.
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

		// paint y axis
		baseMult = axesDim.height / axesBounds.getHeight();
		base0 = baseMult * -axesBounds.getY();
		g.drawLine(baseX, baseY, baseX, baseY - axesDim.height);
		tickPlacement.clear();

		// Calculate tick placement
		// When no or one tick is applicable
		if (axesBounds.getHeight() < 1d) {
			int closest = (int)Math.round(axesBounds.getCenterY());
			tickPlacement.add(closest);
			if (axesBounds.getY() > closest || axesBounds.getMaxY() < closest) {
				base0 = axesDim.height / 2d;
				baseMult = Double.NaN;
			}
		}
		// When multiple ticks are applicable
		else {
			// Allow a small margin around the drawn area to also show ticks in proximity
			int from = Math.max((int)Math.ceil(axesBounds.getY()), (int)Math.floor(this.bounds.getY() - .5d));
			int to = Math.min((int)Math.floor(axesBounds.getMaxY()), (int)Math.ceil(this.bounds.getMaxY() + .5d));
			
			if (to - from >= prefTicks) {
				if (from <= -3) {
					int start = -(int)Math.ceil(from/3d);
					int stop = -Math.min(0, (int)Math.floor(to / 3d));
					for (int i = start; i >= stop; i--) {
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
		// Remove superfluous ticks
		while (tickPlacement.size() > maxTicks) {
			for (int i = tickPlacement.size() - 2; i > 0; i -= 2) {
				if (tickPlacement.get(i) != 0)
					tickPlacement.delete(i);
			}
		}

		// Assign labels to ticks and draw them
		baseTicks = marginAxesSide + axesDim.height;
		for (int i = 0; i < tickPlacement.size(); i++) {
			int t = tickPlacement.getQuick(i);
			
			String text;
			if (t <= -3) {
				int prefixIndex = -(int)Math.floor(t/3d) - 1;
				if (prefixIndex < prefixMin24.length) {
					text = prefixMin24[prefixIndex] + "m";
				}
				else {
					text = "10^" + t + " m";
				}
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
				int prefixIndex = t/3 - 1;
				if (prefixIndex < prefixPlus24.length) {
					text = prefixPlus24[prefixIndex] + "m";
				}
				else {
					text = "10^" + t + " m";
				}
				int mod = t % 3;
				if (mod == 1) text = "10 " + text;
				if (mod == 2) text = "100 " + text;
			}

			// No ticks
			if (baseMult == Double.NaN) {
				drawString(text, g, Alignment.RIGHT, baseX - 5, baseTicks + (int)Math.round(base0) + 5);
			}
			else {
				int baseT = baseTicks - (int)Math.round(base0 + baseMult*t);
				g.drawLine(baseX - 3, baseT, baseX + 3, baseT);
				drawString(text, g, Alignment.RIGHT, baseX - 5, baseT + 5);
			}
		}
	}

	private void addScales(Collection<Instance> insts) {
		Collection<Submodel> visited = new ArraySet<Submodel>();
		for (Instance inst : insts) {
			AnnotatedInstance ainst = (AnnotatedInstance)inst;
			if (ainst.ofSubmodel()) {
				Submodel sub = ainst.getSubmodelInstance();
				if (visited.contains(sub)) continue;
				if (tryAddScale(new ScaleSet(ainst.getTimescaleInstance(), ainst.getSpacescaleInstance(), ainst.getOtherscaleInstance()), sub.getId())) {
					visited.add(sub);
				}
			}
		}
	}

	private boolean tryAddScale(ScaleSet sc, String name) {
		Rectangle2D scBounds = sc.getBounds();
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
		int chooseX = x;
		if (align != Alignment.LEFT) {
			Rectangle2D bounding = g.getFontMetrics().getStringBounds(text, g);
			if (align == Alignment.CENTER) {
				chooseX -= (int)Math.round(bounding.getCenterX());
			}
			else {
				chooseX -= (int)Math.round(bounding.getWidth());
			}
		}
		g.drawString(text, chooseX, y);
	}
}
