/**
 * 
 */
package eu.mapperproject.xmml.topology;

import eu.mapperproject.xmml.util.ScaleModifier.Dimension;

/**
 * A coupling filter
 * @author Joris Borgdorff
 *
 */
public class Filter {
	public enum Type {
		INTERPOLATION, REDUCTION, CONVERTER
	}

	private final String name;
	private final Type type;
	private final Dimension scale;
	private final double factor;

	/**
	 * @param name
	 * @param type
	 * @param scale
	 * @param factor
	 */
	public Filter(String name, Type type, Dimension scale, double factor) {
		this.name = name;
		this.type = type;
		this.scale = scale;
		this.factor = factor;
	}
}
