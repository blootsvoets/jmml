package eu.mapperproject.jmml.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joris Borgdorff
 */
public class UniqueLists<T,V> extends AbstractList<V> {
	private final FastArrayList<V> elems;
	private final Distinguisher<T, ? super V> dist;
	private final boolean setNumbers;
	private final Validator<V> valid;
	
	public UniqueLists(Distinguisher<T,? super V> dist, boolean setNumbers, Validator<V> valid) {
		this.elems = new FastArrayList<V>();
		this.dist = dist;
		this.setNumbers = setNumbers;
		this.valid = valid;
	}
	
	public UniqueLists(Distinguisher<T,? super V> dist) {
		this(dist, true, null);
	}
	
	@Override
	public boolean add(V el) {
		if (this.valid != null && !valid.isValid(el)) {
			return false;
		}
		if (this.containsElement(el)) {
			throw new IllegalArgumentException("May not add double element.");
		}
		if (this.setNumbers) {
			((Numbered)el).setNumber(elems.size());
		}
		return this.elems.add(el);
	}
	
	@Override
	public V get(int i) {
		return this.elems.get(i);
	}
	
	public boolean hasType(T type) {
		return hasType(dist.getTypeIndex(type));
	}
	
	public boolean hasType(int i) {
		final V[] back = elems.getBackingArray();
		for (int j = 0; j < elems.size(); j++) {
			if (dist.getIndex(back[j]) == i) return true;
		}
		return false;
	}
	
	public V getById(T type, String id) {
		return getById(dist.getTypeIndex(type), id);
	}
	
	public V getById(int i, String id) {
		if (i >= 0) {
			final V[] back = elems.getBackingArray();
			for (int j = 0; j < elems.size(); j++) {
				if (dist.getIndex(back[j]) == i && dist.getId(back[j]).equals(id)) {
					return back[j];
				}
			}
		}
		return null;	
	}

	public V getById(String id) {
		final V[] back = elems.getBackingArray();
		for (int j = 0; j < elems.size(); j++) {
			if (dist.getId(back[j]).equals(id)) {
				return back[j];
			}
		}
		return null;	
	}

	@Override
	public boolean contains(Object o) {
		return this.elems.contains(o);
	}

	public boolean containsElement(V el) {
		try {
			return (getById(dist.getIndex(el), dist.getId(el)) != null);
		}
		catch (NullPointerException e) {
			System.out.println(el);
			System.exit(1);
			return true;
		}
	}
	
	@Override
	public int size() {
		return elems.size();
	}
}
