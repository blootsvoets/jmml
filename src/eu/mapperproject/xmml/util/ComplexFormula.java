package eu.mapperproject.xmml.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Represents a simple mathematical formula
 * @author Joris Borgdorff
 */
public class ComplexFormula extends Formula {
	/** Symbols that can be used */
	private enum Operator {
		PLUS("\\+"), MINUS("-"), TIMES("\\*"), DIVIDE("/"), POW("\\^"), LOG("log"), SQRT("sqrt"), SIZEOF("sizeof\\[([0-9]*)\\]"), TOKEN("\\[([0-9]*)\\]"), VARIABLE("[a-zA-Z_]\\w*"), NUMBER("[0-9\\.]");
		
		private Pattern p;
		
		Operator(String regex) {
			p = Pattern.compile(regex);
		}
		
		Matcher matches(String s) {
			return p.matcher(s);
		}
	}
	
	private final static Pattern whitespace = Pattern.compile("\\s");
	private final static Pattern parens = Pattern.compile("\\(([^\\)]*)\\)");
	private Operator operator;
	private Formula subformulaLeft;
	private Formula subformulaRight;
	private Set<String> variables;
	private List<String> parensToken;
	
	public static ComplexFormula create(String formulaString) {
		formulaString = whitespace.matcher(formulaString).replaceAll("");
		List<String> tokens = tokenizeParens(formulaString);
		return new ComplexFormula(tokens.get(0), tokens);
	}
	
	private ComplexFormula(String formulaString, List<String> tokens) {
		this.variables = null;
		this.parensToken = tokens;
		this.subformulaLeft = null;
		this.subformulaRight = null;
		this.operator = null;
		this.parseString(formulaString);
	}
	
	/**
	 * @return variable names contained in the formula
	 */
	public Set<String> getVariableNames() {
		if (this.variables == null) {
			this.variables = new TreeSet<String>();
			if (this.subformulaLeft != null)
				this.variables.addAll(this.subformulaLeft.getVariableNames());
			if (this.subformulaRight != null)
				this.variables.addAll(this.subformulaRight.getVariableNames());
		}
		return this.variables;
	}
	
	/** Calculates the formula, given the variable values
	 * 
	 * @param variables a map of each variable with a value
	 * @return its value; a double
	 */
	public double evaluate(Map<String,Integer> vars) {
		switch (this.operator) {
		case PLUS:
			return subformulaLeft.evaluate(vars) + subformulaRight.evaluate(vars); 
		case MINUS:
			return subformulaLeft.evaluate(vars) - subformulaRight.evaluate(vars); 
		case TIMES:
			return subformulaLeft.evaluate(vars) * subformulaRight.evaluate(vars); 
		case DIVIDE:
			return subformulaLeft.evaluate(vars) / subformulaRight.evaluate(vars); 
		case POW:
			return Math.pow(subformulaLeft.evaluate(vars), subformulaRight.evaluate(vars)); 
		case LOG:
			return Math.log(subformulaRight.evaluate(vars)); 
		case SQRT:
			return Math.sqrt(subformulaRight.evaluate(vars));
		default:
			return subformulaRight.evaluate(vars);
		}
	}
	
	private void parseString(String s) {
		for (Operator op : Operator.values()) {
			Matcher m = op.matches(s);
			
			if (m.find()) {
				this.operator = op;
				System.out.println("Operater: " + op.name());
				int start = m.start();
				int end = start + m.end() - 1;
				
				if (start > 0) {
					System.out.println("Parse left: " + s.substring(0, start));
					System.out.println("Parse right: " + s.substring(end));
					this.subformulaLeft = new ComplexFormula(s.substring(0, start), this.parensToken);
					this.subformulaRight = new ComplexFormula(s.substring(end), this.parensToken);
				}
				else {
					switch (op) {
					case SIZEOF:
						System.out.println("Parse sizeof " + m.group(1) + ":" + this.parensToken.get(Integer.parseInt(m.group(1))));
						this.subformulaRight = new FormulaSizeof(this.parensToken.get(Integer.parseInt(m.group(1))));
						break;
					case VARIABLE:
						System.out.println("Parse variable: " + s);
						this.subformulaRight = new FormulaVariable(s);
						break;
					case NUMBER:
						System.out.println("Parse number: " + s);
						this.subformulaRight = new FormulaNumber(s);
						break;
					case TOKEN:
						System.out.println("Parse token " + m.group(1) + ":" + this.parensToken.get(Integer.parseInt(m.group(1))));
						this.subformulaRight = new ComplexFormula(this.parensToken.get(Integer.parseInt(m.group(1))), this.parensToken);
						break;
					default:
						System.out.println("Parse right: " + s.substring(end + 1));
						this.subformulaRight = new ComplexFormula(s.substring(end + 1), this.parensToken);
						break;
					}
				}
				break;
			}
		}
		if (this.operator == null) {
			throw new IllegalStateException("Can not parse empty string");
		}
	}
	
	private static List<String> tokenizeParens(String s) {
		List<String> list = new ArrayList<String>();
		list.add(s);
		int token = 1;
		
		Matcher m = parens.matcher(s);
		
		while (m.find()) {
			list.add(m.group(1));
			s = m.replaceFirst("[" + token + "]");
			token++;
			m = parens.matcher(s);
		}
		
		list.set(0, s);
		return list;
	}
}
