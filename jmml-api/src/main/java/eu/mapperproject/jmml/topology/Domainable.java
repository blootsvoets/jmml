/**
 * 
 */
package eu.mapperproject.jmml.topology;

import eu.mapperproject.jmml.specification.annotated.AnnotatedDomain;

/**
 * An interface for any classes that have a domain
 * @author Joris Borgdorff
 *
 */
public interface Domainable {
	/** Get the domain */
	public AnnotatedDomain getDomain();
}
