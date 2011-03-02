/**
 * 
 */
package eu.mapperproject.xmml.util;

/**
 * Represents an object by a single char
 * @author Joris Borgdorff
 *
 */
public class CharParseToken<T> implements ParseToken<T> {
	private char ch;
	private T object;
	private String remainder;
	
	public CharParseToken(T object, char ch) {
		this.ch = ch;
		this.object = object;
		this.remainder = null;
	}
	
	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.util.ParseToken#startOf(java.lang.String)
	 */
	@Override
	public boolean startOf(String s) {
		this.remainder = null;
		if (s != null && s.length() > 0 && s.charAt(0) == this.ch) {
			if (s.length() > 1) {
				this.remainder = s.substring(1);
			}
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.util.ParseToken#is(java.lang.String)
	 */
	@Override
	public boolean is(String s) {
		this.remainder = null;
		return (s != null && s.length() > 0 && s.charAt(0) == this.ch);
	}

	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.util.ParseToken#indexOf(java.lang.String)
	 */
	@Override
	public int indexOf(String s) {
		this.remainder = null;
		int index = -1;
		if (s != null) {
			index = s.indexOf(ch);
			if (index != -1 && index < s.length() - 1) {
				this.remainder = s.substring(index + 1);
			}
		}
		return index;
	}

	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.util.ParseToken#lastIndexOf(java.lang.String)
	 */
	@Override
	public int lastIndexOf(String s) {
		this.remainder = null;
		int index = -1;
		if (s != null) {
			index = s.lastIndexOf(ch);
			if (index != -1 && index < s.length() - 1) {
				this.remainder = s.substring(index + 1);
			}
		}
		return index;
	}

	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.util.ParseToken#getRemainder()
	 */
	@Override
	public String getRemainder() {
		return this.remainder;
	}

	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.util.ParseToken#getObject()
	 */
	@Override
	public T getObject() {
		return this.object;
	}

}
