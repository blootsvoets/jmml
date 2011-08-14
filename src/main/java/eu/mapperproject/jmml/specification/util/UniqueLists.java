/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.util;

import eu.mapperproject.jmml.specification.graph.Numbered;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joris Borgdorff
 */
public class UniqueLists<T,V> extends AbstractList<V> {
	private final List<V> elems;
	private final Distinguisher<T, V> dist;
	private final boolean setNumbers;
	private final Validator<V> valid;
	
	public UniqueLists(Distinguisher<T,V> dist, boolean setNumbers, Validator<V> valid) {
		this.elems = new ArrayList<V>();
		this.dist = dist;
		this.setNumbers = setNumbers;
		this.valid = valid;
	}
	
	public UniqueLists(Distinguisher<T,V> dist) {
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
		
	public V getById(T type, String id) {
		return getById(dist.getTypeIndex(type), id);
	}
	
	public V getById(int i, String id) {
		if (i >= 0) {
			for (V elem : elems) {
				if (dist.getIndex(elem) == i && dist.getId(elem).equals(id)) {
					return elem;
				}
			}
		}
		return null;	
	}

	public V getById(String id) {
		for (V elem : elems) {
			if (dist.getId(elem).equals(id)) {
				return elem;
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
