/**
 * 
 */
package eu.mapperproject.jmml.topology;

import eu.mapperproject.jmml.specification.SEL;
import eu.mapperproject.jmml.specification.annotated.AnnotatedInstance;

/**
 * Represent one operation of an instance
 * @author Joris Borgdorff
 *
 */
public class InstanceOperator {
	private final AnnotatedInstance instance;
	private final SEL operator;
	
	public InstanceOperator(AnnotatedInstance instance, SEL operator) {
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
	public AnnotatedInstance getInstance() {
		return this.instance;
	}
}
