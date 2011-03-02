/**
 * 
 */
package eu.mapperproject.xmml.topology;

import eu.mapperproject.xmml.definitions.Port;

/**
 * The port of an instance
 * @author Joris Borgdorff
 *
 */
public class InstancePort {
	private final Instance instance;
	private final Port port;

	public InstancePort(Instance instance, Port port) {
		this.instance = instance;
		this.port = port;
	}

	@Override
	public String toString() {
		return this.instance.getId() + "." + this.port.getId();
	}
	
	/**
	 * @return the Port of the instance
	 */
	public Port getPort() {
		return port;
	}

	/**
	 * @return the instance
	 */
	public Instance getInstance() {
		return instance;
	}
}
