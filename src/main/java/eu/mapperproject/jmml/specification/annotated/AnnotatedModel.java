/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Model;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedModel extends Model {
	@Override
	public AnnotatedDefinitions getDefinitions() {
		return this.definitions;
	}
	
	@Override
	public AnnotatedTopology getTopology() {
		return this.topology;
	}
}
