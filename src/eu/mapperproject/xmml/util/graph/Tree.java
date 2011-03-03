package eu.mapperproject.xmml.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A tree of child elements.
 * 
 * The root is kept by the tree and the parents by the nodes, but the children are kept in the data structure.
 * @author Joris Borgdorff
 *
 * @param <T> type of elements in the tree
 */
public class Tree<T extends Child<T>> implements Iterable<T> {
	private final Map<T, Collection<T>> childMap;
	private T root;
	private final Collection<T> childless;
	
	/** Create an empty tree. */
	public Tree() {
		this.root = null;
		this.childMap = new HashMap<T, Collection<T>>();
		this.childless = new ArrayList<T>(0);
	}
	
	/** Create a tree with given elements.
	 * These elements are supposed to form a tree using their given parent.
	 * @param elems elements to be added
	 */
	public Tree(Collection<T> elems) {
		this();
		this.addAll(elems);
	}
	
	/** Add a single element to the tree.
	 * This will, additionally, add all ancestors of that element to the tree.
	 * @param t element to be added
	 * @throws IllegalArgumentException if the element causes a different root to be added than already exists
	 */
	public void add(T t) {
		if (t.isRoot()) {
			if (this.root == null) this.root = t;
			else if (!this.root.equals(t)) throw new IllegalArgumentException("Tree can not contain more than one root");
		}
		else {
			T parent = t.parent();
			
			Collection<T> col = this.childMap.get(parent);
			if (col == null) {
				col = new HashSet<T>();
				this.childMap.put(parent, col);
			}
			col.add(t);
			
			add(parent);
		}
	}
	
	/** Add all elements to a tree
	 * 
	 * @see add(T t)
	 */
	public void addAll(Collection<T> elems) {
		for (T c : elems) {
			add(c);
		}
	}
	
	/** Get all children of a certain element
	 * Returns an empty list the element is a leaf or not in the tree 
	 * @param elem of which to get the children
	 * @return
	 */
	public Collection<T> getChildren(T elem) {
		Collection<T> col = childMap.get(elem);
		return col == null ? this.childless : col;
	}
	
	/** Get the root of the tree */
	public T getRoot() {
		return this.root;
	}
	
	/** Get a common ancestor of two child elements, or if equal, return the first node.
	 * This method does left-first search for a common ancestor. It will return null if no
	 * common ancestor is found. */
	public static <T extends Child<T>> T getCommonAncestor(T n1, T n2) {
		T n = null;
		if (n1.equals(n2)) {
			n = n1;
		}
		else if (!n1.isRoot()) {
			n = getCommonAncestor(n1.parent(), n2);
		}
		if (n == null && !n2.isRoot()) {
			n = getCommonAncestor(n1, n2.parent());
		}
		
		return n;
	}


	@Override
	public Iterator<T> iterator() {
		return new DepthFirstIterator();
	}

	/** Iterates over this tree in a depth-first fashion */
	private class DepthFirstIterator implements Iterator<T>{
		private final List<T> current;
		private int last;
		
		DepthFirstIterator() {
			current = new ArrayList<T>();
			current.add(getRoot());
			last = 0;
		}

		@Override
		public boolean hasNext() {
			return last >= 0;
		}

		@Override
		public T next() {
			T next = current.remove(last);
			last--;
			
			Collection<T> children = getChildren(next);
			current.addAll(children);
			last += children.size();
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Can not remove from tree");
		}
	}
}
