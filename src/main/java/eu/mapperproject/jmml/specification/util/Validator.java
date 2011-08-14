/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.util;

/**
 *
 * @author Joris Borgdorff
 */
public interface Validator<V> {
	public boolean isValid(V element);
}
