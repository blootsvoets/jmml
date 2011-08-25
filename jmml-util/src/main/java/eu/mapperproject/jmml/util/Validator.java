package eu.mapperproject.jmml.util;

/**
 *
 * @author Joris Borgdorff
 */
public interface Validator<V> {
	public boolean isValid(V element);
}
