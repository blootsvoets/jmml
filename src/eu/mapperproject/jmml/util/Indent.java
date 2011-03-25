package eu.mapperproject.jmml.util;

/**
 * Keeps track of the indentation and outputs it.
 * 
 * A tab of given size is made at construction and pasted onto the current indentation at an increase of indentation.
 * @author Joris Borgdorff
 */
public class Indent {
	private String currentIndentation;
	private final String tab;
	
	/** Create an indent with a given tab size and zero initial indentation */
	public Indent(int spacing) {
		this.tab = repeatSpace(spacing);
		this.currentIndentation = "\n";
	}
	
	/** Increase the indentation by the given tab size */
	public void increase() {
		currentIndentation += this.tab;
	}
	
	/** Decrease the indentation by the given tab size */
	public void decrease() {
		currentIndentation = currentIndentation.substring(0, currentIndentation.length() - tab.length());
	}
	
	@Override
	public String toString() {
		return this.currentIndentation;
	}

	/** Creates a tab of given size using a StringBuilder */
	private static String repeatSpace(int times) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}	
}