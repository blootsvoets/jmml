/**
 * 
 */
package eu.mapperproject.xmml.topology;

import eu.mapperproject.xmml.definitions.Submodel.SEL;

/**
 * Represent one operation of an instance
 * @author Joris Borgdorff
 *
 */
public class InstanceOperator {
	private final Instance instance;
	private final SEL operator;
	
	public InstanceOperator(Instance instance, SEL operator) {
		this.instance = instance;
		this.operator = operator;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !this.getClass().equals(o.getClass())) return false;
		InstanceOperator io = (InstanceOperator)o;
		return this.operator == io.operator && this.instance.getId().equals(io.instance.getId()); 
	}
	
	@Override
	public int hashCode() {
		return (this.operator == null ? 0 : this.operator.hashCode()) ^ this.instance.getId().hashCode();
	}

	/**
	 * Get the operator
	 */
	public SEL getOperator() {
		return this.operator;
	}
}
