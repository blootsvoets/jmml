/*
 * 
 */
package eu.mapperproject.jmml.specification.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Joris Borgdorff
 */
public class ArrayMap<K,V> implements Map<K, V>, Serializable {
	enum IteratorType {
		KEYS, VALUES, ENTRY;
	}

	private int size;
	private K[] keys;
	private V[] values;
	
	public ArrayMap(Map<? extends K, ? extends V> map) {
		this(map.size());
		if (map instanceof ArrayMap) {
			ArrayMap<? extends K, ? extends V> amap = (ArrayMap<? extends K, ? extends V>)map;
			keys = Arrays.copyOf(amap.keys, map.size());
			values = Arrays.copyOf(amap.values, map.size());
		}
		else {
			Iterator<? extends Entry<? extends K, ? extends V>> iter = map.entrySet().iterator();
			for (int i = 0; i < map.size(); i++) {
				Entry<? extends K, ? extends V>entry = iter.next();
				keys[i] = entry.getKey();
				values[i] = entry.getValue();				
			}
		}
	}
	
	public ArrayMap() {
		this(5);
	}
	
	@SuppressWarnings({"unchecked", "unchecked"})
	public ArrayMap(int initialCapacity) {
		keys = (K[])new Object[initialCapacity];
		values = (V[])new Object[initialCapacity];
	}
	
	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean containsKey(Object o) {
		return indexOf(o, keys) >= 0;
	}

	@Override
	public boolean containsValue(Object o) {
		return indexOf(o, values) >= 0;
	}

	private int indexOf(Object o, Object[] os) {
		if (o == null) {
			for (int i = 0; i < size; i++) {
				if (os[i] == null) return i;
			}
		}
		else {
			for (int i = 0; i < size; i++) {
				if (o.equals(os[i])) return i;
			}
		}
		return -1;
	}
	
	@Override
	public V get(Object o) {
		int index = indexOf(o, keys);
		if (index == -1) return null;
		return values[index];
	}

	@Override
	public V put(K k, V v) {
		int index = indexOf(k, keys);
		if (index == -1) {
			this.ensureCapacity(size + 1);
			this.keys[size] = k;
			this.values[size] = v;
			this.size++;
			return null;
		}
		else {
			V tmp = this.values[index];
			this.values[index] = v;
			return tmp;
		}
	}

	@Override
	public V remove(Object o) {
		int index = indexOf(o, keys);
		if (index == -1) return null;
		return remove(index);
	}

	V remove(int index) {
		V tmp = values[index];
		System.arraycopy(keys, index + 1, keys, index, size - index - 1);
		System.arraycopy(values, index + 1, values, index, size - index - 1);
		size--;
		return tmp;
	}

	
	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		this.ensureCapacity(map.size() + size);
				
		for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
			int index = indexOf(entry.getKey(), keys);
			if (index == -1) {
				this.keys[size] = entry.getKey();
				this.values[size] = entry.getValue();
				this.size++;
			}
			else {
				this.values[index] = entry.getValue();
			}
		}
	}

	@Override
	public void clear() {
		size = 0;
	}

	@Override
	public Set<K> keySet() {
		return new KeySet();
	}

	@Override
	public Collection<V> values() {
		return new ValueCollection();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new EntrySet();
	}
	
	private void ensureCapacity(int newSize) {
		if (newSize > keys.length) {
			int newCapacity = Math.max(newSize, keys.length*2 + 1);
			keys = Arrays.copyOf(keys, newCapacity);
			values = Arrays.copyOf(values, newCapacity);
		}
	}
	
	private class KeySet implements Set<K> {
		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean isEmpty() {
			return ArrayMap.this.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return containsKey(o);
		}

		@Override
		public Iterator<K> iterator() {
			return new MapIterator<K>(IteratorType.KEYS);
		}

		@Override
		public Object[] toArray() {
			return Arrays.copyOf(keys, size);
		}

		@Override
		@SuppressWarnings({"unchecked", "unchecked"})
		public <T> T[] toArray(T[] ts) {
			if (ts.length == size) {
				System.arraycopy(keys, 0, ts, 0, size);
				return ts;
			}
			else {
				return (T[])Arrays.copyOf(keys, size);
			}
		}

		@Override
		public boolean add(K e) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean remove(Object o) {
			int index = indexOf(o, keys);
			if (index == -1) return false;
			ArrayMap.this.remove(index);
			return true;
		}

		@Override
		public boolean containsAll(Collection<?> clctn) {
			for (Object o : clctn) {
				if (!containsKey(o)) return false;
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends K> clctn) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean retainAll(Collection<?> clctn) {
			int i = 0, initial = size;
			while (i < size) {
				if (!clctn.contains(keys[i])) {
					ArrayMap.this.remove(i);
				}
				else {
					i++;
				}
			}
			return initial == size;
		}

		@Override
		public boolean removeAll(Collection<?> clctn) {
			int initial = size;
			for (Object o : clctn) {
				remove(o);
			}
			return initial == size;
		}

		@Override
		public void clear() {
			ArrayMap.this.clear();
		}
	
	}

	private class EntrySet implements Set<Entry<K,V>> {
		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean isEmpty() {
			return size == 0;
		}

		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Entry)) throw new ClassCastException("Only contains Map.Entry.");
			Entry me = (Entry)o;
			int index = indexOf(me.getKey(), keys);
			if (index == -1) return false;
			return (values[index] == null ? me.getValue() == null : values[index].equals(me.getValue()));
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			return new MapIterator<Entry<K, V>>(IteratorType.ENTRY);
		}

		@Override
		public Object[] toArray() {
			@SuppressWarnings("unchecked")
			MapEntry[] map = (MapEntry[])new Object[size];
			
			for (int i = 0; i < size; i++) {
				map[i] = new MapEntry(i);
			}
			return map;
		}

		@Override
		public <T> T[] toArray(T[] ts) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean add(Entry<K, V> e) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean remove(Object o) {
			if (!(o instanceof Entry)) throw new ClassCastException("Only contains Map.Entry.");
			Entry e = (Entry)o;
			int index = indexOf(o, keys);
			if (index == -1) return false;
			if (values[index] == null ? e.getValue() != null : !values[index].equals(e.getValue())) return false;
			ArrayMap.this.remove(index);
			return true;
		}

		@Override
		public boolean containsAll(Collection<?> clctn) {
			for (Object o : clctn) {
				if (!contains(o)) return false;
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends Entry<K, V>> clctn) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean retainAll(Collection<?> clctn) {
			int i = 0, initial = size;
			Entry<K,V> entry = new MapEntry(0);
			
			while (i < size) {
				if (!clctn.contains(entry)) {
					ArrayMap.this.remove(i);
				}
				else {
					entry = new MapEntry(++i);
				}
			}
			return initial == size;
		}

		@Override
		public boolean removeAll(Collection<?> clctn) {
			int initial = size;
			for (Object o : clctn) {
				remove(o);
			}
			return initial == size;
		}

		@Override
		public void clear() {
			ArrayMap.this.clear();
		}
	}
	
	private class MapIterator<T> implements Iterator<T> {
		private int i;
		private IteratorType type;
		
		public MapIterator(IteratorType type) {
			i = -1;
			this.type = type;
		}
		
		@Override
		public boolean hasNext() {
			return i + 1 < size;
		}

		@Override
		@SuppressWarnings({"unchecked", "unchecked"})
		public T next() {
			i++;
			switch (type) {
				case KEYS: return (T)keys[i];
				case VALUES: return (T)values[i];
				default: return (T)new MapEntry(i);
			}
		}

		@Override
		public void remove() {
			ArrayMap.this.remove(i);
		}
	}
	
	private class MapEntry implements Entry<K, V> {
		private int i;
		
		MapEntry(int i) {
			this.i = i;
		}
		@Override
		public K getKey() {
			return keys[i];
		}

		@Override
		public V getValue() {
			return values[i];
		}

		@Override
		public V setValue(V v) {
			V tmp = values[i];
			values[i] = v;
			return tmp;
		}
	}
	
	private class ValueCollection implements Collection<V> {
		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean isEmpty() {
			return size == 0;
		}

		@Override
		public boolean contains(Object o) {
			return containsValue(o);
		}

		@Override
		public Iterator<V> iterator() {
			return new MapIterator<V>(IteratorType.VALUES);
		}

		@Override
		public Object[] toArray() {
			return Arrays.copyOf(values, size);
		}

		@Override
		@SuppressWarnings({"unchecked", "unchecked"})
		public <T> T[] toArray(T[] ts) {
			if (ts.length == size) {
				System.arraycopy(values, 0, ts, 0, size);
				return ts;
			}
			else {
				return (T[])Arrays.copyOf(values, size);
			}
		}
		
		@Override
		public boolean remove(Object o) {
			int index = indexOf(o, values);
			if (index == -1) return false;
			ArrayMap.this.remove(index);
			return true;
		}

		@Override
		public boolean containsAll(Collection<?> clctn) {
			for (Object o : clctn) {
				if (!containsKey(o)) return false;
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends V> clctn) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean retainAll(Collection<?> clctn) {
			int i = 0, initial = size;
			while (i < size) {
				if (!clctn.contains(values[i])) {
					ArrayMap.this.remove(i);
				}
				else {
					i++;
				}
			}
			return initial == size;
		}

		@Override
		public boolean removeAll(Collection<?> clctn) {
			int initial = size;
			for (Object o : clctn) {
				while (remove(o)){}
			}
			return initial == size;
		}

		@Override
		public void clear() {
			ArrayMap.this.clear();
		}

		@Override
		public boolean add(V e) {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}
}
