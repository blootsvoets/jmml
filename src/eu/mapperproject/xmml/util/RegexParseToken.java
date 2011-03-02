/**
 * 
 */
package eu.mapperproject.xmml.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A token of an object using a regular expression.
 * 
 * @see java.util.regex.Pattern
 * @author Joris Borgdorff
 */
public class RegexParseToken<T> implements ParseToken<T> {
	private final Pattern parser;
	private final T object;
	private Matcher matcher;
	private String remainder;
	
	public RegexParseToken(T object, String regex) {
		this.object = object;
		this.parser = Pattern.compile(regex);
		this.matcher = null;
		this.remainder = null;
	}
	
	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.util.ParseToken#startOf(java.lang.String)
	 */
	@Override
	public boolean startOf(String s) {
		this.remainder = null;
		this.matcher = this.parser.matcher(s);
		if (matcher.find() && matcher.start() == 0) {
			if (matcher.end() < s.length()) {
				this.remainder = s.substring(matcher.end());
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
		this.matcher = this.parser.matcher(s);
		return (matcher.find() && matcher.start() == 0 && matcher.end() == s.length());
	}

	/** Return the match object of the previous match, or null if no previous match was made */
	public Matcher getMatchObject() {
		return this.matcher;
	}

	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.util.ParseToken#getObject()
	 */
	@Override
	public T getObject() {
		return this.object;
	}

	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.util.ParseToken#indexOf(java.lang.String)
	 */
	@Override
	public int indexOf(String s) {
		this.remainder = null;
		this.matcher = this.parser.matcher(s);
		if (matcher.find()) {
			if (matcher.end() < s.length()) {
				this.remainder = s.substring(matcher.end());
			}
			return matcher.start();
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.util.ParseToken#lastIndexOf(java.lang.String)
	 */
	@Override
	public int lastIndexOf(String s) {
		this.remainder = null;
		this.matcher = this.parser.matcher(s);
		
		int index = -1;
		int end = 0;
		int len = s.length();
		while (end < len) {
			if (matcher.find()) {
				index = matcher.start();
				end = matcher.end();
				if (end < len) {
					this.remainder = s.substring(end);
				}
			}
			else {
				if (index > -1) matcher.find(index);
				end = s.length();
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
}
