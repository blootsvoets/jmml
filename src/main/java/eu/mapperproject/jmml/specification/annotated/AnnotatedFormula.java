/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Formula;
import eu.mapperproject.jmml.specification.numerical.InterpretedFormula;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jborgdo1
 */
public class AnnotatedFormula extends Formula {
	private transient InterpretedFormula formula;

	public InterpretedFormula interpret() {
		if (this.formula == null) {
			if (value == null) return null;
			try {
				this.formula = InterpretedFormula.valueOf(value);
			} catch (ParseException ex) {
				this.formula = null;
				Logger.getLogger(AnnotatedFormula.class.getName()).log(Level.SEVERE, "Could not parse formula: {}", ex);
			}
		}
		return this.formula;
	}
	
	@Override
	public void setValue(String value) {
		super.setValue(value);
		try {
			this.formula = InterpretedFormula.valueOf(value);
		} catch (ParseException ex) {
			this.formula = null;
			Logger.getLogger(AnnotatedFormula.class.getName()).log(Level.SEVERE, "Could not parse formula: {}", ex);
		}
	}
	
}
