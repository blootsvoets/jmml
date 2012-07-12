package eu.mapperproject.jmml.util.numerical;

import eu.mapperproject.jmml.util.numerical.ScaleFactor.Dimension;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a number with a unit
 * @author Joris Borgdorff
 *
 */
public class SIUnit implements Comparable<SIUnit>, Serializable {	
	protected final double value;
	protected final ScaleFactor scale;
	private final static Pattern siPattern = Pattern.compile("(-?[0-9.]+)([eE](-?[0-9]+))?\\s*(\\w*\\s*\\w*)");
	
	public static SIUnit valueOf(String siunit) {
		Matcher m = siPattern.matcher(siunit);
		if (m.find()) {
			double value = Double.parseDouble(m.group(1));
			ScaleFactor scale = ScaleFactor.valueOf(m.group(m.groupCount()));

			// Parse exponent inherent to scientific notation
			String expStr = m.group(3);
			if (expStr != null) {
				ScaleFactor exp = new ScaleFactor(Integer.parseInt(expStr));
				scale = scale.add(exp);
			}
			
			return new SIUnit(value, scale);
		}
		else {
			throw new IllegalArgumentException("String could not be parsed as an SIUnit: " + siunit);
		}
	}
	
	public SIUnit(double value, ScaleFactor scale) {
		this.value = value;
		this.scale = scale;
	}
	
	protected SIUnit(SIUnit other) {
		this.value = other.value;
		this.scale = other.scale;
	}
	
	/** Add the given SIUnit to the current */
	public SIUnit add(SIUnit other) {
		double oval = getSuitableValue(other);
		return new SIUnit(oval + this.value, this.scale);
	}

	/** Multiply the given SIUnit with the current */
	public SIUnit mult(SIUnit other) {
		double oval = getSuitableValue(other);
		return new SIUnit(this.value * oval, this.scale);
	}
	
	/** Multiply this unit by a long */
	public SIUnit mult(long other) {
		return new SIUnit(this.value, this.scale.mult(other));
	}

	
	/** Add the given SIUnit to the current */
	public SIUnit sub(SIUnit other) {
		double oval = getSuitableValue(other);
		return new SIUnit(this.value - oval, this.scale);
	}

	/** Divide this unit by a long */
	public SIUnit div(long d) {
		return new SIUnit(this.value, this.scale.div(d));
	}
	
	/** Divide this unit by another unit, making them scale less */
	public SIUnit div(SIUnit other) {
		return new SIUnit(this.value / other.value, this.scale.div(other.scale));
	}

	/** Get the dimension that the unit lives on, or null if none */
	public Dimension getDimension() {
		return this.scale.getDimension();
	}
	
	/** Get the value of another SIUnit in the same scale as the current */
	private double getSuitableValue(SIUnit other) {
		// Frequently, the scales are the same.
		if (this.scale.compareTo(other.scale) == 0) {
			return other.value;
		} else {
			return this.scale.convert(other.scale).apply(other.value);
		}
	}
	
	/** Get the double value of the current scale. If this unit falls out of the range of a double, +-infinity is returned. */ 
	public double doubleValue() {
		return this.scale.apply(this.value);
	}

	/** Get the double value at another scale. If this unit falls out of the range of a double, +-infinity is returned. */ 
	public double doubleValue(ScaleFactor scaleMod) {
		return scaleMod.convert(this.scale).apply(this.value);
	}

	/** Get the log10 of the current SIUnit */
	public double log10() {
		return this.scale.log10() + Math.log10(this.value);
}
	
	@Override
	public boolean equals(Object other) {
		if (!other.getClass().equals(this.getClass())) return false;
		return this.compareTo((SIUnit)other) == 0;
	}
	
	@Override
	public int hashCode() {
		int hashCode = scale.hashCode();
		hashCode = 31*hashCode + Double.valueOf(value).hashCode();
		return hashCode;
	}
	
	@Override
	public String toString() {
		return this.value + "*" + this.scale;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SIUnit o) {
		double other = this.getSuitableValue(o);
		if (value == other) return 0;
		else if (value < other) return -1;
		else return 1;
	}
}
