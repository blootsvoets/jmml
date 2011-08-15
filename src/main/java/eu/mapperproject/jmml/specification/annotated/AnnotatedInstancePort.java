/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.InstancePort;
import eu.mapperproject.jmml.specification.Port;
import eu.mapperproject.jmml.specification.Submodel;

/**
 *
 * @author jborgdo1
 */
public class AnnotatedInstancePort extends InstancePort {
	private transient AnnotatedPort port;
	private transient AnnotatedInstance instance;
	
	public AnnotatedPort getPort() {
		return port;
	}

	public AnnotatedInstance getInstance() {
		return instance;
	}
		
	@Override
	public void setValue(String value) {
		super.setValue(value);
		String[] split = value.split("\\.");
		this.instance = ObjectFactoryAnnotated.getModel().getTopology().getInstance(split[0]);
		this.port = this.instance.getPorts().getPort(split[1]);
	}
}
