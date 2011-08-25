package eu.mapperproject.jmml.topology;

import eu.mapperproject.jmml.specification.annotated.AnnotatedScale;
import eu.mapperproject.jmml.util.numerical.SIUnit;
import eu.mapperproject.jmml.util.ArrayMap;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * Stores and represents the scales of a submodel (instance)
 * @author Joris Borgdorff
 */
public class ScaleSet {
	private Map<String, AnnotatedScale> all;
	private AnnotatedScale time;
	private List<? extends AnnotatedScale> space;
	private List<? extends AnnotatedScale> other;

	public ScaleSet(AnnotatedScale time, List<? extends AnnotatedScale> space, List<? extends AnnotatedScale> other) {
		this.time = time;
		this.space = space;
		this.other = other;
		this.all = new ArrayMap<String, AnnotatedScale>();
		this.all.put(time.getId(), time);
		for (AnnotatedScale sc : space) {
			this.all.put(sc.getId(), sc);
		}
		for (AnnotatedScale sc : other) {
			this.all.put(sc.getId(), sc);
		}
	}
	
	/** Get any scale by id. Returns null if the scale is not found */
	public AnnotatedScale getScale(String id) {
		return this.all.get(id);
	}

	/** Whether a scale by the given ID already exists in this scale map */
	public boolean hasScale(String id) {
		return this.all.containsKey(id);
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
		float[] yh = getBounds(this.space);
		if (yh == null) {
			yh = getBounds(this.other);
			if (yh == null) return null;
		}
		return new Rectangle2D.Float(xw[0], yh[0], xw[1], yh[1]);
	}

	/**
	 * Get the maximal range of the collection of scales.
	 * The first element returned is the x-coordinate and the second the width.
	 */
	private static float[] getBounds(Collection<? extends AnnotatedScale> scales) {
		float[] yh, yhMax = {Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY};

		boolean hasScale = false;
		for (AnnotatedScale scale : scales) {
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
	private static float[] getBounds(AnnotatedScale scale) {
		float[] d = new float[2];
		
		SIUnit delta = scale.getMeanDelta();
		if (delta != null) {
			d[0] = d[1] = (float)delta.log10();
		}
		
		SIUnit total = scale.getMeanTotal();
		if (total != null) {
			d[1] = (float)total.log10();
			if (delta == null) d[0] = d[1];
		}

		// No temporal dimension
		if (delta == null && total == null) {
			return null;
		}
		else {
			d[1] -= d[0];
			return d;
		}
	}
}
