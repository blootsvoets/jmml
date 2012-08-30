package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.util.Identifiable;
import eu.mapperproject.jmml.specification.Datatype;
import eu.mapperproject.jmml.specification.Definitions;
import eu.mapperproject.jmml.specification.Filter;
import eu.mapperproject.jmml.specification.Mapper;
import eu.mapperproject.jmml.specification.Submodel;
import eu.mapperproject.jmml.specification.Terminal;
import eu.mapperproject.jmml.specification.util.DistinguishClass;
import eu.mapperproject.jmml.util.Distinguisher;
import eu.mapperproject.jmml.util.UniqueLists;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedDefinitions extends Definitions {
	
	public AnnotatedDefinitions() {
		Distinguisher<Class<?>,Identifiable> dist = new DistinguishClass(new Class[] {Filter.class, Mapper.class, Submodel.class, Terminal.class});
		terminalOrFilterOrMapper = new UniqueLists<Class<?>,AnnotatedDefinition>(dist);
		dist = new DistinguishClass(new Class[] {Datatype.class});
		this.datatype = new UniqueLists<Class<?>,Datatype>(dist);
	}
	
	public Mapper getMapper(String id) {
		return (Mapper)((UniqueLists)this.terminalOrFilterOrMapper).getById(1, id);
	}

	public AnnotatedFilter getFilter(String id) {
		return (AnnotatedFilter)((UniqueLists)this.terminalOrFilterOrMapper).getById(0, id);
	}

	public Submodel getSubmodel(String id) {
		return (Submodel)((UniqueLists)this.terminalOrFilterOrMapper).getById(2, id);
	}

	public Terminal getTerminal(String id) {
		return (Terminal)((UniqueLists)this.terminalOrFilterOrMapper).getById(3, id);
	}

	public Datatype getDatatype(String id) {
		return (Datatype)((UniqueLists)this.datatype).getById(id);
	}
}
