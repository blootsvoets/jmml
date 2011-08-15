package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Range;
import eu.mapperproject.jmml.specification.Scale;
import eu.mapperproject.jmml.specification.Unit;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedScale extends Scale {
	@Override
	public void setRegularDelta(Unit u) {
		if (this.delta == null) {
			this.regularDelta = u;
		}
	}
	@Override
	public void setRegularTotal(Unit u) {
		if (this.total == null) {
			this.regularTotal = u;
		}
	}
	
	@Override
	public void setTotal(Range r) {
		AnnotatedRange ar = (AnnotatedRange)r;
		if (ar != null & ar.isRegular()) {
			this.regularTotal = new AnnotatedUnit();
			this.regularTotal.setValue(ar.getMin().getValue());
		}
		else {
			this.regularTotal = null;
			this.total = ar;
		}
	}
	@Override
	public void setDelta(Range r) {
		AnnotatedRange ar = (AnnotatedRange)r;
		if (ar != null & ar.isRegular()) {
			this.regularDelta = new AnnotatedUnit();
			this.regularDelta.setValue(ar.getMin().getValue());
		}
		else {
			this.regularDelta = null;
			this.delta = ar;
		}
	}
}
