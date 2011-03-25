package eu.mapperproject.jmml.util.numerical;

import eu.mapperproject.jmml.util.parser.MultiStringParseToken;
import eu.mapperproject.jmml.util.parser.ParseToken;

import java.math.BigInteger;

public class ScaleModifier implements Comparable<ScaleModifier>{
	public enum Dimension {
		DATA, TIME, SPACE, OTHER;
	}

	private final BigInteger mult;
	private final BigInteger div;
	private final Dimension dim;

	/** Create a ScaleModifier with a power of ten size and a dimension. */
	public ScaleModifier(int exp, Dimension dim) {
		this.dim = dim;
		if (exp >= 0) {
			mult = BigInteger.TEN.pow(exp);
			div = BigInteger.ONE;
		}
		else {
			mult = BigInteger.ONE;
			div = BigInteger.TEN.pow(-exp);
		}
	}

	/** Create a ScaleModifier with a power of ten size. */
	public ScaleModifier(int exp) {
		this(exp, null);
	}

	/** Create a ScaleModifier with a dividend m and a divisor div and a dimension */
	public ScaleModifier(long m, long div, Dimension dim) {
		this(BigInteger.valueOf(m), BigInteger.valueOf(div), dim);
	}

	/** Create a ScaleModifier with a dividend m and a divisor div */
	public ScaleModifier(long m, long div) {
		this(m, div, null);
	}

	/** Create a ScaleModifier with a dividend m and a divisor div and a dimension */
	public ScaleModifier(BigInteger m, BigInteger div, Dimension dim) {
		this.dim = dim;
		BigInteger gcd = m.gcd(div);
		this.mult = m.divide(gcd);
		this.div = div.divide(gcd);
	}

	/** Create a ScaleModifier with a dividend m and a divisor div */
	public ScaleModifier(BigInteger m, BigInteger div) {
		this(m, div, null);
	}
	
	/** Add a scale to another */
	public ScaleModifier add(ScaleModifier other) {
		Dimension d = other.dim == null ? this.dim : other.dim;
		return new ScaleModifier(mult.multiply(other.mult), div.multiply(other.div), d);
	}

	/**
	 * Divide this scale modifier by another
	 */
	public ScaleModifier div(ScaleModifier other) {
		Dimension d = other.dim == null ? this.dim : other.dim;
		return new ScaleModifier(mult.multiply(other.div), div.multiply(other.mult), d);
	}
	
	/**
	 * Calculate the scalemodifier needed to go from a unit in this scale to a unit in another scale.
	 */
	public ScaleModifier convert(ScaleModifier other) {
		return other.div(this);
	}
	
	/**
	 * Modify the scale by extending it to a power of ten
	 * @param n the number of powers of ten, may be negative.
	 * @return the modified scale
	 */
	public ScaleModifier pow10(int n) {		
		return this.convert(new ScaleModifier(n));
	}
	
	/** Divide the scale by a value */
	public ScaleModifier div(long d) {
		return new ScaleModifier(this.mult, this.div.multiply(BigInteger.valueOf(d)), this.dim);
	}
	
	/**
	 * Apply this ScaleModifier to a double and return the result.
	 * @throws IllegalStateException if the divisor or dividend are so large that they can not be represented as a finite double.
	 */
	public double apply(double orig) {
		double m = this.mult.doubleValue();
		double d = this.div.doubleValue();
		if (m == Double.POSITIVE_INFINITY || d == Double.POSITIVE_INFINITY) {
			throw new IllegalStateException("Can not apply numbers larger than longs");
		}
		
		return (orig*m)/d;
	}
	
	/**
	 * Get the dimension of the scale.
	 */
	public Dimension getDimension() {
		return this.dim;
	}

	/**
	 * Whether the scale is dimensionless, or, has no dimension.
	 */
	public boolean isDimensionless() {
		return this.dim == null;
	}
	
	/**
	 * Set the dimensional axis of this scale modifier
	 * @param dim Dimension to be set
	 */
	ScaleModifier changeDimension(Dimension dim) {
		return new ScaleModifier(this.mult, this.div, dim);
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

	// Object
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
	
	@Override
	public String toString() {
		String dimStr = this.dim == null ? "" : "[" + this.dim + "]";
		if (this.mult.equals(BigInteger.ONE) && !this.div.equals(BigInteger.ONE)) {
			return this.div + "^-1" + dimStr;
		}
		else if (this.div.equals(BigInteger.ONE) && !this.mult.equals(BigInteger.valueOf(1))) {
			return this.mult + dimStr;
		}
		else {
			return this.mult + "/" + this.div + dimStr;
		}
	}

	public final static ScaleModifier SI = new ScaleModifier(0);

	public final static ScaleModifier MINUTE = new ScaleModifier(60, 1, Dimension.TIME);
	public final static ScaleModifier HOUR = new ScaleModifier(60*60, 1, Dimension.TIME);
	public final static ScaleModifier DAY = new ScaleModifier(60*60*24, 1, Dimension.TIME);
	public final static ScaleModifier WEEK = new ScaleModifier(60*60*24*7, 1, Dimension.TIME);
	public final static ScaleModifier MONTH = new ScaleModifier(60*60*24*365/12, 1, Dimension.TIME);
	public final static ScaleModifier YEAR = new ScaleModifier(60*60*24*365, 1, Dimension.TIME);

	public final static ScaleModifier BIT = new ScaleModifier(1, 8, Dimension.DATA);

	public final static ScaleModifier DECI = new ScaleModifier(-1);
	public final static ScaleModifier CENTI = new ScaleModifier(-2);
	public final static ScaleModifier MILLI = new ScaleModifier(-3);
	public final static ScaleModifier MICRO = new ScaleModifier(-6);
	public final static ScaleModifier NANO = new ScaleModifier(-9);
	public final static ScaleModifier PICO = new ScaleModifier(-12);
	public final static ScaleModifier FEMTO = new ScaleModifier(-12);
	public final static ScaleModifier ATTO = new ScaleModifier(-18);
	public final static ScaleModifier ZEPTO = new ScaleModifier(-21);
	public final static ScaleModifier YOCTO = new ScaleModifier(-24);

	public final static ScaleModifier DECA = new ScaleModifier(1);
	public final static ScaleModifier HECTO = new ScaleModifier(2);
	public final static ScaleModifier KILO = new ScaleModifier(3);
	public final static ScaleModifier MEGA = new ScaleModifier(6);
	public final static ScaleModifier GIGA = new ScaleModifier(9);
	public final static ScaleModifier TERA = new ScaleModifier(12);
	public final static ScaleModifier PETA = new ScaleModifier(15);
	public final static ScaleModifier EXA = new ScaleModifier(18);
	public final static ScaleModifier ZETTA = new ScaleModifier(21);
	public final static ScaleModifier YOTTA = new ScaleModifier(24);

	private final static ParseToken<ScaleModifier>[] scaleTokens;
	private final static ParseToken<Dimension>[] dimTokens;
	private final static ParseToken<ScaleModifier> bitToken = new MultiStringParseToken<ScaleModifier>(BIT, new String[] {"bits", "bit"});
	static {
		ScaleModifier[] objects = {
				MINUTE, HOUR, DAY, WEEK, MONTH, YEAR,
				DECA, HECTO, KILO, MEGA, GIGA, TERA, PETA, EXA, ZETTA, YOTTA,
				DECI, CENTI, MILLI, MICRO, NANO, PICO, FEMTO, ATTO, ZEPTO, YOCTO
			};
		String[][] names = {
				{"minutes", "minute", "min"}, {"hours", "hour", "hrs", "hr"}, {"days", "day"}, {"weeks", "week", "wks", "wk"}, {"months", "month"}, {"years", "year", "yrs", "yr"},
				{"deca", "da"}, {"hecto", "h"}, {"kilo", "K", "k"}, {"mega", "M"}, {"giga", "G"}, {"tera", "T"}, {"peta", "P"}, {"exa", "E"}, {"zetta", "Z"}, {"yotta", "Y"},
				{"deci", "d"}, {"centi", "c"}, {"milli", "m"}, {"micro", "u"}, {"nano", "n"}, {"pico", "p"}, {"femto", "f"}, {"atto", "a"}, {"zepto", "z"}, {"yocto", "y"}
			};
		scaleTokens = MultiStringParseToken.createTokens(objects, names);
		
		names = new String[][] {{"bytes", "byte", "B"}, {"seconds", "second", "sec", "s"}, {"meters", "meter", "m"}, {}};
		dimTokens = MultiStringParseToken.createTokens(Dimension.values(), names);
	}

	/** Parse a string and convert it to a ScaleModifier */
	public static ScaleModifier parseScale(String s) {
		ScaleModifier scale = null;
		Dimension dim = null;
		if (s.length() == 0) s = null;
		
		// Numeric scale
		if (s != null) {
			for (ParseToken<ScaleModifier> token : scaleTokens) {
				if (token.startOf(s)) {
					s = token.getRemainder();
					scale = token.getObject();
					break;
				}
			}
		}
		// Bit is both a dimension and an extra scale modifier
		if (s != null && bitToken.startOf(s)) {
			s = bitToken.getRemainder();
			scale = (scale == null) ? bitToken.getObject() : scale.add(bitToken.getObject());
			dim = scale.dim;
		}
		// Dimension of the scale
		if (dim == null && s != null) {
			for (ParseToken<Dimension> token : dimTokens) {
				if (token.startOf(s)) {
					s = token.getRemainder();
					dim = token.getObject();
					break;
				}
			}
		}
		
		if (s != null) {
			throw new IllegalArgumentException("String could not be parsed as a Scale: " + s);
		}
		else {
			if (scale == null) {
				scale = SI;
			}
			if (scale.isDimensionless() && dim != null) {
				scale = scale.changeDimension(dim);
			}

			return scale;
		}
	}
}
