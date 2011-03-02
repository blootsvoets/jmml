package eu.mapperproject.xmml.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import eu.mapperproject.xmml.util.ScaleModifier.Dimension;

public class SIUnitTest {
	private SIUnit o1s, o1min;
	
	@Before
	public void setUp() { 
		o1s = new SIUnit(1, ScaleModifier.SI, Dimension.TIME);
		o1min = new SIUnit(1, ScaleModifier.MINUTE);
	}
	
	@Test
	public void compare() {
		assertEquals(new Integer(-1), new Integer(o1s.compareTo(o1min))); 
	}
	
	@Test 
	public void equals() {
		assertFalse(o1s.equals(o1min));
	}
	
	@Test
	public void parseSingle() {
		SIUnit.parseSIUnit("1");
	}

	@Test
	public void parseSci() {
		SIUnit.parseSIUnit("1E-64");
	}
	
	@Test
	public void parseTime() {
		SIUnit t = SIUnit.parseSIUnit("1 sec");
		assertEquals(t, o1s);
		assertEquals(Dimension.TIME, t.getDimension());
		t = SIUnit.parseSIUnit("1 min");
		assertEquals(t, o1min);
		SIUnit.parseSIUnit("1 hour");
		SIUnit.parseSIUnit("1 week");
		SIUnit.parseSIUnit("1 month");
		SIUnit.parseSIUnit("1 year");
	}

	@Test
	public void parseData() {
		SIUnit.parseSIUnit("1 kbit");
		SIUnit.parseSIUnit("1 MB");
		assertEquals(SIUnit.parseSIUnit("8 bit"), SIUnit.parseSIUnit("1 byte"));
	}
	
	@Test
	public void div() {
		assertEquals(new Double(60d), new Double(o1min.div(o1s).doubleValue()));
		assertEquals(new Double(30d), new Double(o1min.div(2l).doubleValue()));
	}
	
	@Test
	public void add() {
		assertEquals(new Double(61d), new Double(o1min.add(o1s).doubleValue()));
	}
}
