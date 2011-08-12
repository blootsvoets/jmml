/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.util;

import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author jborgdo1
 */
public class ArrayMap<K,V> extends AbstractMap<K,V> {

	@Override
	public Set<Entry<K,V>> entrySet() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	
	private static class SimpleEntry<K,V> implements Entry<K,V> {
		final K key;
		V value;
		
		SimpleEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		@Override
		public K getKey() {
			return this.key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V v) {
			V tmp = this.value;
			this.value = v;
			return tmp;
		}
	}
}
