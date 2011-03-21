package eu.mapperproject.xmml.util.numerical;

import java.util.Map;

/** Represent a single number */
class FormulaNumber extends Formula {
	private double value;

	FormulaNumber(String var) {
		this.value = Double.parseDouble(var);
	}

	@Override
	public double evaluate(Map<String, Integer> variables) {
		return this.value;
	}
}