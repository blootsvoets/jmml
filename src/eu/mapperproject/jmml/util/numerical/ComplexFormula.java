package eu.mapperproject.jmml.util.numerical;

import eu.mapperproject.jmml.util.numerical.Formula;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** Represents a simple mathematical formula
 * @author Joris Borgdorff
 */
public class ComplexFormula extends Formula {
	private Operator operator;
	private Formula left;
	private Formula right;
	private Set<String> variables;
	
	ComplexFormula(Operator op, Formula left, Formula right) {
		this.variables = null;
		this.left = left;
		this.right = right;
		this.operator = op;
	}
	
	ComplexFormula(Operator op, Formula value) {
		this(op, null, value);
	}
	
	/**
	 * @return variable names contained in the formula
	 */
	public Set<String> getVariableNames() {
		if (this.variables == null) {
			this.variables = new TreeSet<String>();
			if (this.left != null)
				this.variables.addAll(this.left.getVariableNames());
			if (this.right != null)
				this.variables.addAll(this.right.getVariableNames());
		}
		return this.variables;
	}
	
	/** Calculates the formula, given the variable values
	 * 
	 * @param vars a map of each variable with a value
	 * @return its value; a double
	 */
	public double evaluate(Map<String,Integer> vars) {
		switch (this.operator) {
		case PLUS:
			return left.evaluate(vars) + right.evaluate(vars); 
		case MINUS:
			return left.evaluate(vars) - right.evaluate(vars); 
		case TIMES:
			return left.evaluate(vars) * right.evaluate(vars); 
		case DIVIDE:
			return left.evaluate(vars) / right.evaluate(vars); 
		case POW:
			return Math.pow(left.evaluate(vars), right.evaluate(vars)); 
		case LOG:
			return Math.log(right.evaluate(vars)); 
		case SQRT:
			return Math.sqrt(right.evaluate(vars));
		case NEGATE:
			return -right.evaluate(vars);
		default:
			return right.evaluate(vars);
		}
	}
}
