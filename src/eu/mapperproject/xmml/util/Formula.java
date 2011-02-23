package eu.mapperproject.xmml.util;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** A representation of a Formula */
public abstract class Formula {
	private final static Set<String> emptySet = new TreeSet<String>();

	/**
	 * @return variable names contained in the formula
	 */
	public Set<String> getVariableNames() {
		return emptySet;
	}
	
	/** Calculates the formula, given the variable values
	 * 
	 * @param variables a map of each variable with a value
	 * @return a rounded BigInteger
	 */
	public abstract double evaluate(Map<String,Integer> variables);
}
