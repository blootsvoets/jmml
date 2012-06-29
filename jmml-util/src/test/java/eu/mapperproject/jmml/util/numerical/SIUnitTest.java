package eu.mapperproject.jmml.util.numerical;

import eu.mapperproject.jmml.util.numerical.SIUnit;
import eu.mapperproject.jmml.util.numerical.ScaleFactor;
import eu.mapperproject.jmml.util.numerical.ScaleFactor.Dimension;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class SIUnitTest {
	private SIUnit o1s, o1min;
	
	@Before
	public void setUp() { 
		o1s = new SIUnit(1, ScaleFactor.SI, Dimension.TIME);
		o1min = new SIUnit(1, ScaleFactor.MINUTE);
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
		SIUnit.valueOf("1");
	}

	@Test
	public void parseSci() {
		SIUnit.valueOf("1E-64");
	}
	
	@Test
	public void parseTime() {
		SIUnit t = SIUnit.valueOf("1 sec");
		assertEquals(t, o1s);
		assertEquals(Dimension.TIME, t.getDimension());
		t = SIUnit.valueOf("1 min");
		assertEquals(t, o1min);
		SIUnit.valueOf("1 hour");
		SIUnit.valueOf("1 week");
		SIUnit.valueOf("1 month");
		SIUnit.valueOf("1 year");
	}

	@Test
	public void parseData() {
		SIUnit.valueOf("1 kbit");
		SIUnit.valueOf("1 MB");
		assertEquals(SIUnit.valueOf("8 bit"), SIUnit.valueOf("1 byte"));
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
