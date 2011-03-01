/**
 * 
 */
package eu.mapperproject.xmml.util;

/**
 * An object and the strings that represent it
 * @author Joris Borgdorff
 *
 */
public class ParseToken<T> {
	private final String[] names;
	private final T object;
	private String remainder;
	
	public ParseToken(T object, String[] names) {
		this.names = names;
		this.object = object;
		this.remainder = null;
	}
	
	public static <T> ParseToken<T>[] createTokens(T[] objects, String[][] names) {
		@SuppressWarnings("unchecked")
		ParseToken<T>[] ret = new ParseToken[objects.length];
		for (int i = 0; i < objects.length; i++) {
			ret[i] = new ParseToken<T>(objects[i], names[i]);
		}
		
		return ret;
	}
	
	/**
	 * Whether the given string starts with this token.
	 * @param s parsable string
	 */
	public boolean startOf(String s) {
		for (String name : this.names) {
			if (s.startsWith(name)) {
				this.remainder = s.substring(name.length());
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return the represented object
	 */
	public T getObject() {
		return this.object;
	}
	
	/**
	 * Get the remainder of a previous match. Will return null if no previous match was made. 
	 */
	public String getRemainder() {
		String tmp = this.remainder;
		this.remainder = null;
		return tmp;
	}
}
