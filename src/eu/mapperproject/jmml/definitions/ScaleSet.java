package eu.mapperproject.jmml.definitions;

import eu.mapperproject.jmml.util.numerical.SIRange;
import eu.mapperproject.jmml.util.numerical.SIUnit;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
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
	public Rectangle2D getBounds() {
		float[] xw = getBounds(this.time);
		if (xw == null) return null;
		float[] yh = getBounds(this.space.values());
		if (yh == null) {
			yh = getBounds(this.other.values());
			if (yh == null) return null;
		}
		return new Rectangle2D.Float(xw[0], yh[0], xw[1], yh[1]);
	}

	/**
	 * Get the maximal range of the collection of scales.
	 * The first element returned is the x-coordinate and the second the width.
	 */
	private static float[] getBounds(Collection<Scale> scales) {
		float[] yh, yhMax = new float[2];
		boolean hasScale = false;
		for (Scale scale : scales) {
			yh = getBounds(scale);
			if (yh != null && (yh[1] > yhMax[1] || (yh[1] == yhMax[1] && yh[0] > yhMax[0]))) {
				yhMax = yh;
				hasScale = true;
			}
		}
		return hasScale ? yhMax : null;
	}

	/**
	 * Get the range of a scales.
	 * The first element returned is the x-coordinate and the second the width.
	 * If either is not set, width is 0.
	 * If neither are set, null is returned.
	 */
	private static float[] getBounds(Scale scale) {
		float[] d = new float[2];
		SIUnit delta = null, max = null;
		SIRange sirange;

		sirange = scale.getDelta();
		if (sirange != null) {
			delta = sirange.getMean();
			if (delta != null) {
				d[0] = d[1] = (float)delta.log10();
			}
		}
		sirange = scale.getMax();
		if (sirange != null) {
			max = sirange.getMean();
			if (max != null) {
				d[1] = (float)max.log10();
				if (delta == null) d[0] = d[1];
			}
		}
		// No temporal dimension
		if (delta == null && max == null) {
			return null;
		}
		else {
			d[1] -= d[0];
			return d;
		}
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
