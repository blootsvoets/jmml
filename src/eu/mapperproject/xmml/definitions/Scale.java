/**
 * 
 */
package eu.mapperproject.xmml.definitions;

import eu.mapperproject.xmml.util.SIRange;
import eu.mapperproject.xmml.util.ScaleModifier.Dimension;

/**
 * Represents an xMML scale element
 * @author Joris Borgdorff
 *
 */
public class Scale {
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
}
