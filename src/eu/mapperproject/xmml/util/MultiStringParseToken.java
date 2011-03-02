/**
 * 
 */
package eu.mapperproject.xmml.util;

/**
 * An object and multiple possible strings that represent it
 * @author Joris Borgdorff
 *
 */
public class MultiStringParseToken<T> implements ParseToken<T> {
	private final String[] names;
	private final T object;
	private String remainder;
	
	public MultiStringParseToken(T object, String[] names) {
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
	public static <T> MultiStringParseToken<T>[] createTokens(T[] objects, String[][] names) {
		@SuppressWarnings("unchecked")
		MultiStringParseToken<T>[] ret = new MultiStringParseToken[objects.length];
		for (int i = 0; i < objects.length; i++) {
			ret[i] = new MultiStringParseToken<T>(objects[i], names[i]);
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
	
	@Override
	public boolean startOf(String s) {
		this.remainder = null;
		if (s != null && s.length() > 0) {
			for (String name : this.names) {
				if (s.startsWith(name)) {
					if (s.length() > name.length()) {
						this.remainder = s.substring(name.length());
					}
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean is(String s) {
		this.remainder = null;
		if (s != null && s.length() > 0) {
			for (String name : this.names) {
				if (s.equals(name)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public T getObject() {
		return this.object;
	}
	
	@Override
	public String getRemainder() {
		return this.remainder;
	}
	
	public enum Optional {
		YES, NO, OPTIONAL
	}
	
	public final static ParseToken<Optional>[] optionalTokens = createTokens(Optional.values(), new String[][]{{"yes"}, {"no"}, {"optional"}});

	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.util.ParseToken#indexOf(java.lang.String)
	 */
	@Override
	public int indexOf(String s) {
		this.remainder = null;
		int index, minIndex = -1;
		if (s != null && s.length() > 0) {
			for (String name : names) {
				index = s.indexOf(name);
				if (index > -1 && index < minIndex) {
					minIndex = index;
					this.remainder = s.substring(index + name.length());
				}
			}
		}
		
		return minIndex;
	}

	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.util.ParseToken#lastIndexOf(java.lang.String)
	 */
	@Override
	public int lastIndexOf(String s) {
		this.remainder = null;
		int index, maxIndex = -1;
		if (s != null && s.length() > 0) {
			for (String name : names) {
				index = s.lastIndexOf(name);
				if (index > maxIndex) {
					maxIndex = index;
					this.remainder = s.substring(index + name.length());
				}
			}
		}
		
		return maxIndex;
	}
}
