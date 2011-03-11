package eu.mapperproject.xmml.topology.algorithms;
import eu.mapperproject.xmml.util.Numbered;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;

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
		@Override
		public boolean equals(Object o) {
			if (o == null || !this.getClass().equals(o.getClass())) return false;
			return this.num == ((Int)o).num;
		}

		@Override
		public int hashCode() {
			return this.num;
		}

		@Override
		public String toString() {
			return "Int(" + this.num + ")";
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
		traceEmpty1.merge(traceEmpty2, false);
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
		List<List<Int>> col = traceEmpty1.merge(traceEmpty2, true);
		assertTrue(col.get(0).isEmpty());
		assertSame(1, col.get(1).size());
		assertEquals(two, col.get(1).iterator().next());

		traceEmpty.put(one, 1);
		traceEmpty.put(two, 2);
		
		assertEquals(traceEmpty, traceEmpty1);

		traceEmpty2.put(two, 3);
		col = traceEmpty1.merge(traceEmpty2, true);
		assertSame(1, col.get(1).size());
		traceEmpty.put(two, 3);

		assertEquals(traceEmpty, traceEmpty1);

		col = traceEmpty1.merge(traceEmpty2, true);
		assertSame(1, col.get(0).size());
		assertEquals(two, col.get(0).iterator().next());
	}

	@Test
	public void override() {
		traceEmpty1.put(one, 1);
		traceEmpty2.put(two, 2);
		traceEmpty1.merge(traceEmpty2, false);
		
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
