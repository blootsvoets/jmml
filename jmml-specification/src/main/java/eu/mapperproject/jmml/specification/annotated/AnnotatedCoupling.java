package eu.mapperproject.jmml.specification.annotated;

import com.sun.istack.logging.Logger;
import eu.mapperproject.jmml.specification.Apply;
import eu.mapperproject.jmml.specification.Coupling;
import eu.mapperproject.jmml.specification.Datatype;
import eu.mapperproject.jmml.specification.Filter;
import eu.mapperproject.jmml.specification.InstancePort;
import eu.mapperproject.jmml.specification.SEL;
import eu.mapperproject.jmml.specification.graph.Edge;
import eu.mapperproject.jmml.specification.numerical.InterpretedFormula;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedCoupling extends Coupling implements Edge<AnnotatedInstancePort> {
	private transient final static Logger logger = Logger.getLogger(Coupling.class);
	private transient boolean validated = false;
	
	@Override
	public void setFrom(InstancePort ip) {
		super.setFrom(ip);
		validated = false;
		this.validateCouplings();
	}
	@Override
	public void setTo(InstancePort ip) {
		super.setTo(ip);
		validated = false;
		this.validateCouplings();
	}
	
	@Override
	public AnnotatedInstancePort getFrom() {
		this.validateCouplings();
		return (AnnotatedInstancePort)from;
	}
	@Override
	public AnnotatedInstancePort getTo() {
		this.validateCouplings();
		return (AnnotatedInstancePort)to;
	}

	
	protected void validateCouplings() {
		if (validated || from == null || to == null) return;
		
		AnnotatedInstancePort afrom = (AnnotatedInstancePort)from;
		AnnotatedInstancePort ato = (AnnotatedInstancePort)to;
		
		AnnotatedPort fromP = afrom.getPort();
		AnnotatedPort toP = ato.getPort();
		
		// Check if datatypes match from one end of the coupling to the other.
		Datatype d = fromP.getDatatypeInstance();
		
		for (Apply a : apply) {
			AnnotatedFilter f = ((AnnotatedApply)a).getFilterInstance();
			Datatype next = f.getDatatypeInInstance();
			if (!d.equals(next)) {
				logger.warning("Datatypes in coupling " + this + " are not converted correctly by applied filter " + f.getId() + ": " + next.getId() + " expected and " + d.getId() + " received.");
			}
			d = f.getDatatypeOutInstance();
		}
		
		if (!d.equals(toP.getDatatypeInstance())) {
			logger.warning("Datatypes in coupling " + this + " are not converted correctly: " + toP.getDatatype() + " expected and " + d.getId() + " received.");
		}
		
		// Check if scales match from one end of the coupling to the other.
		AnnotatedScale ft = afrom.getInstance().getTimescale();
		AnnotatedScale tt = ato.getInstance().getTimescale();
		
		if (ft != null && tt != null) {
			SEL toOp = toP.getOperator();
			SEL fromOp = fromP.getOperator();
			if (ft.isSeparated(tt) || ft.isContiguous(tt)) {
				if (ft.hasGreaterOrEqualMaximumTo(tt)) {
					if (toOp != SEL.FINIT) {
						logger.warning("There is temporal scale separation in coupling  " + this + " but the coupling template used is not call (Oi -> finit) or dispatch (Of -> finit).");
					}
				}
				else {
					if (fromOp != SEL.OF) {
						logger.warning("There is temporal scale separation in coupling  " + this + " but the coupling template used is not release (Of -> S/B) or dispatch (Of -> finit).");
					}					
				}
			}
			else if (ft.isOverlapping(tt) && !((fromOp == SEL.OF && toOp == SEL.FINIT) || (fromOp == SEL.OI && (toOp == SEL.B || toOp == SEL.S)))) {
				logger.warning("There is temporal scale overlap in coupling  " + this + " but the coupling template used is not interact (Oi -> S/B) or dispatch (Of -> finit).");
			}
		}
		validated = true;
	}
	
	private void processFilters() {
		
	}
	@Override
	public String toString() {
		return from.getValue() + " -> " + to.getValue(); 
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		AnnotatedCoupling c = (AnnotatedCoupling)o;
		return this.from.equals(c.from) && this.to.equals(c.to) && (this.name == null ? c.name == null : this.name.equals(c.name));
	}
	
	@Override
	public int hashCode() {
		int hashCode = this.name == null ? 0 : this.name.hashCode();
		hashCode = 31 * hashCode + this.from.hashCode();
		hashCode = 31 * hashCode + this.to.hashCode();
		return hashCode;
	}
		
	/** Returns a copy of this coupling with a different receiving operator. */
	public Coupling copyWithToOperator(SEL op) {
		throw new UnsupportedOperationException("Can not just copy a coupling.");
	//	return new Coupling(this.name, this.from, new InstanceOperator(this.to.getInstance(), op), this.filters);
	}

}
