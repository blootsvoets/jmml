package eu.mapperproject.jmml.definitions;

import eu.mapperproject.jmml.util.numerical.SIUnit;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
/**
 * Stores and represents the scales of a submodel (instance)
 * @author Joris Borgdorff
 */
public class ScaleSet {
	private Map<String, Scale> all;
	private Scale time;
	private Map<String, Scale> space;
	private Map<String, Scale> other;

	public ScaleSet() {
		this.time = null;
		this.space = new TreeMap<String, Scale>();
		this.other = new TreeMap<String, Scale>();
		this.all = new TreeMap<String, Scale>();
	}

	public ScaleSet(Scale time, Map<String, Scale> space, Map<String, Scale> other) {
		this.time = time;
		this.space = space;
		this.other = other;
		this.all = new TreeMap<String, Scale>();
		this.all.put(time.getId(), time);
		this.all.putAll(space);
		this.all.putAll(other);
	}
	
	/** Put a scale in the scale map, replacing an old one with the same name */
	public void putScale(Scale s) {
		switch (s.getDimension()) {
		case TIME:
			time = s;
			break;
		case SPACE:
			space.put(s.getId(), s);
			break;
		default:
			other.put(s.getId(), s);
			break;
		}
		
		this.all.put(s.getId(), s);
	}
	
	/** Get any scale by id. Returns null if the scale is not found */
	public Scale getScale(String id) {
		return this.all.get(id);
	}

	/** Whether a scale by the given ID already exists in this scale map */
	public boolean hasScale(String id) {
		return this.all.containsKey(id);
	}
	
	/** Get the average number of timesteps using the current scale */
	public int getTimesteps() {
		return this.time.getSteps();
	}

	/**
	 * Get the bounds of the scaleset.
	 * Timescale is on the x-axis and spatial scale on the y-axis. If no spatial
	 * scale is preset, other scale is used. If this is also not present or timescale
	 * is not present, null is returned.
	 */
	public Rectangle2D.Double getBounds() {
		double[] xw = getBounds(this.time);
		if (xw == null) return null;
		double[] yh = getBounds(this.space.values());
		if (yh == null) {
			yh = getBounds(this.other.values());
			if (yh == null) return null;
		}
		return new Rectangle2D.Double(xw[0], yh[0], xw[1], yh[1]);
	}
	
	private static double[] getBounds(Collection<Scale> scales) {
		double[] yh, yhMax = new double[2];
		boolean hasScale = false;
		for (Scale scale : scales) {
			System.out.println(scale);
			yh = getBounds(scale);
			if (yh != null && (yh[1] > yhMax[1] || (yh[1] == yhMax[1] && yh[0] > yhMax[0]))) {
				yhMax = yh;
				hasScale = true;
			}
		}
		return hasScale ? yhMax : null;
	}

	private static double[] getBounds(Scale scale) {
		double[] d = new double[2];
		SIUnit max = scale.getMax().getMean();
		if (max != null) {
			d[1] = max.log10();
		}
		SIUnit delta = scale.getDelta().getMean();
		if (delta != null) {
			d[0] = delta.log10();
			if (max != null) d[1] -= d[0];
		}
		// No temporal dimension
		else if (max == null) {
			return null;
		}
		return d;
	}
	
	/** Copy references to each of the scales in the scale map to a new scale map */
	public ScaleSet copy() {
		ScaleSet copy = new ScaleSet();
		for (Scale s : this.all.values()) {
			copy.putScale(s);
		}
		return copy;
 	}
}
