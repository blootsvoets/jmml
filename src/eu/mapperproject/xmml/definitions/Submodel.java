package eu.mapperproject.xmml.definitions;

import java.util.Map;

import eu.mapperproject.xmml.ModelMetadata;
import eu.mapperproject.xmml.Param;
import eu.mapperproject.xmml.util.ParseToken.Optional;

public class Submodel {
	private Map<String,Port> in;
	private Map<String,Port> out;
	private Map<String,Scale> scales;
	private Map<String,Param> params;
	private ModelMetadata meta;
	private boolean initial;
	private Optional stateful;
	private Optional interactive;
	
	public Submodel(ModelMetadata meta, Map<String,Scale> scales, Map<String,Port> in, Map<String,Port> out, Map<String,Param> params, boolean initial, Optional stateful, Optional interactive) {
		this.meta = meta;
		this.scales = scales;
		this.in = in;
		this.out = out;
		this.params = params;
		this.initial = initial;
		this.stateful = stateful;
		this.interactive = interactive;
	}
}
