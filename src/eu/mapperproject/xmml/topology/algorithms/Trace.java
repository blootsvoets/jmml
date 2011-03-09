package eu.mapperproject.xmml.topology.algorithms;

import eu.mapperproject.xmml.util.Numbered;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/** Keeps track of which index objects had, when last referenced
  * All of the functions 
  */
public class Trace<T extends Numbered> {
	private int[] trace;
	private final List<T> objects;
	private int size;
	private final static List emptyMerge;
	static {
		Collection col = new ArrayList(0);
		emptyMerge = new ArrayList(2);
		emptyMerge.add(col);
		emptyMerge.add(col);
	}

	public Trace() {
		int initsize = 10;
		this.trace = new int[initsize];
		this.objects = new ArrayList<T>();

		for (int i = 0; i < initsize; i++) {
			this.trace[i] = -1;
			this.objects.add(null);
			this.size = 0;
		}
	}

	/** Constructs an independent trace with the same values as the given one */
	public Trace(Trace<T> t) {
		this.trace = new int[t.trace.length];
		System.arraycopy(t.trace, 0, this.trace, 0, this.trace.length);
		this.objects = t.objects;
		this.size = t.size;
	}

	/** Add an index to an object */
	public void put(T o, int i) {
		int num = o.getNumber();
		this.addObject(o, num);
		this.trace[num] = i;
	}

	/** Add the given object to the object list, if necessary */
	private void addObject(T o, int num) {
		if (this.trace.length <= num) {
			int initsize = Math.max(this.trace.length * 2, num + 1);
			int[] tmp = new int[initsize];
			System.arraycopy(this.trace, 0, tmp, 0, this.trace.length);
			for (int i = this.trace.length; i < initsize; i++) {
				tmp[i] = -1;
			}
			this.trace = tmp;
		}
		if (this.trace[num] == -1) {
			this.size++;
			while (this.objects.size() <= num) {
				this.objects.add(null);
			}
			this.objects.set(num, o);
		}
	}

	/** Merges the values of the given trace with the current trace, choosing the largest of the values */
	public List<Collection<T>> merge(Trace<T> t, boolean track) {
		List<Collection<T>> ret = null;
		Collection<T> eq = null, gt = null;

		if (track) {
			ret = new ArrayList<Collection<T>>(2);
			eq = new ArrayList<T>((this.size + 1) / 2);
			gt = new ArrayList<T>(Math.max(1, (t.size - this.size) + (this.size + 1) / 2));
			ret.add(eq);
			ret.add(gt);
		}

		int len = Math.min(t.trace.length, this.trace.length);

		for (int i = 0; i < len; i++) {
			if (t.trace[i] >= this.trace[i] && t.trace[i] > -1) {
				T o = t.objects.get(i);
				if (track) {
					if (t.trace[i] == this.trace[i]) eq.add(o);
					else gt.add(o);
				}

				this.trace[i] = t.trace[i];
				this.objects.set(i, o);
			}
		}
		if (t.trace.length > this.trace.length) {
			int[] tmp = new int[t.trace.length];
			System.arraycopy(this.trace, 0, tmp, 0, len);
			this.trace = tmp;
			for (int i = len; i < t.trace.length; i++) {
				if (t.trace[i] > -1) {
					this.trace[i] = t.trace[i];
					T o = t.objects.get(i);
					this.addObject(o, i);
					if (track) gt.add(o);
				}
			}
		}

		return track ? ret : emptyMerge;
	}

	/** Overrides the values of the given trace with the current trace, selected by given collection */
	public void override(Trace<T> t, Collection<T> select) {
		for (T key : select) {
			int num = key.getNumber();
			this.put(key, t.trace[num]);
		}
	}

	/** Merges the values of the given trace with the current trace, selected by given collection */
	public void merge(Trace<T> t, Collection<T> select) {
		for (T key : select) {
			int num = key.getNumber();
			if (t.trace[num] > this.trace[num]) {
				this.put(key, t.trace[num]);
			}
		}
	}

	/** Calculates the next value for given object, and sets it in the trace.
	 * If the object has not been instantiated in this trace, it returns and stores 0.
	 */
	public int nextInt(T o) {
		int num = o.getNumber();
		this.addObject(o, num);
		this.trace[num]++;

		return this.trace[num];
	}

	/** Get the current value for given object. May not be called if the object is not instantiated in the
	 * trace yet. Does not change the trace.
	 * @throws IllegalStateException if the given object is not yet instantiated
	 */
	public int currentInt(T o) {
		int num = o.getNumber();
		if (this.trace.length <= num || this.trace[num] == -1) {
			throw new IllegalStateException("CurrentInt can only be called if the object is instantiated.");
		}
		return this.trace[num];
	}
	
	/** Get the previous value for given object. May not be called if the object is not instantiated in the
	 * trace yet, or in its first iteration.
	 * @throws IllegalStateException if the given object is not yet instantiated or in its first iteration
	 */
	public int previousInt(T o) {
		int num = o.getNumber();
		if (this.trace.length <= num || this.trace[num] <= 0) {
			throw new IllegalStateException("Previous can only be called if the object is instantiated and not in the first state.");			
		}
		this.trace[num]--;
		return this.trace[num];
	}

	/** Returns whether given object has been instantiated in this trace */
	public boolean isInstantiated(T o) {
		int num = o.getNumber();
		return this.trace.length <= num || this.trace[num] != -1;
	}

	/** Removes and thus uninstantiates given object from this trace */
	public void reset(T o) {
		int num = o.getNumber();
		if (this.trace.length >= num && this.trace[num] != -1) {
			this.trace[num] = -1;
			this.size--;
		}
	}

	@Override
	public String toString() {
		String ret = "{ ";
		for (int i = 0; i < this.trace.length; i++) {
			if (this.trace[i] != -1) {
				ret += this.objects.get(i) + ":" + this.trace[i] + " ";
			}
		}
		return ret + "}";
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !this.getClass().equals(o.getClass())) return false;
		return Arrays.equals(trace, ((Trace<?>)o).trace);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.trace);
	}
}
