package eu.mapperproject.xmml.util;

import java.math.BigInteger;

public class ScaleModifier implements Comparable<ScaleModifier>{
	public enum Dimension {
		DATA, TIME, LENGTH;
	}

	public final static ScaleModifier SI = new ScaleModifier(0);
	
	public final static ScaleModifier MINUTE = new ScaleModifier(60, 1);
	public final static ScaleModifier HOUR = new ScaleModifier(60*60, 1);
	public final static ScaleModifier DAY = new ScaleModifier(60*60*24, 1);
	public final static ScaleModifier WEEK = new ScaleModifier(60*60*24*7, 1);
	public final static ScaleModifier MONTH = new ScaleModifier(60*60*24*365/12, 1);
	public final static ScaleModifier YEAR = new ScaleModifier(60*60*24*365, 1);

	public final static ScaleModifier BIT = new ScaleModifier(1, 8);
	
	public final static ScaleModifier MILLI = new ScaleModifier(-3);
	public final static ScaleModifier MICRO = new ScaleModifier(-6);
	public final static ScaleModifier NANO = new ScaleModifier(-9);
	public final static ScaleModifier PICO = new ScaleModifier(-12);
	public final static ScaleModifier FEMTO = new ScaleModifier(-12);
	public final static ScaleModifier ATTO = new ScaleModifier(-18);
	public final static ScaleModifier ZEPTO = new ScaleModifier(-21);
	public final static ScaleModifier YOCTO = new ScaleModifier(-24);

	public final static ScaleModifier KILO = new ScaleModifier(3);
	public final static ScaleModifier MEGA = new ScaleModifier(6);
	public final static ScaleModifier GIGA = new ScaleModifier(9);
	public final static ScaleModifier TERA = new ScaleModifier(12);
	public final static ScaleModifier PETA = new ScaleModifier(15);
	public final static ScaleModifier EXA = new ScaleModifier(18);
	public final static ScaleModifier ZETTA = new ScaleModifier(21);
	public final static ScaleModifier YOTTA = new ScaleModifier(24);

		
	private final BigInteger mult;
	private final BigInteger div;
	private Dimension dim;

	public ScaleModifier(int exp) {
		if (exp >= 0) {
			mult = BigInteger.valueOf(10).pow(exp);
			div = BigInteger.valueOf(1);
		}
		else {
			mult = BigInteger.valueOf(1);
			div = BigInteger.valueOf(10).pow(-exp);
		}
	}

	public ScaleModifier(long m, long div) {
		this(BigInteger.valueOf(m), BigInteger.valueOf(div));
	}

	public ScaleModifier(BigInteger m, BigInteger div) {
		BigInteger gcd = m.gcd(div);
		this.mult = m.divide(gcd);
		this.div = div.divide(gcd);
	}
	
	/** Add a scale to another */
	public ScaleModifier add(ScaleModifier other) {
		return new ScaleModifier(mult.multiply(other.mult), div.multiply(other.div));
	}

	/**
	 * Calculate the multiplication to get from this SI unit to the other.
	 */
	public ScaleModifier convert(ScaleModifier other) {
		return new ScaleModifier(div.multiply(other.mult), mult.multiply(other.div));
	}
	
	/**
	 * Modify the scale by extending it to a power of ten
	 * @param n the number of powers of ten, may be negative.
	 * @return the modified scale
	 */
	public ScaleModifier pow10(int n) {		
		return this.convert(new ScaleModifier(n));
	}
	
	/**
	 * @return dimension of the scale
	 */
	public Dimension getDimension() {
		return dim;
	}

	@Override
	public int compareTo(ScaleModifier o) {
		if (o.mult.equals(mult) && o.div.equals(div)) {
			return 0;
		}
		else {
			BigInteger[] odiv = o.mult.divideAndRemainder(o.div);
			BigInteger[] tdiv = mult.divideAndRemainder(div);
			int c = tdiv[0].compareTo(odiv[0]);

			// If divided is equal: largest remainder means largest value
			if (c == 0) c = tdiv[1].compareTo(odiv[1]);
			
			return c;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if (!other.getClass().equals(this.getClass())) return false;
		ScaleModifier sm = (ScaleModifier)other;
		return this.div.equals(sm.div) && this.mult.equals(sm.mult) && this.dim == sm.dim;
	}
	
	@Override
	public int hashCode() {
		int hashCode = this.mult.hashCode();
		hashCode = 31*hashCode + this.div.hashCode();
		hashCode = 31*hashCode + this.dim.hashCode();
		return hashCode;
	}
	
	private final static ParseToken<ScaleModifier>[] scaleTokens;
	private final static ParseToken<ScaleModifier>[] timeTokens;
	private final static ParseToken<ScaleModifier> dataToken = new ParseToken<ScaleModifier>(BIT, new String[] {"bits", "bit"});
	private final static ParseToken<Dimension>[] dimTokens;
	static {
		ScaleModifier[] objects = {
				MILLI, MICRO, NANO, PICO, FEMTO, ATTO, ZEPTO, YOCTO,
				KILO, MEGA, GIGA, TERA, PETA, EXA, ZETTA, YOTTA
			};
		String[][] names = {
				{"milli", "m"}, {"micro", "u"}, {"nano", "n"}, {"pico", "p"}, {"femto", "f"}, {"atto", "a"}, {"zepto", "z"}, {"yocto", "y"},
				{"kilo", "K", "k"}, {"mega", "M"}, {"giga", "G"}, {"tera", "T"}, {"peta", "P"}, {"exa", "E"}, {"zetta", "Z"}, {"yotta", "Y"}
			};
		scaleTokens = ParseToken.createTokens(objects, names);
		
		objects = new ScaleModifier[] {MINUTE, HOUR, DAY, WEEK, MONTH, YEAR};
		names = new String[][] {{"minutes", "minute", "min"}, {"hours", "hour", "hrs", "hr"}, {"days", "day"}, {"weeks", "week", "wks", "wk"}, {"years", "year", "yrs", "yr"}};
		timeTokens = ParseToken.createTokens(objects, names);
		
		names = new String[][] {{"byte", "B", "b"}, {"seconds", "second", "sec", "s"}, {"meters", "meter", "m"}};
		dimTokens = ParseToken.createTokens(Dimension.values(), names);
	}
	
	public static ScaleModifier parseScale(String s) {
		ScaleModifier scale = null;
		Dimension dim = null;
		
		// SI scale
		if (s.length() > 0) {
			for (ParseToken<ScaleModifier> token : scaleTokens) {
				if (token.startOf(s)) {
					s = token.getRemainder();
					scale = token.getObject();
				}
			}
		}
		// Irregular time scale
		if (scale == null && s.length() > 0) {
			for (ParseToken<ScaleModifier> token : timeTokens) {
				if (token.startOf(s)) {
					s = token.getRemainder();
					scale = token.getObject();
					dim = Dimension.TIME;
				}
			}
		}
		// Irregular data scale
		if (scale == null && s.length() > 0) {
			if (dataToken.startOf(s)) {
				s = dataToken.getRemainder();
				scale = dataToken.getObject();
				dim = Dimension.DATA;	
			}
		}
		// Dimension
		if (dim == null && s.length() > 0) {
			for (ParseToken<Dimension> token : dimTokens) {
				if (token.startOf(s)) {
					dim = token.getObject();
					s = dataToken.getRemainder();
				}
			}
		}
		
		if (s.length() > 0) {
			throw new IllegalArgumentException("String could not be parsed as a Scale: " + s);
		}
		else {
			scale.dim = dim;
			return scale;
		}
	}
}
