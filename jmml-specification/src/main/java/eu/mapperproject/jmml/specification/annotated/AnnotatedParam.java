/*
 * 
 */

package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Param;
import java.util.regex.Pattern;

/**
 *
 * @author Joris Borgdorff
 */
class AnnotatedParam extends Param {
	private final static Pattern numericPattern = Pattern.compile("^-?[0-9.]+([eE]-?[0-9]+)?$");
	private final static Pattern intPattern = Pattern.compile("^-?[0-9]+$");
	private final static Pattern boolPattern = Pattern.compile("^(true|false)$");
	
	public boolean isNumeric() {
		return numericPattern.matcher(this.getValue()).matches();
	}

	public boolean isInt() {
		return intPattern.matcher(this.getValue()).matches();
	}

	public boolean isBool() {
		return boolPattern.matcher(this.getValue().toLowerCase()).matches();
	}
}
