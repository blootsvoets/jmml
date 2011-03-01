/**
 * 
 */
package eu.mapperproject.xmml.util;

/**
 * @author Joris Borgdorff
 *
 */
public class SIRange {
	private final SIUnit min, max;
	
	public SIRange(SIUnit min, SIUnit max) {
		if (min.compareTo(max) > 0) {
			throw new IllegalArgumentException("Maximum in a range must be larger than the minimum");
		}
		this.min = min;
		this.max = max;
	}
	
	public SIRange(SIUnit value) {
		this(value, value);
	}
	
	public SIUnit getMinimum() {
		return this.min;
	}

	public SIUnit getMaximum() {
		return this.max;
	}
	
	public boolean isPoint() {
		return this.min.equals(this.max);
	}
}
