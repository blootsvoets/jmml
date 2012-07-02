package eu.mapperproject.jmml.util.numerical;

import eu.mapperproject.jmml.util.parser.MultiStringParseToken;
import eu.mapperproject.jmml.util.parser.ParseToken;
import java.io.Serializable;
import java.math.BigInteger;

public class ScaleFactor implements Comparable<ScaleFactor>, Serializable {
	public enum Dimension {
		DATA, TIME, SPACE, OTHER;
	}

	private final BigInteger mult;
	private final BigInteger div;
	private final Dimension dim;
	private final String dimName;

	/** Create a ScaleModifier with a power of ten size and a dimension. */
	public ScaleFactor(int exp, Dimension dim) {
		this(exp, dim, dim == null ? null : dim.toString().toLowerCase());
	}

	public ScaleFactor(int exp, Dimension dim, String dimName) {
		this.dim = dim;
		this.dimName = dimName;
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
	public ScaleFactor(int exp) {
		this(exp, null, null);
	}

	/** Create a ScaleModifier with a dividend m and a divisor div and a dimension */
	public ScaleFactor(long m, long div, Dimension dim) {
		this(BigInteger.valueOf(m), BigInteger.valueOf(div), dim, dim == null ? null : dim.toString().toLowerCase());
	}

	/** Create a ScaleModifier with a dividend m and a divisor div and a dimension */
	public ScaleFactor(long m, long div, Dimension dim, String dimName) {
		this(BigInteger.valueOf(m), BigInteger.valueOf(div), dim, dimName);
	}

	/** Create a ScaleModifier with a dividend m and a divisor div */
	public ScaleFactor(long m, long div) {
		this(m, div, null, null);
	}

	/** Create a ScaleModifier with a dividend m and a divisor div and a dimension */
	public ScaleFactor(BigInteger m, BigInteger div, Dimension dim) {
		this(m, div, dim, dim == null ? null : dim.toString().toLowerCase());
	}
	
	/** Create a ScaleModifier with a dividend m and a divisor div and a dimension */
	public ScaleFactor(BigInteger m, BigInteger div, Dimension dim, String dimName) {
		this.dim = dim;
		this.dimName = dimName;
		BigInteger gcd = m.gcd(div);
		this.mult = m.divide(gcd);
		this.div = div.divide(gcd);
	}

	/** Create a ScaleModifier with a dividend m and a divisor div */
	public ScaleFactor(BigInteger m, BigInteger div) {
		this(m, div, null, null);
	}
	
	/** Add a scale to another */
	public ScaleFactor add(ScaleFactor other) {
		Dimension d = other.dim == null ? this.dim : other.dim;
		String dN = other.dimName == null ? this.dimName : other.dimName;
		return new ScaleFactor(mult.multiply(other.mult), div.multiply(other.div), d, dN);
	}

	/**
	 * Divide this scale modifier by another
	 */
	public ScaleFactor div(ScaleFactor other) {
		Dimension d = other.dim == null ? this.dim : other.dim;
		String dN = other.dimName == null ? this.dimName : other.dimName;
		return new ScaleFactor(mult.multiply(other.div), div.multiply(other.mult), d, dN);
	}
	
	/**
	 * Calculate the scalemodifier needed to go from a unit in this scale to a unit in another scale.
	 */
	public ScaleFactor convert(ScaleFactor other) {
		return other.div(this);
	}
	
	/**
	 * Modify the scale by extending it to a power of ten
	 * @param n the number of powers of ten, may be negative.
	 * @return the modified scale
	 */
	public ScaleFactor pow10(int n) {		
		return this.convert(new ScaleFactor(n));
	}
	
	/** Divide the scale by a value */
	public ScaleFactor div(long d) {
		return new ScaleFactor(this.mult, this.div.multiply(BigInteger.valueOf(d)), this.dim, this.dimName);
	}

	/** Multiply the scale by a value */
	public ScaleFactor mult(long d) {
		return new ScaleFactor(this.mult.multiply(BigInteger.valueOf(d)), this.div, this.dim, this.dimName);
	}
	
	/**
	 * Apply the log10 to the modifier and return the result as a double
	 */
	public double log10() {
		double ret = 0d;
		ret += iterativeLog10(this.mult);
		ret -= iterativeLog10(this.div);
		return ret;
	}

	private double iterativeLog10(BigInteger b) {
		double ret = 0d;
		while (b.compareTo(BIG_LONG) > 0) {
			b = b.divide(BIG_INT);
			ret += BIG_INT_LOG10;
		}
		return ret + Math.log10(b.doubleValue());
	}
	
	/**
	 * Apply this ScaleModifier to a double and return the result.
	 * @throws IllegalStateException if the divisor or dividend are so large that they can not be represented as a finite double.
	 */
	public double apply(double orig) {
		double m = this.mult.doubleValue();
		double d = this.div.doubleValue();
		if (m == Double.POSITIVE_INFINITY || d == Double.POSITIVE_INFINITY) {
			throw new IllegalStateException("Can not apply numbers larger than doubles");
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
	public ScaleFactor withDimension(Dimension dim) {
		return new ScaleFactor(this.mult, this.div, dim);
	}
	
	public ScaleFactor withDimension(Dimension dim, String dimName) {
		return new ScaleFactor(this.mult, this.div, dim, dimName);
	}
	
	@Override
	public int compareTo(ScaleFactor o) {
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
		ScaleFactor sm = (ScaleFactor)other;
		return this.div.equals(sm.div) && this.mult.equals(sm.mult) && this.dim == sm.dim &&
				(this.dimName == null ? sm.dimName == null : dimName.equals(sm.dimName));
	}
	
	@Override
	public int hashCode() {
		int hashCode = this.mult.hashCode();
		hashCode = 31*hashCode + this.div.hashCode();
		hashCode = 31*hashCode + this.dim.hashCode();
		hashCode = 31*hashCode + (this.dimName == null ? 0 : this.dimName.hashCode());
		return hashCode;
	}
	
	@Override
	public String toString() {
		String dimStr = this.dimName == null ? "" : "[" + this.dimName + "]";
		if (this.mult.equals(BigInteger.ONE) && !this.div.equals(BigInteger.ONE)) {
			return this.div + "^-1" + dimStr;
		}
		else if (this.div.equals(BigInteger.ONE) && !this.mult.equals(BigInteger.ONE)) {
			return this.mult + dimStr;
		}
		else if (this.div.equals(BigInteger.ONE) && this.mult.equals(BigInteger.ONE)) {
			return dimStr;
		}
		else {
			return this.mult + "/" + this.div + dimStr;
		}
	}

	public final static ScaleFactor SI = new ScaleFactor(0);

	public final static ScaleFactor METER = new ScaleFactor(0, Dimension.SPACE);
	
	public final static ScaleFactor SECOND = new ScaleFactor(0, Dimension.TIME);
	public final static ScaleFactor MINUTE = new ScaleFactor(60, 1, Dimension.TIME);
	public final static ScaleFactor HOUR = new ScaleFactor(60*60, 1, Dimension.TIME);
	public final static ScaleFactor DAY = new ScaleFactor(60*60*24, 1, Dimension.TIME);
	public final static ScaleFactor WEEK = new ScaleFactor(60*60*24*7, 1, Dimension.TIME);
	public final static ScaleFactor MONTH = new ScaleFactor(60*60*24*365/12, 1, Dimension.TIME);
	public final static ScaleFactor YEAR = new ScaleFactor(60*60*24*365, 1, Dimension.TIME);

	public final static ScaleFactor BIT = new ScaleFactor(1, 8, Dimension.DATA);
	public final static ScaleFactor BYTE = new ScaleFactor(0, Dimension.DATA);

	public final static ScaleFactor DECI = new ScaleFactor(-1);
	public final static ScaleFactor CENTI = new ScaleFactor(-2);
	public final static ScaleFactor MILLI = new ScaleFactor(-3);
	public final static ScaleFactor MICRO = new ScaleFactor(-6);
	public final static ScaleFactor NANO = new ScaleFactor(-9);
	public final static ScaleFactor PICO = new ScaleFactor(-12);
	public final static ScaleFactor FEMTO = new ScaleFactor(-12);
	public final static ScaleFactor ATTO = new ScaleFactor(-18);
	public final static ScaleFactor ZEPTO = new ScaleFactor(-21);
	public final static ScaleFactor YOCTO = new ScaleFactor(-24);

	public final static ScaleFactor DECA = new ScaleFactor(1);
	public final static ScaleFactor HECTO = new ScaleFactor(2);
	public final static ScaleFactor KILO = new ScaleFactor(3);
	public final static ScaleFactor MEGA = new ScaleFactor(6);
	public final static ScaleFactor GIGA = new ScaleFactor(9);
	public final static ScaleFactor TERA = new ScaleFactor(12);
	public final static ScaleFactor PETA = new ScaleFactor(15);
	public final static ScaleFactor EXA = new ScaleFactor(18);
	public final static ScaleFactor ZETTA = new ScaleFactor(21);
	public final static ScaleFactor YOTTA = new ScaleFactor(24);

	private final static BigInteger BIG_LONG = BigInteger.valueOf(Long.MAX_VALUE);
	// 10^9
	private final static BigInteger BIG_INT = BigInteger.valueOf(1000000000);
	private final static double BIG_INT_LOG10 = 9d;
	
	private final static ParseToken<ScaleFactor>[] scalePrefixTokens, timeTokens;
	private final static ParseToken<Dimension>[] dimTokens;
	private final static ParseToken<ScaleFactor> bitToken = new MultiStringParseToken<ScaleFactor>(BIT, new String[] {"bits", "bit"});
	static {
		ScaleFactor[] objects = {
				DECA, HECTO, KILO, MEGA, GIGA, TERA, PETA, EXA, ZETTA, YOTTA,
				DECI, CENTI, MILLI, MICRO, NANO, PICO, FEMTO, ATTO, ZEPTO, YOCTO
			};
		String[][] names = {
				{"deca", "da"}, {"hecto", "h"}, {"kilo", "K", "k"}, {"mega", "M"}, {"giga", "G"}, {"tera", "T"}, {"peta", "P"}, {"exa", "E"}, {"zetta", "Z"}, {"yotta", "Y"},
				{"deci", "d"}, {"centi", "c"}, {"milli", "m"}, {"micro", "u"}, {"nano", "n"}, {"pico", "p"}, {"femto", "f"}, {"atto", "a"}, {"zepto", "z"}, {"yocto", "y"}
			};
		scalePrefixTokens = MultiStringParseToken.createTokens(objects, names);

		names = new String[][] {{"minutes", "minute", "min"}, {"hours", "hour", "hrs", "hr"}, {"days", "day"}, {"weeks", "week", "wks", "wk"}, {"months", "month"}, {"years", "year", "yrs", "yr"}};
		objects = new ScaleFactor[] {MINUTE, HOUR, DAY, WEEK, MONTH, YEAR};
		timeTokens = MultiStringParseToken.createTokens(objects, names);

		names = new String[][] {{"bytes", "byte", "B"}, {"seconds", "second", "sec", "s"}, {"meters", "meter", "m"}, {}};
		dimTokens = MultiStringParseToken.createTokens(Dimension.values(), names);
	}

	/** Parse a string and convert it to a ScaleModifier */
	public static ScaleFactor valueOf(String s) {
		ScaleFactor scale = null;
		Dimension dim = null;
		if (s.length() == 0) s = null;
		
		// Time scale
		if (s != null) {
			for (ParseToken<ScaleFactor> token : timeTokens) {
				if (token.startOf(s)) {
					s = token.getRemainder();
					scale = token.getObject();
					break;
				}
			}
		}
		// Numeric scale
		if (scale == null && s != null) {
			for (ParseToken<ScaleFactor> token : scalePrefixTokens) {
				if (token.startOf(s)) {
					String rem = token.getRemainder();
					if (rem != null) {
						s = rem;
						scale = token.getObject();
						break;
					}
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
				scale = scale.withDimension(dim);
			}

			return scale;
		}
	}
}
