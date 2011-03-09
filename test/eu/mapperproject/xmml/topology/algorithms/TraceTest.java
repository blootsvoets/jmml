package eu.mapperproject.xmml.topology.algorithms;
import eu.mapperproject.xmml.util.Numbered;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class TraceTest {
	static class Int implements Numbered {
		int num;
		Int(int num) {
			this.num = num;
		}
		@Override
		public int getNumber() {
			return this.num;
		}

	}

	private Trace<Int> traceEmpty, traceEmpty1, traceEmpty2;
	private Int one, two;

	@Before
	public void setUp() throws Exception {
		traceEmpty = new Trace<Int>();
		traceEmpty1 = new Trace<Int>();
		traceEmpty2 = new Trace<Int>();
		one = new Int(1);
		two = new Int(2);
	}
	
	@Test
	public void mergeBasic() {
		traceEmpty1.merge(traceEmpty2);
		assertEquals(traceEmpty, traceEmpty1);
		assertEquals(traceEmpty, traceEmpty2);
	}

	@Test
	public void nextAndCurrent() {
		assertEquals(0, traceEmpty1.nextInt(one));
		traceEmpty1.put(one, 1);
		assertEquals(1, traceEmpty1.currentInt(one));
		assertEquals(0, traceEmpty1.nextInt(two));
		assertEquals(2, traceEmpty1.nextInt(one));
		assertEquals(2, traceEmpty1.currentInt(one));
		assertEquals(0, traceEmpty1.currentInt(two));
		assertEquals(1, traceEmpty1.nextInt(two));
		assertEquals(3, traceEmpty1.nextInt(one));
	}
	
	@Test(expected= IllegalStateException.class)
	public void empty() {
		assertFalse(traceEmpty.isInstantiated(one));
		traceEmpty.currentInt(one);
	}

	@Test
	public void overrideput() {
		traceEmpty.put(one, 3);
		traceEmpty.put(one, 2);
		traceEmpty1.put(one, 2);
		assertEquals(traceEmpty1, traceEmpty);
	}

	@Test
	public void compareputAndNext() {
		this.nextAndCurrent();
		traceEmpty.put(two, 1);
		traceEmpty.put(one, 3);
		assertEquals(traceEmpty, traceEmpty1);
	}


	@Test
	public void mergeWithput() {
		traceEmpty1.put(one, 1);
		traceEmpty2.put(two, 2);
		traceEmpty1.merge(traceEmpty2);
		
		traceEmpty.put(one, 1);
		traceEmpty.put(two, 2);
		
		assertEquals(traceEmpty, traceEmpty1);
	}
	
	@Test
	public void reset() {
		traceEmpty.put(one, 2);
		traceEmpty.reset(one);
		assertEquals(0, traceEmpty.nextInt(one));
	}
	
	@Test
	public void independentConstructor() {
		Trace<Int> t = new Trace<Int>(traceEmpty);
		t.put(one, 1);
		assertFalse(t.equals(traceEmpty));
	}
}
