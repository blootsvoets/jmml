/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Definition;
import eu.mapperproject.jmml.specification.Definitions;
import eu.mapperproject.jmml.specification.Domain;
import eu.mapperproject.jmml.specification.Filter;
import eu.mapperproject.jmml.specification.Formula;
import eu.mapperproject.jmml.specification.Instance;
import eu.mapperproject.jmml.specification.InstancePort;
import eu.mapperproject.jmml.specification.Mapper;
import eu.mapperproject.jmml.specification.ObjectFactory;
import eu.mapperproject.jmml.specification.Ports;
import eu.mapperproject.jmml.specification.Submodel;
import eu.mapperproject.jmml.specification.Topology;
import eu.mapperproject.jmml.specification.Unit;
import javax.xml.bind.annotation.XmlRegistry;

/**
 *
 * @author jborgdo1
 */
@XmlRegistry
public class ObjectFactoryAnnotated extends ObjectFactory {
	
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
}
