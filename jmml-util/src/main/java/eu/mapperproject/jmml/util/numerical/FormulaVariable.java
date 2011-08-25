package eu.mapperproject.jmml.util.numerical;

import eu.mapperproject.jmml.util.ArraySet;
import java.util.Map;
import java.util.Set;

/** Represent a single formula */
class FormulaVariable extends InterpretedFormula {
	private String var;
	private Set<String> vars;

	FormulaVariable(String var) {
		this.var = var;
		this.vars = new ArraySet<String>(1);
		this.vars.add(var);
	}

	@Override
	public double evaluate(Map<String, Integer> variables) {
		Integer i = variables.get(this.var);
		
		if (i == null) {
			throw new IllegalArgumentException("All variables must be initialized to evaluate the formula");
		}
		
		return i.longValue();
	}

	@Override
	public Set<String> getVariableNames() {
		return this.vars;
	}
}