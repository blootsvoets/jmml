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
}
