package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Definition;
import eu.mapperproject.jmml.util.Numbered;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedDefinition extends Definition implements Numbered {
	protected transient int number;

	@Override
	public boolean deepEquals(Object o) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public String getPackage() {
		if (clazz == null) {
			return null;
		}
		int lastDot = clazz.lastIndexOf('.');
		if (lastDot == -1) {
			return null;
		} else {
			return clazz.substring(0, lastDot);
		}
	}
	
	public String getClazzName() {
		if (clazz == null) {
			return id;
		}
		int lastDot = clazz.lastIndexOf('.');
		if (lastDot == -1) {
			return clazz;
		} else {
			return clazz.substring(lastDot+1);
		}
	}
		
	@Override
	public void setNumber(int num) {
		this.number = num;
	}
	
	@Override
	public int getNumber() {
		return this.number;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		return this.number == ((AnnotatedDefinition)o).number;
	}

	@Override
	public int hashCode() {
		return this.number;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + getId() + ")";
	}
}
