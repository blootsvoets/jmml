package eu.mapperproject.xmml.definitions;

import eu.mapperproject.xmml.Identifiable;
import eu.mapperproject.xmml.util.graph.Edge;

/**
 * Define a data converter, from one datatype to another
 * @author Joris Borgdorff
 *
 */
public class Converter implements Identifiable, Edge<Datatype> {
	private final Datatype to;
	private final Datatype from;
	private final String id;
	private final String reqName;
	private final Datatype reqData;
	private final String src;

	public Converter(String id, Datatype from, Datatype to) {
		this(id, from, to, null, null, null);
	}

	public Converter(String id, Datatype from, Datatype to, String reqName, Datatype reqData, String src) {
		this.id = id;
		this.from = from;
		this.to = to;
		this.reqName = reqName;
		this.reqData = reqData;
		this.src = src;
	}

	/**
	 * @return the datatype that the converter converts from
	 */
	public Datatype getFrom() {
		return this.from;
	}

	/**
	 * @return the datatype that the converter converts to
	 */
	public Datatype getTo() {
		return to;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
}
