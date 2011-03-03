/**
 * 
 */
package eu.mapperproject.xmml.definitions;

import eu.mapperproject.xmml.Identifiable;
import eu.mapperproject.xmml.util.SIRange;
import eu.mapperproject.xmml.util.SIUnit;
import eu.mapperproject.xmml.util.ScaleModifier.Dimension;

/**
 * Represents an xMML scale element
 * @author Joris Borgdorff
 *
 */
public class Scale implements Identifiable {
	private String id;
	private Dimension dim;
	private String dimName;
	private int dimensions;
	private SIRange delta;
	private boolean deltaFixed;
	private SIRange max;
	private boolean maxFixed;
	
	public Scale(String id, Dimension dim, SIRange delta, boolean deltaFixed, SIRange max, boolean maxFixed, int dimensions, String dimName) {
		this.id = id;
		this.dim = dim;
		this.dimensions = dimensions;
		this.delta = delta;
		this.deltaFixed = deltaFixed;
		this.max = max;
		this.maxFixed = maxFixed;
	}

	public Scale(String id, Dimension dim, SIRange delta, boolean deltaFixed, SIRange max, boolean maxFixed) {
		this(id, dim, delta, deltaFixed, max, maxFixed, 1, null);
	}
	
	/** Number of steps that can be distinguished within this scale, using the mean values of delat and max, if they are ranges */
	public int steps() {
		SIUnit d = delta.getMean(), L = max.getMean();
		return (int)Math.round(L.div(d).doubleValue());
	}

	/**
	 * @return the delta
	 */
	public SIRange getDelta() {
		return this.delta;
	}

	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.Identifiable#getId()
	 */
	@Override
	public String getId() {
		return id;
	}
	
	/** Get the dimensional axis of this scale */
	public Dimension getDimension() {
		return this.dim;
	}

	/**
	 * @return range of the maximum size
	 */
	public SIRange getMax() {
		return max;
	}

	/**
	 * @return whether the delta should be fixed when specifying a submodel instance
	 */
	public boolean hasDeltaFixed() {
		return deltaFixed;
	}

	/**
	 * @return whether the maximum should be fixed when specifying a submodel instance
	 */
	public boolean hasMaxFixed() {
		return maxFixed;
	}
}
