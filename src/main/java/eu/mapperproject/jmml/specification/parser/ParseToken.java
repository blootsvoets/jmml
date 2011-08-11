/**
 * 
 */
package eu.mapperproject.jmml.specification.parser;

/**
 * A ParseToken keeps an object and can match that to a String
 * @author Joris Borgdorff
 *
 */
public interface ParseToken<T> {
	/**
	 * Whether the given string starts with this token.
	 * @param s parsable string
	 */
	public boolean startOf(String s);

	/**
	 * Whether the entire string matches this token.
	 * @param s parsable string
	 */
	public boolean is(String s);

	/**
	 * Index of the first character of the first match. Returns -1 if no match was found.
	 */
	public int indexOf(String s);
	
	/**
	 * Index of the first character of the last match. Returns -1 if no match was found.
	 */
	public int lastIndexOf(String s);

	/**
	 * Get the remainder of a previous match. Will return null if no previous match was made. 
	 */
	public String getRemainder();

	/**
	 * Get the represented object
	 * @return the represented object
	 */
	public T getObject();
}
