/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.definitions;

import eu.mapperproject.jmml.ModelMetadata;
import java.util.Map;

/**
 * Represents a computational element with ports
 * @author Joris Borgdorff
 */
public abstract class AbstractElement extends AbstractImplementation {
	protected final Map<String,Port> in;
	protected final Map<String,Port> out;
	
	public AbstractElement(ModelMetadata meta, Map<String,Port> inPorts, Map<String,Port> outPorts) {
		super(meta);
		this.in = inPorts;
		this.out = outPorts;
	}
}
