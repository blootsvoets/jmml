package eu.mapperproject.xmml.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.mapperproject.xmml.util.ScaleModifier.Dimension;

/**
 * Represents a number with a unit
 * @author Joris Borgdorff
 *
 */
public class SIUnit implements Comparable<SIUnit> {	
	private final double value;
	private final ScaleModifier scale;
	
	public static SIUnit parseSIUnit(String siunit) {
		Matcher m = Pattern.compile("(-?[0-9.]+)([eE](-?[0-9]+))?\\s*(\\w*\\s*\\w*)").matcher(siunit);
		if (m.find()) {
			double value = Double.parseDouble(m.group(1));
			ScaleModifier scale = ScaleModifier.parseScale(m.group(m.groupCount()));

			// Parse exponent inherent to scientific notation
			String expStr = m.group(3);
			if (expStr != null) {
				ScaleModifier exp = new ScaleModifier(Integer.parseInt(expStr));
				scale = scale.add(exp);
			}
			
			return new SIUnit(value, scale);
		}
		else {
			throw new IllegalArgumentException("String could not be parsed as an SIUnit: " + siunit);
		}
	}
	
	public SIUnit(double value, ScaleModifier scale) {
		this.value = value;
		this.scale = scale;
	}
	
	public SIUnit(double value, ScaleModifier scale, Dimension dim) {
		this.value = value;
		this.scale = scale.changeDimension(dim);
	}
	
	public Dimension getDimension() {
		return this.scale.getDimension();
	}
	
	private double getSuitableValue(SIUnit other) {
		return this.scale.convert(other.scale).apply(other.value);
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
		return Double.valueOf(this.value).compareTo(this.getSuitableValue(o));
	}
}
