/**
 * 
 */
package eu.mapperproject.xmml.util;

/**
 * A tuple of values of a single type
 * @author Joris Borgdorff
 *
 */
public class Tuple<T> {
	private final T left, right;

	public Tuple(T left, T right) {
		this.left = left;
		this.right = right;
	}

	/**
	 * @return the left
	 */
	public T getLeft() {
		return left;
	}

	/**
	 * @return the right
	 */
	public T getRight() {
		return right;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Tuple<?> t = (Tuple<?>)o;
		return t.left.equals(left) && t.right.equals(right);
	}
	
	@Override
	public int hashCode() {
		return 31*left.hashCode() + right.hashCode();
	}
	
	@Override
	public String toString() {
		return "<" + left + "," + right + ">";
	}
}
