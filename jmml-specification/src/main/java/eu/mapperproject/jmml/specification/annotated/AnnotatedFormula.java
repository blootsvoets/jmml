package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Formula;
import eu.mapperproject.jmml.util.numerical.InterpretedFormula;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedFormula extends Formula {
	private transient InterpretedFormula formula;

	public InterpretedFormula interpret() {
		if (this.formula == null) this.setValue(this.value);
		return this.formula;
	}
	
	@Override
	public void setValue(String value) {
		this.value = value;
		try {
			this.formula = InterpretedFormula.valueOf(value);
		} catch (ParseException ex) {
			this.formula = null;
			Logger.getLogger(AnnotatedFormula.class.getName()).log(Level.SEVERE, "Could not parse formula: {}", ex);
		}
	}
	
}
