package eu.mapperproject.jmml.util;

import java.util.*;

/**
 *
 * @author Joris Borgdorff
 */
public class FastArrayList<T> implements List<T> {
	private int size;
	private T[] elems;
	private final FastIterator iter;
	
	public FastArrayList() {
		this(5);
	}
	
	@SuppressWarnings("unchecked")
	public FastArrayList(int initialCapacity) {
		this.elems = (T[])new Object[initialCapacity];
		this.iter = new FastIterator();
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
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}

	@Override
	public Iterator<T> iterator() {
		this.iter.reset();
		return this.iter;
	}

	@Override
	public Object[] toArray() {
		return Arrays.copyOf(elems, size);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U> U[] toArray(U[] ts) {
		if (ts.length >= size) {
			System.arraycopy(elems, 0, ts, 0, size);
			return ts;
		}
		else {
			return (U[])Arrays.copyOf(elems, size, ts.getClass());
		}
	}

	@Override
	public boolean add(T e) {
		ensureCapacity(size + 1);
		elems[size] = e;
		this.size++;
		return true;
	}

	@Override
	public boolean remove(Object o) {
		int i = indexOf(o);
		if (i != -1) {
			remove(i);
			return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> clctn) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean addAll(Collection<? extends T> clctn) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean addAll(int i, Collection<? extends T> clctn) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean removeAll(Collection<?> clctn) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean retainAll(Collection<?> clctn) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void clear() {
		size = 0;
	}

	@Override
	public T get(int i) {
		return elems[i];
	}

	@Override
	public T set(int i, T e) {
		if (i >= size)
			throw new IndexOutOfBoundsException("Can not set element at index " + i + " with size " + size + ".");
		T tmp = elems[i];
		elems[i] = e;
		return tmp;
	}

	@Override
	public void add(int i, T e) {
		if (i == size) {
			this.add(e);
		}
		ensureCapacity(size + 1);
		System.arraycopy(elems, i, elems, i + 1, size - i);
		elems[i] = e;
	}

	@Override
	public T remove(int i) {
		size--;
		if (i == size) {
			return elems[i];
		}
		else {
			T tmp = elems[i];
			System.arraycopy(elems, i + 1, elems, i, size - i);
			return tmp;
		}
	}

	@Override
	public int indexOf(Object o) {
		if (o == null) {
			for (int i = 0; i < size; i++) {
				if (elems[i] == null) return i;
			}
		}
		else {
			for (int i = 0; i < size; i++) {
				if (o.equals(elems[i])) return i;
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		if (o == null) {
			for (int i = size - 1; i >= 0; i--) {
				if (elems[i] == null) return i;
			}
		}
		else {
			for (int i = size - 1; i >= 0; i--) {
				if (o.equals(elems[i])) return i;
			}
		}
		return -1;
	}

	@Override
	public ListIterator<T> listIterator() {
		this.iter.reset();
		return this.iter;
	}

	@Override
	public ListIterator<T> listIterator(int i) {
		this.iter.reset(i);
		return this.iter;
	}

	@Override
	public List<T> subList(int i, int i1) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public T[] getBackingArray() {
		return elems;
	}
	
	private void ensureCapacity(int capacity) {
		if (elems.length <= capacity) {
			int newLen = Math.max(elems.length * 2 + 1, capacity);
			elems = Arrays.copyOf(elems, newLen);
		}
	}
	
	private final class FastIterator implements ListIterator<T> {
		private int i;
		
		public FastIterator() {
			i = -1;
		}
		
		@Override
		public boolean hasNext() {
			return i + 1 < size;
		}

		@Override
		public boolean hasPrevious() {
			return i >= 0;
		}

		
		@Override
		public T next() {
			return elems[++i];
		}

		@Override
		public void remove() {
			FastArrayList.this.remove(i);
		}
		
		void reset() {
			i = -1;
		}
		
		void reset(int i_) {
			this.i = i_;
		}

		@Override
		public T previous() {
			return elems[i--];
		}

		@Override
		public int nextIndex() {
			return i + 1;
		}

		@Override
		public int previousIndex() {
			return i;
		}

		@Override
		public void set(T e) {
			elems[i] = e;
		}

		@Override
		public void add(T e) {
			FastArrayList.this.add(i, e);
		}	
	}
}
