/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.util;

import eu.mapperproject.jmml.specification.graph.Identifiable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;

/**
 *
 * @author Joris Borgdorff
 */
public class UniqueLists<V> extends AbstractList<JAXBElement<V>> {
	private List[] lists;
	private String[] names;
	
	public UniqueLists(String[] names) {
		this.names = names;
		this.lists = new ArrayList[names.length];
	}
	
	@Override
	public boolean add(JAXBElement<V> el) {
		int i = getIndex(el.getName().getLocalPart());
		
		if (lists[i].contains(el)) {
			throw new IllegalArgumentException("May not add two ports with the same name.");
		}
			
		return lists[i].add(el);
	}
	
	@Override
	public JAXBElement<V> get(int i) {
		int j = 0, n = lists[0].size();
		while (i >= lists[j].size() && j < names.length - 1) {
			i -= lists[j].size();
			j++;
		}
		
		return (JAXBElement<V>)lists[j].get(i);
	}
		
	public V getById(String name, String id) {
		return getById(getIndex(name), id);
	}
	
	public V getById(int i, String id) {
		for (JAXBElement<V> je : (List<JAXBElement<V>>)lists[i]) {
			if (je != null && ((Identifiable)je.getValue()).getId().equals(id)) {
				return je.getValue();
			}
		}
		return null;	
	}

	@Override
	public int size() {
		int sz = 0;
		for (List l : lists) {
			sz += l.size();
		}
		return sz;
	}

	private int getIndex(String name) {
		for (int i = 0; i < names.length; i++) {
			if (name.equals(names[i])) {
				return i;
			}
		}
		return -1;
	}
}
