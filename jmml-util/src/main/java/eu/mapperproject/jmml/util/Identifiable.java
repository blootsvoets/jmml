package eu.mapperproject.jmml.util;

/**
 * An object with an ID.
 * An equals method of an identifiable object should yield the same result as
 * doing an equals on the id.
 * @author Joris Borgdorff
 */
public interface Identifiable {
	/**
	 * Get the ID of an identifiable object. All objects with the same string
	 * as ID should equal the current object
	 */
	public String getId();

	/**
	 * Whether the other object equals the current one besides the id.
	 */
	public boolean deepEquals(Object o);
}
