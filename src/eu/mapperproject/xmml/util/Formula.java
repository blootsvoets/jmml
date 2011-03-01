package eu.mapperproject.xmml.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** A representation of a Formula */
public abstract class Formula {
	/** Symbols that can be used */
	enum Operator {
		PLUS('+', 2), MINUS('+', 2), TIMES('*', 2), DIVIDE('/', 2), NEGATE('-', 1), POW('^', 2), LOG("log", 1), SQRT("sqrt", 1), SIZEOF("sizeof", 0), TOKEN('[', 0), VARIABLE(null, 0), NUMBER(null, 0);
		
		private char single;
		private String multiple;
		private int params;
		
		Operator(String multiple, int parameters) {
			this.params = parameters;
			this.multiple = multiple;
			this.single = (char)0;
		}
		
		Operator(char single, int parameters) {
			this.params = parameters;
			this.multiple = null;
			this.single = single;
		}
		
		int lastIndexOf(String s) {
			return s.lastIndexOf(single);
			if (multiple != null) {
		}
		
		int getParameterCount() {
			return this.params;
		}
	}
	
	private final static Pattern whitespace = Pattern.compile("\\s");
	private final static Pattern parens = Pattern.compile("\\(([^\\(\\)]*)\\)");
	
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

	public static Formula parse(String formulaString) throws ParseException {
		formulaString = whitespace.matcher(formulaString).replaceAll("");
		List<String> tokens = tokenizeParens(formulaString);
		return parseString(tokens.get(0), tokens);
	}
	
	/** Parse a string to a formula recursively.
	 * 
	 * @param s String to parse
	 * @param tokens Contents of tokens denoting parentheses
	 * @throws ParseException if the formula is not well-formed
	 */
	private static Formula parseString(String s, List<String> tokens) throws ParseException {
		Formula left, right;
		Formula current = null;
		
		if (s.isEmpty()) {
			throw new IllegalArgumentException("Can not parse empty string");
		}
		
		for (Operator op : Operator.values()) {			
			Matcher m = op.matches(s);
			
			if (m.find()) {
				int startGroup = m.groupCount() == 0 ? 0 : 1; 
				int start = m.start(startGroup);
				String before = s.substring(0, start);
				
//				if (op == Operator.PLUS) {
//					if (Operator.MINUS.matches(before).find()) continue;
//				}
//				if (op == Operator.TIMES) {
//					if (Operator.DIVIDE.matches(before).find()) continue;
//				}

				if (op.getParameterCount() > 0) {
					String after = s.substring(m.end());				
					right = parseString(after, tokens);
					left = (op.getParameterCount() == 2) ? parseString(before, tokens) : null;
					current = new ComplexFormula(op, left, right);
				}
				else {
					String token = startGroup == 0 ? null : tokens.get(Integer.parseInt(m.group(1)));
					switch (op) {
					case SIZEOF:
						current = new FormulaSizeof(token);
						break;
					case VARIABLE:
						current = new FormulaVariable(s);
						break;
					case NUMBER:
						current = new FormulaNumber(s);
						break;
					case TOKEN:
						current = parseString(token, tokens);
						break;
					}
				}
				
				break;
			}
		}
		if (current == null) {
			throw new ParseException("Can not parse empty string", 0);
		}
		return current;
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
