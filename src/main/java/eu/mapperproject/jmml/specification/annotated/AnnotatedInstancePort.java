/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.InstancePort;

/**
 *
 * @author jborgdo1
 */
public class AnnotatedInstancePort extends InstancePort {
	private String port;

	public String getPort() {
		return port;
	}

	public String getInstance() {
		return value;
	}
	
	@Override
	public String getValue() {
		return value + '.' + port;
	}
	
	@Override
	public void setValue(String value) {
		String[] split = value.split("\\.");
		this.value = split[0];
		this.port = split[1];
	}
}
