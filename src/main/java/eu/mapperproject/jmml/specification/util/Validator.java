package eu.mapperproject.jmml.specification.util;

/**
 *
 * @author Joris Borgdorff
 */
public interface Validator<V> {
	public boolean isValid(V element);
}
