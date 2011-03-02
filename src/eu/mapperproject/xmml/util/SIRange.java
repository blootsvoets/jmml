/**
 * 
 */
package eu.mapperproject.xmml.util;

/**
 * A range from one SI unit to the next
 * @author Joris Borgdorff
 */
public class SIRange {
	private final SIUnit min, max;
	
	public SIRange(SIUnit min, SIUnit max) {
		if (min != null && max != null && min.compareTo(max) > 0) {
			throw new IllegalArgumentException("Maximum in a range must be larger than the minimum");
		}
		this.min = min;
		this.max = max;
	}
	
	public SIRange(SIUnit value) {
		this(value, value);
	}
	
	/** Minimum value of the range */
	public SIUnit getMinimum() {
		return this.min;
	}

	/** Maximum value of the range */
	public SIUnit getMaximum() {
		return this.max;
	}
	
	/** Whether the range is in fact a single point or value */
	public boolean isPoint() {
		return this.min.equals(this.max);
	}
	
	/** The mean value between the minimum and maximum */
	public SIUnit getMean() {
		return this.max.add(this.min).div(2l);
	}
	
	/** Whether the given range is a full subset of this range */
	public boolean contains(SIRange range) {
		return this.min.compareTo(range.min) <= 0 && this.max.compareTo(range.max) >= 0;
	}
}
