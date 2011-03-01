/**
 * 
 */
package eu.mapperproject.xmml;

/**
 * Simple parameter class
 * @author Joris Borgdorff
 *
 */
public class Param {
	private final String name;
	private final String value;
	
	public Param(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
