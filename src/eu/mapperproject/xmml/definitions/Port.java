package eu.mapperproject.xmml.definitions;

import eu.mapperproject.xmml.definitions.Submodel.SEL;

/**
 * An in or out port of a submodel
 * @author Joris Borgdorff
 *
 */
public class Port {
	public enum Type {
		STATE, NORMAL;
	}

	private final SEL operator;
	private final Datatype datatype;
	private final Type type;
	private final String id;

	/**
	 * @param id
	 * @param operator
	 * @param datatype
	 * @param state
	 */
	public Port(String id, SEL operator, Datatype datatype, Type state) {
		this.id = id;
		this.operator = operator;
		this.datatype = datatype;
		this.type = state;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the operator
	 */
	public SEL getOperator() {
		return operator;
	}

	/**
	 * @return the datatype
	 */
	public Datatype getDatatype() {
		return datatype;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

}
