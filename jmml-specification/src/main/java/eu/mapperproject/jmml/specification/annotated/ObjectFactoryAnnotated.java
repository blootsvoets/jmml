package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.*;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRegistry;

/**
 *
 * @author Joris Borgdorff
 */
@XmlRegistry
public class ObjectFactoryAnnotated extends ObjectFactory {
	private static AnnotatedModel m = null;
	
	@Override
	public Model createModel() {
		m = new AnnotatedModel();
		return m;
	}
	
	@Override
	public Domain createDomain() {
		return new AnnotatedDomain();
	}
	
	@Override
	public Unit createUnit() {
		return new AnnotatedUnit();
	}
	
	@Override
	public Definition createDefinition() {
		return new AnnotatedDefinition();
	}
	@Override
	public Definitions createDefinitions() {
		return new AnnotatedDefinitions();
	}
	@Override
	public Formula createFormula() {
		return new AnnotatedFormula();
	}
	@Override
	public InstancePort createInstancePort() {
		return new AnnotatedInstancePort();
	}
	@Override
	public Topology createTopology() {
		return new AnnotatedTopology();
	}
	
	@Override
	public Instance createInstance() {
		return new AnnotatedInstance();
	}

	@Override
	public Submodel createSubmodel() {
		return new Submodel();
	}

	@Override
	public Mapper createMapper() {
		return new Mapper();
	}

	@Override
	public Filter createFilter() {
		return new Filter();
	}

	@Override
	public Ports createPorts() {
		return new AnnotatedPorts();
	}
	
	@Override
	public Param createParam() {
		return new AnnotatedParam();
	}
	
	public static AnnotatedModel getModel() {
		return m;
	}
}
