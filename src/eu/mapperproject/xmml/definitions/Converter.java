package eu.mapperproject.xmml.definitions;
/**
 * Define a data converter, from one datatype to another
 * @author Joris Borgdorff
 *
 */
public class Converter {
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
}
