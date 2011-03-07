package eu.mapperproject.xmml.topology.algorithms;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import eu.mapperproject.xmml.topology.algorithms.Annotation.AnnotationType;

public class AnnotationTest {
	
	private Annotation<Integer> anIter1;
	private Annotation<Integer> anInst;
	private Annotation<Integer> anIter2;
	
	@Before
	public void setUp() throws Exception {
		anInst = new Annotation<Integer>(AnnotationType.INSTANCE);
		anIter1 = new Annotation<Integer>(AnnotationType.ITERATION);
		anIter2 = new Annotation<Integer>(AnnotationType.ITERATION);
	}
	
	@Test
	public void eq() {
		assertEquals(anIter1, anIter2);
		assertFalse(anIter1.equals(anInst));
	}
	
	@Test
	public void nextOrderIndifferent() {
		anIter1 = anIter1.next(1);
		anIter1 = anIter1.next(2);
		
		anIter2 = anIter2.next(2);
		anIter2 = anIter2.next(1);
		assertEquals(anIter1, anIter2);
	}

	@Test
	public void currentIndifferent() {
		anIter1 = anIter1.current(1);
		assertEquals(anIter2, anIter1);
	}
	
	@Test
	public void next() {
		anIter1 = anIter1.next(1);
		anIter1 = anIter1.next(1);
		assertEquals(1, anIter1.getCounter());
		
		anIter1 = anIter1.next(2);
		assertEquals(0, anIter1.getCounter());
	}
	
	@Test
	public void reset() {
		anIter1 = anIter1.next(1);
		anIter1 = anIter1.reset(1);
		anIter1 = anIter1.reset(2);
		assertEquals(anIter2, anIter1);
	}
}
