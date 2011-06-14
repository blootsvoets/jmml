/**
 * 
 */
package eu.mapperproject.jmml.io;

import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import eu.mapperproject.jmml.definitions.Scale;
import eu.mapperproject.jmml.definitions.ScaleSet;
import eu.mapperproject.jmml.util.numerical.SIRange;
import eu.mapperproject.jmml.util.numerical.SIUnit;
import eu.mapperproject.jmml.util.numerical.ScaleModifier.Dimension;

/**
 * Parse the scales of a submodel or instance
 * @author Joris Borgdorff
 *
 */
public class ScaleParser {
	private final static Logger logger = Logger.getLogger(ScaleParser.class.getName());

	private final String from;
	private final ScaleSet orig;
	private final ScaleSet current;
	private boolean lastFixed;

	public ScaleParser(String from) {
		this(from, null);
	}
	
	public ScaleParser(String from, ScaleSet orig) {
		this.from = from;
		this.orig = orig;
		this.current = (orig == null) ? new ScaleSet() : orig.copy();
		this.lastFixed = false;
	}
	
	/** Parse the XML elements representing the scales of a certain dimension */
	public void parseElements(Elements scales, Dimension dim) {			
		for (int i = 0; i < scales.size(); i++) {
			Element scale = scales.get(i);

			String id = scale.getAttributeValue("id");
			if (id == null) id = resolveId(dim);
			else if (this.orig == null && current.hasScale(id)) {
				logger.log(Level.WARNING, "Multiple scales with the same id have been defined in submodel ''{0}''. Scales should have a unique id per submodel.", from);
			}
			
			// Get information of the original version of this scale, if applicable
			SIRange origDelta = null, origMax = null;
			SIUnit characteristic = null;
			boolean origDeltaFixed = false, origMaxFixed = false;
			if (this.orig != null) {
				Scale origScale = this.orig.getScale(id);
				if (origScale == null) {					
					logger.log(Level.WARNING, "In a submodel instance ({0}), only scales may be overridden that were already defined in the submodel. Scale ''{1}'' was not previously defined in a submodel.", new Object[]{from, id});
					continue;
				}
				origDelta = origScale.getDelta();
				origMax = origScale.getMax();
				characteristic = origScale.getCharacteristic();
			}
			else {
				String charVal = scale.getAttributeValue("characteristic");
				if (charVal != null) {
					characteristic = SIUnit.parseSIUnit(charVal);
				}
			}
			
			SIRange delta = this.parseRange(scale, id, "delta", "step size", origDelta, origDeltaFixed);
			boolean deltaFixed = this.lastFixed;
			if (characteristic == null) {
				characteristic = delta.getMean();
			}
			
			SIRange max = this.parseRange(scale, id, "max", "maximum size", origMax, origMaxFixed);
			boolean maxFixed = this.lastFixed;

			
			Attribute dims = scale.getAttribute("dimensions");
			int dimensions = 1;
			if (dims != null) {
				dimensions = Integer.parseInt(dims.getValue());
			}
			String dimName = scale.getAttributeValue("name");
			
			current.putScale(new Scale(id, dim, delta, deltaFixed, max, maxFixed, characteristic, dimensions, dimName));
		}
	}
	
	/** Get the scalemap that was parsed so far */
	public ScaleSet getScaleMap() {
		return this.current;
	}
	
	private SIRange parseRange(Element scale, String id, String type, String info, SIRange origRange, boolean origFixed) {
		SIRange range = null; this.lastFixed = true;
		String dattr = scale.getAttributeValue(type);
		if (dattr != null) {
			range = new SIRange(SIUnit.parseSIUnit(dattr));
			if (origFixed && !origRange.isPoint() && !origRange.contains(range)) {
				logger.log(Level.WARNING, "Value {0} of scale ''{1}'' of submodel instance ''{2}'' is outside the possible range {3}, according to its submodels range specification", new Object[]{range.getMaximum(), id, from, origRange});
			}
		}
		else {
			if (origFixed) {
				logger.log(Level.WARNING, "According to scale ''{0}'' of the submodel of ''{1}'' the maximum should be fixed, it has to have a definite value in each of the instances", new Object[]{id, from});
			}
			Element eType = scale.getFirstChildElement(type);
			if (eType == null) {
				return origRange;
			}
			else {
				String minStr = eType.getAttributeValue("min"), maxStr = eType.getAttributeValue("max");
				SIUnit min = minStr == null ? null : SIUnit.parseSIUnit(minStr);
				SIUnit max = maxStr == null ? null : SIUnit.parseSIUnit(maxStr);
				
				if (origRange != null) {
					if (min == null) min = origRange.getMinimum();
					if (max == null) max = origRange.getMaximum();
				}

				range = new SIRange(min, max);
				if (eType.getAttributeValue("type").equals("variable")) {
					if (origFixed) {
						logger.log(Level.WARNING, "according to scale ''{0}'' of the submodel of ''{1}'' the {2} should be fixed, it can not be set to variable afterwards", new Object[]{id, from, info});
					}
					else {
						this.lastFixed = false;
					}
				}
			}
		}
		
		return range;
	}
	
	private String resolveId(Dimension dim) {
		String id = null;
		
		if (dim == Dimension.TIME) {
			id = "t";
		}
		else if (dim == Dimension.SPACE) {
			if (!current.hasScale("x")) id = "x";
			else if (!current.hasScale("y")) id = "y";
			else if (!current.hasScale("z")) id = "z";
			else if (!current.hasScale("u")) id = "u";
			else if (!current.hasScale("v")) id = "v";
			else if (!current.hasScale("w")) id = "w";
			else {
				logger.warning("With more than 6 length scales, please provide names, now random names are used.");
				id = "x" + Math.random();
			}
		}
		else {
			if (!current.hasScale("a")) id = "a";
			else if (!current.hasScale("b")) id = "b";
			else if (!current.hasScale("c")) id = "c";
			else if (!current.hasScale("d")) id = "d";
			else if (!current.hasScale("e")) id = "e";
			else if (!current.hasScale("f")) id = "f";
			else {
				logger.warning("With more than 6 length scales, please provide names, now random names are used.");
				id = "a" + Math.random();
			}		
		}
		
		return id;
	}
}
