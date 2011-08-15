package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Datatype;
import eu.mapperproject.jmml.specification.Definitions;
import eu.mapperproject.jmml.specification.Filter;
import eu.mapperproject.jmml.specification.Mapper;
import eu.mapperproject.jmml.specification.Submodel;
import eu.mapperproject.jmml.specification.graph.Identifiable;
import eu.mapperproject.jmml.specification.util.DistinguishClass;
import eu.mapperproject.jmml.specification.util.Distinguisher;
import eu.mapperproject.jmml.specification.util.UniqueLists;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedDefinitions extends Definitions {
	
	public AnnotatedDefinitions() {
		Distinguisher<Class,Identifiable> dist = new DistinguishClass(new Class[] {Filter.class, Mapper.class, Submodel.class});
		filterOrMapperOrSubmodel = new UniqueLists<Class,AnnotatedDefinition>(dist);
		dist = new DistinguishClass(new Class[] {Datatype.class});
		this.datatype = new UniqueLists<Class,Datatype>(dist);
	}
	
	public Mapper getMapper(String id) {
		return (Mapper)((UniqueLists)this.filterOrMapperOrSubmodel).getById(1, id);
	}

	public Filter getFilter(String id) {
		return (Filter)((UniqueLists)this.filterOrMapperOrSubmodel).getById(0, id);
	}

	public Submodel getSubmodel(String id) {
		return (Submodel)((UniqueLists)this.filterOrMapperOrSubmodel).getById(2, id);
	}

	public Datatype getDatatype(String id) {
		return (Datatype)((UniqueLists)this.datatype).getById(id);
	}
}
