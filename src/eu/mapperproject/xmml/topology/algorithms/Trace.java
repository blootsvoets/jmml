package eu.mapperproject.xmml.topology.algorithms;

import cern.colt.list.IntArrayList;
import java.util.Arrays;

/** Keeps track of which index objects had, when last referenced
  * All of the functions 
  */
public class Trace {
	private int[] trace;
	private final IntArrayList[] merge;

	public Trace() {
		merge = new IntArrayList[2];
		merge[0] = new IntArrayList(5);
		merge[1] = new IntArrayList(5);

		int initsize = 10;
		this.trace = resize(new int[0], initsize);
	}

	/** Constructs an independent trace with the same values as the given one */
	public Trace(Trace t) {
		this.trace = Arrays.copyOf(t.trace, t.trace.length);
		this.merge = t.merge;
	}

	/** Add an index to an object */
	public void put(int num, int i) {
		this.resizeTrace(num);
		this.trace[num] = i;
	}

	/** Merges the values of the given trace with the current trace, choosing the largest of the values */
	public IntArrayList[] merge(Trace t) {
		IntArrayList eq = merge[0], gt = merge[1];
		eq.clear(); gt.clear();

		int len = Math.min(t.trace.length, this.trace.length);

		for (int i = 0; i < len; i++) {
			if (t.trace[i] > this.trace[i]) {
				gt.add(i);
				this.trace[i] = t.trace[i];
			}
			else if (t.trace[i] == this.trace[i] && t.trace[i] > -1) {
				eq.add(i);
			}
		}
		if (t.trace.length > this.trace.length) {
			this.resizeTrace(t.trace.length - 1);
			System.arraycopy(t.trace, len, this.trace, len, t.trace.length - len);
			for (int i = len; i < t.trace.length; i++) {
				if (this.trace[i] > -1) {
					gt.add(i);
				}
			}
		}

		return merge;
	}

	/** Overrides the values of the given trace with the current trace, selected by given collection */
	public void override(Trace t, IntArrayList select) {
		int len = select.size();
		for (int i = 0; i < len; i++) {
			int num = select.get(i);
			this.put(num, t.trace[num]);
		}
	}

	/** Merges the values of the given trace with the current trace, selected by given collection */
	public void merge(Trace t, IntArrayList select) {
		int len = select.size();
		for (int i = 0; i < len; i++) {
			int num = select.get(i);
			if (t.trace[num] > this.trace[num]) {
				this.put(num, t.trace[num]);
			}
		}
	}

	/** Calculates the next value for given object, and sets it in the trace.
	 * If the object has not been instantiated in this trace, it returns and stores 0.
	 */
	public int nextInt(int num) {
		this.resizeTrace(num);
		this.trace[num]++;

		return this.trace[num];
	}

	/** Get the current value for given object. May not be called if the object is not instantiated in the
	 * trace yet. Does not change the trace.
	 * @throws IllegalStateException if the given object is not yet instantiated
	 */
	public int currentInt(int num) {
		if (this.trace.length <= num || this.trace[num] == -1) {
			throw new IllegalStateException("CurrentInt can only be called if the object is instantiated.");
		}
		return this.trace[num];
	}
	
	/** Get the previous value for given object. May not be called if the object is not instantiated in the
	 * trace yet, or in its first iteration.
	 * @throws IllegalStateException if the given object is not yet instantiated or in its first iteration
	 */
	public int previousInt(int num) {
		if (this.trace.length <= num || this.trace[num] <= 0) {
			throw new IllegalStateException("Previous can only be called if the object is instantiated and not in the first state.");			
		}
		this.trace[num]--;
		return this.trace[num];
	}

	/** Returns whether given object has been instantiated in this trace */
	public boolean isInstantiated(int num) {
		return this.trace.length > num && this.trace[num] != -1;
	}

	/** Removes and thus uninstantiates given object from this trace */
	public void reset(int num) {
		if (this.trace.length > num && this.trace[num] != -1) {
			this.trace[num] = -1;
		}
	}

	@Override
	public String toString() {
		String ret = "{ ";
		for (int i = 0; i < this.trace.length; i++) {
			if (this.trace[i] != -1) {
				ret += i + ":" + this.trace[i] + " ";
			}
		}
		return ret + "}";
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		return Arrays.equals(trace, ((Trace)o).trace);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.trace);
	}

	/** Resizes an int array and fills up to a certain size with
	 * -1. The returned size will be max(2*oldlen+1, maxsize).
	 * @param input input array, may not be null
	 * @param maxsize size of the returned array
	 * @return
	 */
	private int[] resize(int[] input, int maxsize) {
		int oldlen = input.length;
		int len = oldlen * 2 + 1;
		if (maxsize >= len) {
			len = maxsize + 1;
		}
		input = Arrays.copyOf(input, len);
		Arrays.fill(input, oldlen, len, -1);

		return input;
	}

	/** Resizes the trace array array and fills up to a certain size with
	 * -1. The new size will be max(2*oldlen+1, index).
	 * @param index size of the trace array
	 * @return
	 */
	private void resizeTrace(int index) {
		if (this.trace.length <= index) {
			this.trace = this.resize(trace, index);
		}
	}
}
