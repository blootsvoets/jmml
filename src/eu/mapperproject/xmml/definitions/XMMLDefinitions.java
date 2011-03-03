package eu.mapperproject.xmml.definitions;

import java.util.HashMap;
import java.util.Map;

import eu.mapperproject.xmml.util.Tuple;

/**
 * Definitions of multiscale model elements, including datatypes, converters and submodels
 * @author Joris Borgdorff
 *
 */
public class XMMLDefinitions {
	private Map<String,Datatype> datatypes;
	private Map<String,Converter> converters;
	private Map<Tuple<String>,Map<String, Converter>> converterByDatatype;
	private final Map<String,Submodel> submodels;
	
	public XMMLDefinitions(Map<String,Datatype> datatypes, Map<String,Converter> converters, Map<String,Submodel> submodels) {
		this.datatypes = datatypes;
		this.converters = converters;
		this.submodels = submodels;
		
		this.converterByDatatype = new HashMap<Tuple<String>,Map<String, Converter>>();
		for (Map.Entry<String, Converter> converter : converters.entrySet()) {
			Converter c = converter.getValue();
			Tuple<String> t = new Tuple<String>(c.getFrom().getId(), c.getTo().getId());
			
			Map<String, Converter> map = this.converterByDatatype.get(t);
			if (map == null) {
				map = new HashMap<String, Converter>();
				this.converterByDatatype.put(t, map);
			}
			map.put(converter.getKey(), c);
		}
	}

	/**
	 * @return the submodels
	 */
	public Map<String,Submodel> getSubmodels() {
		return submodels;
	}
	
	/**
	 * @param from Datatype to convert from
	 * @param to Datatype to convert to
	 * @return all converters of given datatypes 
	 */
	public Map<String, Converter> getConverters(Datatype from, Datatype to) {
		return this.converterByDatatype.get(new Tuple<String>(from.getId(), to.getId())); 
	}

	/**
	 * Get the converter with the given id
	 */
	public Converter getConverter(String id) {
		return this.converters.get(id); 
	}
}
