/*
 * 
 */
package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Datatype;
import eu.mapperproject.jmml.specification.Filter;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedFilter extends Filter {
	private transient Datatype datatypeInInst, datatypeOutInst;
	
	public Datatype getDatatypeInInstance() {
		if (this.datatypeInInst == null) this.setDatatypeIn(this.datatypeIn);
		return this.datatypeInInst;
	}
	public Datatype getDatatypeOutInstance() {
		if (this.datatypeInInst == null) this.setDatatypeOut(this.datatypeOut);
		return this.datatypeOutInst;
	}
	
	@Override
	public void setDatatypeIn(String d) {
		super.setDatatypeIn(d);
		this.datatypeInInst = ObjectFactoryAnnotated.getModel().getDefinitions().getDatatype(d);
	}
	@Override
	public void setDatatypeOut(String d) {
		super.setDatatypeOut(d);
		this.datatypeOutInst = ObjectFactoryAnnotated.getModel().getDefinitions().getDatatype(d);
	}
}
