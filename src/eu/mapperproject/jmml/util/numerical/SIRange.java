/**
 * 
 */
package eu.mapperproject.jmml.util.numerical;

/**
 * A range from one SI unit to the next
 * @author Joris Borgdorff
 */
public class SIRange {
	private final SIUnit min, max;
	
	/** Construct a range with a minimum and maximum.
	 * 
	 * If either min or max is null, they will be interpreted as negative and positive infinity respectively.
	 * @param min minimum or null
	 * @param max maximum or null
	 * @throws IllegalArgumentException if both min and max are null, or if min is larger than max.
	 */
	public SIRange(SIUnit min, SIUnit max) {
		if (min == null && max == null) {
			throw new IllegalArgumentException("Either the minimum or maximum of a range need to be specified");
		}
		if (min != null && max != null && min.compareTo(max) > 0) {
			throw new IllegalArgumentException("Maximum in a range must be larger than the minimum");
		}
		this.min = min;
		this.max = max;
	}
	
	public SIRange(SIUnit value) {
		this(value, value);
	}
	
	/** Whether the minimum and maximum of the range are both set values */
	public boolean isDefinite() {
		return (min != null && max != null);
	}
	
	/** Minimum value of the range.
	 * 
	 * If null is returned, interpret as negative infinity. 
	 */
	public SIUnit getMinimum() {
		return this.min;
	}

	/** Maximum value of the range
	 * 
	 * If null is returned, interpret as positive infinity. 
	 */
	public SIUnit getMaximum() {
		return this.max;
	}
	
	/** Whether the range is in fact a single point or value */
	public boolean isPoint() {
		if (!this.isDefinite()) {
			return false;
		}
		return this.min.equals(this.max);
	}
	
	/** The mean value between the minimum and maximum.
	 * Returns null if either the minimum or maximum is null
	 */
	public SIUnit getMean() {
		if (!this.isDefinite()) {
			return null;
		}
		return this.max.add(this.min).div(2l);
	}
	
	/** Whether the given range is a full subset of this range */
	public boolean contains(SIRange range) {
		return (this.min == null || this.min.compareTo(range.min) <= 0) && (this.max == null || this.max.compareTo(range.max) >= 0);
	}
}
