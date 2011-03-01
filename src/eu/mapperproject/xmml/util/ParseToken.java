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
	
	/**
	 * Create an array of ParseTokens given an array of objects and a respectively indexed array of names 
	 * @param objects objects that the parsetokens represent
	 * @param names the names that the objects belong to
	 * @return an array of parsetokens of the given arguments
	 */
	public static <T> ParseToken<T>[] createTokens(T[] objects, String[][] names) {
		@SuppressWarnings("unchecked")
		ParseToken<T>[] ret = new ParseToken[objects.length];
		for (int i = 0; i < objects.length; i++) {
			ret[i] = new ParseToken<T>(objects[i], names[i]);
		}
		
		return ret;
	}

	/**
	 * Find the object that corresponds to the given string using an array of parsetokens.
	 * If such an object is not found, returns null.
	 */
	public static <T> T findObject(String s, ParseToken<T>[] tokens) {
		for (ParseToken<T> token : tokens) {
			if (token.is(s)) return token.getObject();
		}
		
		return null;
	}
	
	/**
	 * Whether the given string starts with this token.
	 * @param s parsable string
	 */
	public boolean startOf(String s) {
		for (String name : this.names) {
			if (s.startsWith(name)) {
				if (s.length() > name.length()) {
					this.remainder = s.substring(name.length());
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Whether the given string equals this token.
	 * @param s parsable string
	 */
	public boolean is(String s) {
		for (String name : this.names) {
			if (s.equals(name)) {
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
	
	public enum Optional {
		YES, NO, OPTIONAL
	}
	
	public final static ParseToken<Optional>[] optionalTokens = createTokens(Optional.values(), new String[][]{{"yes"}, {"no"}, {"optional"}});
}
