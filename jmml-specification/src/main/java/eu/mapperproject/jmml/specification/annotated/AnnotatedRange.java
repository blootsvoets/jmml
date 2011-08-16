package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Range;
import eu.mapperproject.jmml.specification.numerical.SIUnit;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedRange extends Range {
	/** Whether the minimum and maximum of the range are both set values */
	public boolean isDefinite() {
		return (min != null && max != null);
	}
	
	/** Whether the range is in fact a single point or value */
	public boolean isRegular() {
		if (!this.isDefinite()) {
			return false;
		}
		return this.min.equals(this.max);
	}
	
	public SIUnit meanSIUnit() {
		if (this.max == null) {
			if (this.min == null) return null;
			return minSIUnit();
		}
		else if (this.min == null || this.isRegular()) {
			return minSIUnit();
		}
		return maxSIUnit().add(minSIUnit()).div(2l);
	}
	
	/** Whether the given range is a full subset of this range */
	public boolean contains(AnnotatedRange range) {
		return (this.min == null || minSIUnit().compareTo(range.minSIUnit()) <= 0) && (this.max == null || maxSIUnit().compareTo(range.maxSIUnit()) >= 0);
	}
	
	public SIUnit minSIUnit() {
		return min == null ? null : ((AnnotatedUnit)min).interpret();
	}

	public SIUnit maxSIUnit() {
		return max == null ? null : ((AnnotatedUnit)min).interpret();
	}

	@Override
	public String toString() {
		return meanSIUnit().toString();
	}
}
