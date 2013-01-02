/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mapperproject.jmml.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Joris Borgdorff
 */
public class ArrayMapTest {
	
	public static void main(String[] args) {
		// Preload classes
		Map<String,String> map1 = new ArrayMap<String,String>();
		Map<String,String> map2 = new HashMap<String,String>();
		
		for (int i = 1; i < 3000; i++) {
			String[] s = new String[i];
			String[] r = new String[i];
			for (int j = 0; j < i; j++) {
				s[j] = "sub" + j;
			}
			Map<String,String> map = new ArrayMap<String,String>(i);
			for (int j = 0; j < i; j++) {
				map.put(s[j], s[j]);
			}
			long t = System.nanoTime();
			int k = 0;
			for (String str : map.values()) {
				r[k++] = str;
			}
//			System.out.println(i + ": " + (System.nanoTime() - t) + " ns");
			for (int j = 0; j < i; j++) {
				s[j] = r[j];
			}
		}
		
		for (int i = 1; i < 3000; i++) {
			String[] s = new String[i];
			String[] r = new String[i];
			for (int j = 0; j < i; j++) {
				s[j] = "sub" + j;
			}
			Map<String,String> map = new HashMap<String,String>(i*4/3);
			for (int j = 0; j < i; j++) {
				map.put(s[j], s[j]);
			}
			long t = System.nanoTime();
			int k = 0;
			for (String str : map.values()) {
				r[k++] = str;
			}
//			System.out.println(i + ": " + (System.nanoTime() - t) + " ns");
			for (int j = 0; j < i; j++) {
				s[j] = r[j];
			}
		}

		
		System.out.println("ArrayMap");
		for (int i = 1; i < 3000; i++) {
			String[] s = new String[i];
			String[] r = new String[i];
			for (int j = 0; j < i; j++) {
				s[j] = "sub" + j;
			}
			Map<String,String> map = new ArrayMap<String,String>(i);
			for (int j = 0; j < i; j++) {
				map.put(s[j], s[j]);
			}
			long t = System.nanoTime();
			int k = 0;
			for (String str : map.values()) {
				r[k++] = str;
			}
			System.out.println(i + "\t" + (System.nanoTime() - t));
			for (int j = 0; j < i; j++) {
				s[j] = r[j];
			}
		}

		System.out.println("HashMap");
		for (int i = 1; i < 3000; i++) {
			String[] s = new String[i];
			String[] r = new String[i];
			for (int j = 0; j < i; j++) {
				s[j] = "sub" + j;
			}
			Map<String,String> map = new HashMap<String,String>(i*4/3);
			for (int j = 0; j < i; j++) {
				map.put(s[j], s[j]);
			}
			long t = System.nanoTime();
			int k = 0;
			for (String str : map.values()) {
				r[k++] = str;
			}
			System.out.println(i + "\t" + (System.nanoTime() - t));
			for (int j = 0; j < i; j++) {
				s[j] = r[j];
			}
		}
	}
	
	@Test
	public void testSize() {
		System.out.println("size");
		ArrayMap<String,String> instance = new ArrayMap<String,String>(2);
		assertEquals(0, instance.size());
		
		instance.put("new","something");
		assertEquals(1, instance.size());
		instance.put("new","something else");
		assertEquals(1, instance.size());
		instance.put("new1","something else");
		assertEquals(2, instance.size());
		instance.put("new2","something else");
		assertEquals(3, instance.size());
	}
	
		/**
	 * Test of isEmpty method, of class FastArrayList.
	 */
	@Test
	public void testIsEmpty() {
		System.out.println("isEmpty");
		ArrayMap<String,String> instance = new ArrayMap<String,String>();
		assertTrue(instance.isEmpty());
		instance.put("new","ala");
		assertFalse(instance.isEmpty());
		instance.remove("new");
		assertTrue(instance.isEmpty());
	}
	
	/**
	 * Test of contains method, of class FastArrayList.
	 */
	@Test
	public void testContains() {
		System.out.println("contains");
		ArrayMap<String,String> instance = new ArrayMap<String,String>();
		instance.put("new","something"); instance.put("ew", "somethingElse");
		assertTrue(instance.containsValue("somethingElse"));
		assertTrue(instance.containsValue("something"));
		assertFalse(instance.containsKey("something"));
		assertTrue(instance.containsKey("new"));
		assertTrue(instance.containsKey("ew"));
		assertFalse(instance.containsValue("ew"));
	}
	
		/**
	 * Test of iterator method, of class FastArrayList.
	 */
	@Test
	public void testEntrySetIterator() {
		System.out.println("iterator");
		ArrayMap<String,String> instance = new ArrayMap<String,String>();
		instance.put("new","something"); instance.put("ew","somethingElse"); instance.put("w","yetSomethingElse");
		Iterator<Map.Entry<String,String>> result = instance.entrySet().iterator();
		assertTrue(result.hasNext());
		assertEquals("something", result.next().getValue());
		assertTrue(result.hasNext());
		assertEquals("ew", result.next().getKey());
		result.remove();
		assertTrue(result.hasNext());
		assertEquals("yetSomethingElse", result.next().getValue());
		assertFalse(result.hasNext());
	}
}
