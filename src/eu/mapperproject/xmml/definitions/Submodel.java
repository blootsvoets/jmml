package eu.mapperproject.xmml.definitions;

import java.util.HashMap;
import java.util.Map;

public class Submodel {
	Map<String,Port> in;
	Map<String,Port> out;
	Map<String,ScaleRange>
	
	public Submodel() {
		this.in = new HashMap<String,Port>();
		this.out = new HashMap<String,Port>();
	}
}
