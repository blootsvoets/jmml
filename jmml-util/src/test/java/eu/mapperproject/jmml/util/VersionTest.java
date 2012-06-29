package eu.mapperproject.jmml.util;

import eu.mapperproject.jmml.util.numerical.InterpretedVersion;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class VersionTest {
	private InterpretedVersion definiteA, definiteB, rangeA, rangeB, rangeC, rangeD, listA, listB;
	@Before
	public void setUp() {
		definiteA = new InterpretedVersion("0.1");
		definiteB = new InterpretedVersion("0.1.1");
		rangeA = new InterpretedVersion("0.1.x");
		rangeB = new InterpretedVersion("0.0-1.x");
		rangeC = new InterpretedVersion("0.0.x");
		rangeD = new InterpretedVersion("0.1.[2-3]");
		listA = new InterpretedVersion(new String[] {"0.1.x", "0.2.x"});
		listB = new InterpretedVersion(new String[] {"0.0.x", "0.2.x"});
	}
	
	@Test
	public void definiteNoContain() {
		assertFalse(definiteA.contains(definiteB));
		assertFalse(definiteB.contains(definiteA));
	}
	
	@Test
	public void rangeContains() {
		assertTrue(rangeA.contains(definiteA));
		assertTrue(rangeA.contains(definiteB));
		assertFalse(rangeC.contains(definiteA));
		assertFalse(rangeC.contains(definiteB));
		assertFalse(rangeD.contains(definiteA));
		assertFalse(rangeD.contains(definiteB));
	}

	@Test
	public void rangeAnyContains() {
		assertTrue(rangeA.contains(definiteA));
		assertTrue(rangeB.contains(definiteA));
	}
	
	@Test
	public void definite() {
		assertTrue(definiteA.isDefinite());
		assertFalse(rangeA.isDefinite());
	}
	
	@Test(expected= IllegalArgumentException.class)
	public void noDefiniteContains() {
		rangeA.contains(rangeB);
	}
	
	@Test
	public void listContains() {
		assertTrue(listA.contains(definiteA));
		assertFalse(listB.contains(definiteA));
	}
}
