package eu.mapperproject.xmml.topology.algorithms;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class TraceTest {

	private Trace<Integer> traceEmpty, traceEmpty1, traceEmpty2;

	@Before
	public void setUp() throws Exception {
		traceEmpty = new Trace<Integer>();
		traceEmpty1 = new Trace<Integer>();
		traceEmpty2 = new Trace<Integer>();
	}
	
	@Test
	public void mergeBasic() {
		traceEmpty1.merge(traceEmpty2);
		assertEquals(traceEmpty, traceEmpty1);
		assertEquals(traceEmpty, traceEmpty2);
	}

	@Test
	public void nextAndCurrent() {
		assertEquals(0, traceEmpty1.nextInt(1));
		traceEmpty1.put(1, 1);
		assertEquals(1, traceEmpty1.currentInt(1));
		assertEquals(0, traceEmpty1.nextInt(2));
		assertEquals(2, traceEmpty1.nextInt(1));
		assertEquals(2, traceEmpty1.currentInt(1));
		assertEquals(0, traceEmpty1.currentInt(2));
		assertEquals(1, traceEmpty1.nextInt(2));
		assertEquals(3, traceEmpty1.nextInt(1));
	}
	
	@Test(expected= IllegalStateException.class)
	public void empty() {
		assertFalse(traceEmpty.isInstantiated(1));
		traceEmpty.currentInt(1);
	}

	@Test
	public void overrideput() {
		traceEmpty.put(1, 3);
		traceEmpty.put(1, 2);
		traceEmpty1.put(1, 2);
		assertEquals(traceEmpty1, traceEmpty);
	}

	@Test
	public void compareputAndNext() {
		this.nextAndCurrent();
		traceEmpty.put(2, 1);
		traceEmpty.put(1, 3);
		assertEquals(traceEmpty, traceEmpty1);
	}


	@Test
	public void mergeWithput() {
		traceEmpty1.put(1, 1);
		traceEmpty2.put(2, 2);
		traceEmpty1.merge(traceEmpty2);
		
		traceEmpty.put(1, 1);
		traceEmpty.put(2, 2);
		
		assertEquals(traceEmpty, traceEmpty1);
	}
	
	@Test
	public void reset() {
		traceEmpty.put(1, 2);
		traceEmpty.reset(1);
		assertEquals(0, traceEmpty.nextInt(1));
	}
	
	@Test
	public void independentConstructor() {
		Trace<Integer> t = new Trace<Integer>(traceEmpty);
		t.put(1, 1);
		assertFalse(t.equals(traceEmpty));
	}
}
