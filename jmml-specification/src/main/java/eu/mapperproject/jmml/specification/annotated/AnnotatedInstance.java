package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Instance;
import eu.mapperproject.jmml.specification.Mapper;
import eu.mapperproject.jmml.specification.OptionalChoice;
import eu.mapperproject.jmml.specification.Submodel;
import eu.mapperproject.jmml.specification.graph.Numbered;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedInstance extends Instance implements Numbered {
	private transient int number;
	private transient Mapper mapperInst;
	private transient Submodel submodelInst;
	private transient boolean isfinal;
	
	@Override
	public String getId() {
		if (this.id == null) {
			return this.mapper == null ? this.submodel : this.mapper;
		}
		return this.id;
	}
	
	@Override
	public void setId(String value) {
		if (value != null) {
			this.id = value;
		}
    }

	@Override
	public void setMapper(String map) {
		this.mapper = map;
		if (map != null) {
			if (this.submodel != null) {
				throw new IllegalStateException("Cannot create an instance of both a submodel and a mapper.");
			}
			if (this.id == null) {
				this.id = map;
			}
			this.mapperInst = ObjectFactoryAnnotated.getModel().getDefinitions().getMapper(this.mapper);
		}
	}
	
	public Mapper getMapperInstance() {
		if (this.mapperInst == null) this.setMapper(this.mapper);
		return this.mapperInst;
	}
	
	@Override
	public void setSubmodel(String sub) {
		this.submodel = sub;
		if (sub != null) {
			if (this.mapper != null) {
				throw new IllegalStateException("Cannot create an instance of both a submodel and a mapper.");
			}
			if (this.id == null) {
				this.id = sub;
			}
			this.submodelInst = ObjectFactoryAnnotated.getModel().getDefinitions().getSubmodel(this.submodel);
		}
	}

	public Submodel getSubmodelInstance() {
		if (this.submodelInst == null) this.setSubmodel(this.submodel);
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
	public AnnotatedScale getTimescale() {
		if (this.ofSubmodel()) {
			if (this.timescale != null){
				return this.timescale;
			}
			else {
				return (AnnotatedScale) this.getSubmodelInstance().getTimescale();
			}
		}
		return null;
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
		return this.getClass().getSimpleName()
			+ "(" + (ofSubmodel() ? "submodel " : "mapper ")
			+ this.id
			+ "<" + (ofSubmodel() ? submodel : mapper) + ">)";
	}
	
	public boolean isFinal() {
		return isfinal;
	}

	public void isFinal(boolean isfinal) {
		this.isfinal = isfinal;
	}
	
	/**
	 * Whether this instance should be completed after a given number of timesteps
	 * @param steps the number of timesteps so far
	 */
	public boolean isCompleted(int steps) {
		if (this.ofSubmodel()) {
			return this.getTimescale().getSteps() <= steps + 1;
		}
		return true;
	}
	
	@Override
	public OptionalChoice getStateful() {
		if (this.ofSubmodel()) {
			if (this.stateful == null) {
				return this.getSubmodelInstance().getStateful();
			}
			else {
				return this.stateful;
			}
		}
		else {
			return null;
		}
	}
}
