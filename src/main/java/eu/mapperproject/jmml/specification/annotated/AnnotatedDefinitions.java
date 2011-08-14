/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Datatype;
import eu.mapperproject.jmml.specification.Definitions;
import eu.mapperproject.jmml.specification.Filter;
import eu.mapperproject.jmml.specification.Mapper;
import eu.mapperproject.jmml.specification.Submodel;
import eu.mapperproject.jmml.specification.util.DistinguishClass;
import eu.mapperproject.jmml.specification.util.DistinguishQName;
import eu.mapperproject.jmml.specification.util.Distinguisher;
import eu.mapperproject.jmml.specification.util.UniqueLists;
import java.util.List;

/**
 *
 * @author jborgdo1
 */
public class AnnotatedDefinitions extends Definitions {
	
	public AnnotatedDefinitions() {
		Distinguisher dist = new DistinguishClass(new Class[] {Filter.class, Mapper.class, Submodel.class});
		filterOrMapperOrSubmodel = new UniqueLists(dist);		
		System.out.println("aha");
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
		for (Datatype dt : this.datatype) {
			if (dt.getId().equals(id)) {
				return dt;
			}
		}
		return null;
	}
}
