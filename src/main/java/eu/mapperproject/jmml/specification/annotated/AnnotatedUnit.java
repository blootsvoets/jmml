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
	private SIUnit unit;

	public SIUnit interpret() {
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
		return (this.unit == null ? au.unit == null : this.unit.equals(au.unit));
	}

	@Override
	public int hashCode() {
		return (this.unit != null ? this.unit.hashCode() : 0);
	}
}
