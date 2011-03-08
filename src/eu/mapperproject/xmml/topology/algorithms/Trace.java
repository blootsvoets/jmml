package eu.mapperproject.xmml.topology.algorithms;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/** Keeps track of which index objects had, when last referenced
  * All of the functions 
  */
public class Trace<T> {
	private final Map<T, Integer> map;

	public Trace() {
		this.map = new HashMap<T, Integer>();
	}

	/** Constructs an independent trace with the same values as the given one */
	public Trace(Trace<T> t) {
		this.map = new HashMap<T, Integer>();
		this.map.putAll(t.map);
	}

	/** Add an index to an object */
	public void put(T o, int i) {
		this.map.put(o, i);
	}

	/** Merges the values of the given trace with the current trace, choosing the largest of the values */
	public Collection<T> merge(Trace<T> t) {
		Collection<T> ret = new HashSet<T>();
		for (T key : t.map.keySet()) {
			Integer newval = t.map.get(key);
			Integer val = map.get(key);
			if (val == null || newval.compareTo(val) > 0) {
				map.put(key, newval);
				ret.add(key);
			}
		}
		return ret;
	}

	/** Overrides the values of the given trace with the current trace, selected by given collection */
	public void override(Trace<T> t, Collection<T> select) {
		for (T key : select) {
			this.map.put(key, t.map.get(key));
		}
	}

	/** Calculates the next value for given object, and sets it in the trace.
	 * If the object has not been instantiated in this trace, it returns and stores 0.
	 */
	public int nextInt(T o) {
		Integer n = this.map.get(o);

		if (n == null) {
			n = new Integer(0);
		} else {
			n++;
		}
		this.map.put(o, n);

		return n;
	}

	/** Get the current value for given object. May not be called if the object is not instantiated in the
	 * trace yet. Does not change the trace.
	 * @throws IllegalStateException if the given object is not yet instantiated
	 */
	public int currentInt(T o) {
		Integer n = this.map.get(o);
		if (n == null) {
			throw new IllegalStateException("CurrentInt can only be called if the object is instantiated.");
		}
		return n;
	}
	
	/** Get the previous value for given object. May not be called if the object is not instantiated in the
	 * trace yet, or in its first iteration.
	 * @throws IllegalStateException if the given object is not yet instantiated or in its first iteration
	 */
	public int previousInt(T o) {
		Integer n = this.map.get(o);
		if (n == null || n == 0) {
			throw new IllegalStateException("Previous can only be called if the object is instantiated and not in the first state.");			
		}
		n--;
		this.map.put(o, n);
		return n;
	}

	/** Returns whether given object has been instantiated in this trace */
	public boolean isInstantiated(T o) {
		return this.map.containsKey(o);
	}

	/** Removes and thus uninstantiates given object from this trace */
	public void reset(T o) {
		this.map.remove(o);
	}

	@Override
	public String toString() {
		String ret = "{ ";
		for (Map.Entry<T, Integer> entry : this.map.entrySet()) {
			ret += entry.getKey() + ":" + entry.getValue() + " ";
		}
		return ret + "}";
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !this.getClass().equals(o.getClass())) return false;
		return this.map.equals(((Trace<?>) o).map);
	}
}
