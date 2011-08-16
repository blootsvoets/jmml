package eu.mapperproject.jmml.util.numerical;

import eu.mapperproject.jmml.util.parser.CharParseToken;
import eu.mapperproject.jmml.util.parser.MultiStringParseToken;
import eu.mapperproject.jmml.util.parser.ParseToken;
import eu.mapperproject.jmml.util.parser.RegexParseToken;

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
		PLUS('+'), MINUS("[^*/^+-]-", 2), TIMES('*'), DIVIDE('/'), NEGATE("-", 1), POW('^'),
		LOG("log", 1), SQRT("sqrt", 1),
		SIZEOF("sizeof\\[(\\d+)\\]", 0), TOKEN("\\[(\\d+)\\]", 0), VARIABLE("[a-zA-Z_][a-zA-Z0-9_]*", 0), NUMBER("-?(\\d*\\.)?\\d+([eE]-?\\d+)?", 0);
		
		private final ParseToken<Operator> token;
		private final int params;
		
		Operator(String multiple, int parameters) {
			this.params = parameters;
			if (this.params == 1) {
				this.token = new MultiStringParseToken<Operator>(this, new String[] {multiple}); 
			}
			else {
				this.token = new RegexParseToken<Operator>(this, multiple);
			}
		}
		
		Operator(char single) {
			this.params = 2;
			this.token = new CharParseToken<Operator>(this, single);
		}
		
		int getParameterCount() {
			return this.params;
		}
		
		int endIndex(String s) {
			int end = 0;
			if (this.params == 2) {
				end = token.lastIndexOf(s) + 1;
				// Don't allow two-parameter operators to not have left side
				if (end == 1) end = 0;
			}
			else if (this.params == 1) {
				if (token.startOf(s)) {
					end = s.length() - token.getRemainder().length(); 
				}
			}
			else {
				if (token.is(s)) {
					end = s.length();
				}
			}
			return end;
		}
		
		String getRemainder() {
			return token.getRemainder();
		}
		
		Matcher getMatcher() {
			if (token instanceof RegexParseToken) {
				return ((RegexParseToken<Operator>)token).getMatchObject();
			}
			return null;
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

	public static Formula parseFormula(String formulaString) throws ParseException {
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
		
		if (s == null || s.isEmpty()) {
			throw new IllegalArgumentException("Can not parse empty string");
		}
		
		for (Operator op : Operator.values()) {
			int index = op.endIndex(s);
			if (index > 0) {
				// Assuming single char for two-parameter operators not right for MINUS
				if (op == Operator.MINUS) {
					index++;
				}
				// Minus and divide have the same priority and should be handled at the same time
				if (op == Operator.PLUS) {
					if (Operator.MINUS.endIndex(op.getRemainder()) > 0) continue;
				}
				if (op == Operator.TIMES) {
					if (Operator.DIVIDE.endIndex(op.getRemainder()) > 0) continue;
				}

				// 1- and 2-parameter operators, both take a parameter
				if (op.getParameterCount() > 0) {
					right = parseString(op.getRemainder(), tokens);
					left = null;
					if (op.getParameterCount() == 2) {
						String before = s.substring(0, index - 1);
						left = parseString(before, tokens);
					}
					current = new ComplexFormula(op, left, right);
				}
				// 0-parameter operators can take the full input
				else {
					Matcher m = op.getMatcher();
					String token;
					switch (op) {
					case SIZEOF:
						token = tokens.get(Integer.parseInt(m.group(1)));
						current = new FormulaSizeof(token);
						break;
					case VARIABLE:
						current = new FormulaVariable(s);
						break;
					case NUMBER:
						current = new FormulaNumber(s);
						break;
					case TOKEN:
						token = tokens.get(Integer.parseInt(m.group(1)));
						current = parseString(token, tokens);
						break;
					}
				}
				
				break;
			}
		}
		if (current == null) {
			throw new ParseException("Can not parse string '" + s + "' as a formula", 0);
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
