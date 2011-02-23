package eu.mapperproject.xmml.util;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class FormulaTest {
	private Formula xy, log, plus, pow, sizeof, parensA, parensB;
	private Set<String> xySet;
	private Map<String,Integer> xyGive;
	@Before
	public void setUp() {
		try {
		xy = Formula.parse("x*y");
		log = Formula.parse("log(x)");
		plus = Formula.parse("x*y+x*log(x)");
		pow = Formula.parse("x^2");
		sizeof = Formula.parse("x*sizeof(double)");
		parensA = Formula.parse("x*sizeof(double) + (x+y)/x");
		parensB = Formula.parse("((x+x)*3 + (x+y)/x)");
		} catch (Exception e) {};
		
		xySet = new TreeSet<String>();
		xySet.add("x"); xySet.add("y");
		xyGive = new TreeMap<String, Integer>();
		xyGive.put("x", 2); xyGive.put("y", 3);
	}
	
	@Test
	public void variables() {
		assertEquals(xySet, xy.getVariableNames());
		assertFalse(log.getVariableNames().equals(xySet));
	}
	
	@Test
	public void values() {
		assertTrue(6d == xy.evaluate(xyGive));
		assertTrue(128d == sizeof.evaluate(xyGive));
	}
	
	@Test
	public void operators() {
		log.evaluate(xyGive);
		plus.evaluate(xyGive);
		pow.evaluate(xyGive);
		parensA.evaluate(xyGive);
		parensB.evaluate(xyGive);
	}
}
