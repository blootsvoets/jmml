package eu.mapperproject.jmml.definitions;

import eu.mapperproject.jmml.ModelMetadata;
import java.util.Map;

/**
 * Definition of a mapper.
 * 
 * @author Joris Borgdorff
 */
public class Mapper extends AbstractElement {

	public enum Type {
		FAN_IN, FAN_OUT;
	}
	
	private Type type;
	
	public Mapper(ModelMetadata meta, Map<String,Port> inPorts, Map<String,Port> outPorts) {
		super(meta, inPorts, outPorts);
	}
	
	@Override
	public boolean deepEquals(Object o) {
		throw new UnsupportedOperationException("Not supported yet.");
	}	
}
