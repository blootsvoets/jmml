package eu.mapperproject.xmml.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.mapperproject.xmml.util.ScaleModifier.Dimension;

/**
 * Represents a number with a unit
 * @author Joris Borgdorff
 *
 */
public class SIUnit {	
	private final double value;
	private final ScaleModifier scale;
	
	public static SIUnit parseSIUnit(String siunit) {
		Matcher m = Pattern.compile("(-?[0-9.]+([eE]-?[0-9.]+)?)\\s*(\\w*\\s*\\w*)").matcher(siunit);
		if (m.find()) {
			double value = Double.parseDouble(m.group(1));
			ScaleModifier scale = ScaleModifier.parseScale(m.group(m.groupCount()));
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
	
	public Dimension getDimension() {
		return this.scale.getDimension();
	}
	
	
	@Override
	public boolean equals(Object other) {
		if (!other.getClass().equals(this.getClass())) return false;
		ScaleModifier sm = (ScaleModifier)other;
		return this.div.equals(sm.div) && this.mult.equals(sm.mult) && this.dim == sm.dim;
	}
	
	@Override
	public int hashCode() {
		int hashCode = this.mult.hashCode();
		hashCode = 31*hashCode + this.div.hashCode();
		hashCode = 31*hashCode + this.dim.hashCode();
		return hashCode;
	}
}
