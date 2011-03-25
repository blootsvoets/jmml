/**
 * 
 */
package eu.mapperproject.jmml.definitions;

import eu.mapperproject.jmml.Identifiable;
import eu.mapperproject.jmml.util.numerical.SIRange;
import eu.mapperproject.jmml.util.numerical.SIUnit;
import eu.mapperproject.jmml.util.numerical.ScaleModifier.Dimension;

/**
 * Represents an xMML scale element
 * @author Joris Borgdorff
 *
 */
public class Scale implements Identifiable {
	private final String id;
	private final Dimension dim;
	private final String dimName;
	private final int dimensions;
	private final SIRange delta;
	private final boolean deltaFixed;
	private final SIRange max;
	private final boolean maxFixed;
	private final int steps;
	
	public Scale(String id, Dimension dim, SIRange delta, boolean deltaFixed, SIRange max, boolean maxFixed, int dimensions, String dimName) {
		this.id = id;
		this.dim = dim;
		this.dimName = dimName;
		this.dimensions = dimensions;
		this.delta = delta;
		this.deltaFixed = deltaFixed;
		this.max = max;
		this.maxFixed = maxFixed;
		this.steps = calculateSteps();
	}

	public Scale(String id, Dimension dim, SIRange delta, boolean deltaFixed, SIRange max, boolean maxFixed) {
		this(id, dim, delta, deltaFixed, max, maxFixed, 1, null);
	}
	
	/**
	 * Calculate the number of steps that can be taken given the ranges
	 * Returns -1 if delta or max is not set or not definite
	 */
	private int calculateSteps() {
		if (delta == null || !delta.isDefinite() || max == null || !max.isDefinite()) {
			return -1;
		}
		
		SIUnit d = delta.getMean(), L = max.getMean();
		return (int)Math.round(L.div(d).doubleValue());		
	}
	
	/**
	 * Number of steps that can be distinguished within this scale,
	 * using the mean values of delta and max, if they are ranges
	 */
	public int getSteps() {
		return steps;
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) return false;
		return this.id.equals(((Scale)obj).id);
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public boolean deepEquals(Object o) {

		throw new UnsupportedOperationException("Not supported yet.");
	}
}
