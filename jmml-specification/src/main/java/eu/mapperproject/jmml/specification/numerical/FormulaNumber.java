package eu.mapperproject.jmml.specification.numerical;

import java.util.Map;

/** Represent a single number */
class FormulaNumber extends InterpretedFormula {
	private double value;

	FormulaNumber(String var) {
		this.value = Double.parseDouble(var);
	}

	@Override
	public double evaluate(Map<String, Integer> variables) {
		return this.value;
	}
}