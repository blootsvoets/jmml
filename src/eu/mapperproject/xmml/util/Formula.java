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
				int start = m.start();
				String before = s.substring(0, start);
				String after = s.substring(m.end());
				
				if (start > 0) {
					left = parseString(before, tokens);
					right = parseString(after, tokens);
					current = new ComplexFormula(op, left, right);
				}
				else {
					String token = m.groupCount() == 0 ? null : tokens.get(Integer.parseInt(m.group(1)));

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
					default:
						right = parseString(after, tokens);
						current = new ComplexFormula(op, right);
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
