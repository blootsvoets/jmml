/*
 * 
 */
package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Apply;
import eu.mapperproject.jmml.specification.Filter;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedApply extends Apply {
	private transient AnnotatedFilter filterInst;
	
	public AnnotatedFilter getFilterInstance() {
		if (this.filterInst == null) this.setFilter(this.filter);
		return this.filterInst;
	}
	
	@Override
	public void setFilter(String f) {
		this.filter = f;
		this.filterInst = ObjectFactoryAnnotated.getModel().getDefinitions().getFilter(f);
	}
	
	@Override
	public AnnotatedFormula getFactor() {
		return (AnnotatedFormula)(this.factor != null ? factor : this.getFilterInstance().getFactor());
	}
}
