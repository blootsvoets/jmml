/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.definitions;

import eu.mapperproject.jmml.ModelMetadata;

/**
 *
 * @author Joris Borgdorff
 */
public abstract class AbstractImplementation {
	protected final ModelMetadata meta;
	
	public AbstractImplementation(ModelMetadata meta) {
		this.meta = meta;
	}
}
