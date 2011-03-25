/**
 * 
 */
package eu.mapperproject.jmml.topology;

import eu.mapperproject.jmml.definitions.Submodel.SEL;

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
		if (o == null || getClass() != o.getClass()) return false;
		InstanceOperator io = (InstanceOperator)o;
		return this.instance.getNumber() == io.instance.getNumber() && this.operator == io.operator;
	}
	
	@Override
	public int hashCode() {
		return 31*(this.operator == null ? 0 : this.operator.hashCode()) + this.instance.getId().hashCode();
	}
	
	@Override
	public String toString() {
		return this.instance.getId() + "(" + this.operator + ")";
	}

	/**
	 * Get the operator
	 */
	public SEL getOperator() {
		return this.operator;
	}
	
	/** Get the instance */
	public Instance getInstance() {
		return this.instance;
	}
}
