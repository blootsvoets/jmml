/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Unit;
import eu.mapperproject.jmml.specification.numerical.SIUnit;
import java.text.ParseException;

/**
 *
 * @author jborgdo1
 */
public class AnnotatedUnit extends Unit {
	private transient SIUnit unit;

	public SIUnit interpret() {
		if (this.unit == null) {
			this.unit = value == null ? null : SIUnit.valueOf(value);
		}
		return unit;
	}
	
	@Override
	public void setValue(String value) {
		super.setValue(value);
		this.unit = SIUnit.valueOf(value);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!o.getClass().equals(getClass())) return false;
		AnnotatedUnit au = (AnnotatedUnit)o;
		return (this.interpret() == null ? au.interpret() == null : this.unit.equals(au.unit));
	}

	@Override
	public int hashCode() {
		return (this.interpret() != null ? this.unit.hashCode() : 0);
	}
}
