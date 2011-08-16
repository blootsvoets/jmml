package eu.mapperproject.jmml.definitions;

import eu.mapperproject.jmml.ModelMetadata;
import java.util.Map;

/**
 * Represents a computational element with ports
 * @author Joris Borgdorff
 */
public abstract class AbstractElement extends AbstractImplementation {
	protected final Map<String,Port> in;
	protected final Map<String,Port> out;
	
	public AbstractElement(ModelMetadata meta, Map<String,Port> inPorts, Map<String,Port> outPorts) {
		super(meta);
		this.in = inPorts;
		this.out = outPorts;
	}
	
	/** Get an in port by its name */
	public Port getInPort(String name) {
		return in.get(name);
	}

	/** Get an out port by its name */
	public Port getOutPort(String name) {
		return out.get(name);
	}
}
