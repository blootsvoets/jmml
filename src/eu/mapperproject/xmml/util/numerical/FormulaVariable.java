package eu.mapperproject.xmml.util.numerical;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** Represent a single formula */
class FormulaVariable extends Formula {
	private String var;
	private Set<String> vars;

	FormulaVariable(String var) {
		this.var = var;
		this.vars = new TreeSet<String>();
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