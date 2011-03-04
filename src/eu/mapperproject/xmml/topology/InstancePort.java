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
	
	/**
	 * Get the operation that is associated with this port
	 */
	public InstanceOperator getInstanceOperator() {
		return new InstanceOperator(this.instance, this.port.getOperator());
	}

	@Override
	public String toString() {
		return this.instance.getId() + "." + this.port.getId();
	}
}
