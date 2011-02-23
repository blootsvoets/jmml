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
		xy = ComplexFormula.create("x*y");
		log = ComplexFormula.create("log(x)");
		plus = ComplexFormula.create("x*y+x*log(x)");
		pow = ComplexFormula.create("x^2");
		sizeof = ComplexFormula.create("x*sizeof(double)");
		parensA = ComplexFormula.create("x*sizeof(double) + (x+y)/x");
		parensB = ComplexFormula.create("((x+x)*3 + (x+y)/x)");
		
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
}
