package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.util.Identifiable;
import eu.mapperproject.jmml.specification.Datatype;
import eu.mapperproject.jmml.specification.Port;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedPort extends Port implements Identifiable {
	private transient Datatype datatypeInst;
	
	public Datatype getDatatypeInstance() {
		if (this.datatypeInst == null) this.setDatatype(this.datatype);
		return datatypeInst;
	}
	
	@Override
	public void setDatatype(String dt) {
		this.datatype = dt;
		this.datatypeInst = ObjectFactoryAnnotated.getModel().getDefinitions().getDatatype(this.datatype);
	}
	
	@Override
	public boolean deepEquals(Object o) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		return this.id.equals(((AnnotatedPort)o).id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		if (this.operator != null) {
			return this.id + "(" + this.operator + ")";
		}
		else {
			return this.id;
		}
	}
}
