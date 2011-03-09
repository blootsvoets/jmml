package eu.mapperproject.xmml.topology.algorithms;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import eu.mapperproject.xmml.topology.algorithms.Annotation.AnnotationType;
import eu.mapperproject.xmml.topology.algorithms.TraceTest.Int;

public class AnnotationTest {

	private Annotation<Int> anIter1;
	private Annotation<Int> anInst;
	private Annotation<Int> anIter2;
	private Int one, two;
	
	@Before
	public void setUp() throws Exception {
		anInst = new Annotation<Int>(AnnotationType.INSTANCE);
		anIter1 = new Annotation<Int>(AnnotationType.ITERATION);
		anIter2 = new Annotation<Int>(AnnotationType.ITERATION);
		one = new Int(1);
		two = new Int(2);
	}
	
	@Test
	public void eq() {
		assertEquals(anIter1, anIter2);
		assertFalse(anIter1.equals(anInst));
	}
	
	@Test
	public void nextOrderIndifferent() {
		anIter1 = anIter1.next(one);
		anIter1 = anIter1.next(two);
		
		anIter2 = anIter2.next(two);
		anIter2 = anIter2.next(one);
		assertEquals(anIter1, anIter2);
	}

	@Test
	public void currentIndifferent() {
		anIter1 = anIter1.current(one);
		assertEquals(anIter2, anIter1);
	}
	
	@Test
	public void next() {
		anIter1 = anIter1.next(one);
		anIter1 = anIter1.next(one);
		assertEquals(1, anIter1.getCounter());
		
		anIter1 = anIter1.next(two);
		assertEquals(0, anIter1.getCounter());
	}
	
	@Test
	public void reset() {
		anIter1 = anIter1.next(one);
		anIter1 = anIter1.reset(one);
		anIter1 = anIter1.reset(two);
		assertEquals(anIter2, anIter1);
	}
}
