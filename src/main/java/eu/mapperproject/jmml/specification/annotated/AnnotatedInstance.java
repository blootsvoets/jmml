/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Instance;
import eu.mapperproject.jmml.specification.Mapper;
import eu.mapperproject.jmml.specification.Submodel;
import eu.mapperproject.jmml.specification.graph.Numbered;

/**
 *
 * @author jborgdo1
 */
public class AnnotatedInstance extends Instance implements Numbered {
	protected int number;
	protected Mapper mapperInst;
	protected Submodel submodelInst;
	
	@Override
	public void setId(String value) {
		if (value != null) {
			this.id = value;
		}
    }

	
	@Override
	public void setMapper(String map) {
		super.setMapper(map);
		if (map != null) {
			if (this.submodel != null) {
				throw new IllegalStateException("Cannot create an instance of both a submodel and a mapper.");
			}
			if (this.id == null) {
				this.id = map;
			}
			this.submodelInst = null;
			this.mapperInst = ObjectFactoryAnnotated.getModel().getDefinitions().getMapper(mapper);
		}
	}
	
	public Mapper getMapperInstance() {
		return this.mapperInst;
	}
	
	@Override
	public void setSubmodel(String sub) {
		super.setSubmodel(sub);
		if (sub != null) {
			if (this.mapper != null) {
				throw new IllegalStateException("Cannot create an instance of both a submodel and a mapper.");
			}
			if (this.id == null) {
				this.id = sub;
			}
		}
	}

	public Submodel getSubmodelInstance() {
		return this.submodelInst;
	}
	
	public boolean ofSubmodel() {
		return this.submodelInst != null;
	}
	
	public AnnotatedPorts getPorts() {
		if (this.submodelInst != null) {
			return (AnnotatedPorts)this.submodelInst.getPorts();
		}
		else if (this.mapperInst != null) {
			return (AnnotatedPorts)this.mapperInst.getPorts();
		}
		else {
			return null;
		}
	}
	
	@Override
	public boolean deepEquals(Object o) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public int getNumber() {
		return this.number;
	}
	
	@Override
	public void setNumber(int num) {
		this.number = num;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		return this.number == ((AnnotatedInstance)o).number;
	}
	
	@Override
	public int hashCode() {
		return number;
	}
	
	@Override
	public String toString() {
		return this.getClass() + "@" + this.id + " " + mapper + " " + submodel;
	}
}
