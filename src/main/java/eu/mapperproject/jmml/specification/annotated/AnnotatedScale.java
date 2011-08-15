package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Range;
import eu.mapperproject.jmml.specification.Scale;
import eu.mapperproject.jmml.specification.Unit;
import eu.mapperproject.jmml.specification.numerical.SIUnit;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedScale extends Scale {
	/**
	 * Calculate the number of steps that can be taken given the ranges
	 * Returns -1 if delta or max is not set or not definite
	 */
	public int getSteps() {
		AnnotatedUnit dt = getRegularDelta(), max = getRegularTotal();
		if ((dt == null && !delta.isDefinite()) || (max == null && !total.isDefinite())) {
			return -1;
		}
		
		SIUnit d = (dt == null ? delta.meanSIUnit() : dt.interpret());
		SIUnit l = (max == null ? total.meanSIUnit() : max.interpret());
		return (int)Math.round(l.div(d).doubleValue());		
	}
	
	@Override
	public AnnotatedUnit getRegularDelta() {
		if (this.delta != null) this.setDelta(delta);
		return (AnnotatedUnit)this.regularDelta;
	}
	@Override
	public AnnotatedUnit getRegularTotal() {
		if (this.total != null) this.setTotal(total);
		return (AnnotatedUnit)this.regularTotal;
	}

	@Override
	public AnnotatedRange getDelta() {
		if (this.regularDelta == null) this.setDelta(delta);
		return this.delta;
	}
	@Override
	public AnnotatedRange getTotal() {
		if (this.regularTotal == null) this.setTotal(total);
		return this.total;
	}

	@Override
	public void setRegularDelta(Unit u) {
		if (this.delta == null) {
			this.regularDelta = (AnnotatedUnit)u;
		}
	}
	@Override
	public void setRegularTotal(Unit u) {
		if (this.total == null) {
			this.regularTotal = (AnnotatedUnit)u;
		}
	}
	
	@Override
	public void setTotal(Range r) {
		AnnotatedRange ar = (AnnotatedRange)r;
		if (ar != null & ar.isRegular()) {
			this.regularTotal = ar.getMin();
			this.total = null;
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
			this.regularDelta = ar.getMin();
			this.delta = null;
		}
		else {
			this.regularDelta = null;
			this.delta = ar;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !getClass().equals(o.getClass())) return false;
		AnnotatedScale as = (AnnotatedScale)o;
		return (this.getRegularDelta() == null ? as.getRegularDelta() == null : this.regularDelta.equals(as.getRegularDelta()))
			&& (this.getRegularTotal() == null ? as.getRegularTotal() == null : this.regularTotal.equals(as.getRegularTotal()))
			&& (this.delta == null ? as.delta == null : this.delta.equals(as.delta))
			&& (this.total == null ? as.total == null : this.total.equals(as.total));
	}

	@Override
	public int hashCode() {
		int hash = 7;
		return hash;
	}
}
