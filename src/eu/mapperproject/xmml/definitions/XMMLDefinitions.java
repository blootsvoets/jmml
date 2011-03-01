package eu.mapperproject.xmml.definitions;

import java.util.Map;

public class XMMLDefinitions {
	private Map<String,Datatype> datatypes;
	private Map<String,Converter> converters;
	private Map<String,Submodel> submodels;
	
	public XMMLDefinitions(Map<String,Datatype> datatypes, Map<String,Converter> converters, Map<String,Submodel> submodels) {
		this.datatypes = datatypes;
		this.converters = converters;
		this.submodels = submodels;
	}
}
