package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.*;
import eu.mapperproject.jmml.util.graph.Edge;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validates the couplings on timescales and datatypes
 * @author Joris Borgdorff
 */
public class AnnotatedCoupling extends Coupling implements Edge<AnnotatedInstancePort> {
	private transient final static Logger logger = Logger.getLogger(Coupling.class.getName());
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
		if (!validated) this.validateCouplings();
		return (AnnotatedInstancePort)from;
	}
	@Override
	public AnnotatedInstancePort getTo() {
		if (!validated) this.validateCouplings();
		return (AnnotatedInstancePort)to;
	}

	
	public void validateCouplings() {
		if (from == null || to == null) return;
		
		AnnotatedInstancePort afrom = (AnnotatedInstancePort)from;
		AnnotatedInstancePort ato = (AnnotatedInstancePort)to;
		
		AnnotatedPort fromP = afrom.getPort();
		AnnotatedPort toP = ato.getPort();
		
		// Check if datatypes match from one end of the coupling to the other.
		Datatype d = fromP.getDatatypeInstance();

		if (d != null) {
            for (Apply a : this.getApply()) {
                AnnotatedFilter f = ((AnnotatedApply) a).getFilterInstance();
                Datatype next = f.getDatatypeInInstance();
                // An empty datatype is assumed to match
                if (next != null && !d.equals(next)) {
                    logger.log(Level.WARNING, "Datatypes in coupling {0} are not converted correctly by applied filter {1}: {2} expected and {3} received.", new Object[]{this, f.getId(), next.getId(), d.getId()});
                }
                next = f.getDatatypeOutInstance();
                // An empty datatype is assumed to mean ''unchanged''.
                if (next != null) {
                    d = next;
                }
            }

            if (!d.equals(toP.getDatatypeInstance())) {
                logger.log(Level.WARNING, "Datatypes in coupling {0} are not converted correctly: {1} expected and {2} received.", new Object[]{this, toP.getDatatype(), d.getId()});
            }
        }
		
		// Check if scales match from one end of the coupling to the other.
		AnnotatedScale ft = afrom.getInstance().getTimescaleInstance();
		AnnotatedScale tt = ato.getInstance().getTimescaleInstance();
		
		if (ft != null && tt != null) {
			SEL toOp = toP.getOperator();
			SEL fromOp = fromP.getOperator();
			if (ft.isSeparated(tt) || ft.isContiguous(tt)) {
				if (ft.hasGreaterOrEqualMaximumTo(tt)) {
					if (toOp != SEL.FINIT) {
						logger.log(Level.WARNING, "There is temporal scale separation in coupling {0} but the coupling template used is not call (Oi -> finit) or dispatch (Of -> finit).", this);
					}
				}
				else {
					if (fromOp != SEL.OF) {
						logger.log(Level.WARNING, "There is temporal scale separation in coupling {0} but the coupling template used is not release (Of -> S/B) or dispatch (Of -> finit).", this);
					}					
				}
			}
			else if (ft.isOverlapping(tt) && !((fromOp == SEL.OF && toOp == SEL.FINIT) || (fromOp == SEL.OI && (toOp == SEL.B || toOp == SEL.S)))) {
				logger.log(Level.WARNING, "There is temporal scale overlap in coupling {0} but the coupling template used is not interact (Oi -> S/B) or dispatch (Of -> finit).", this);
			}
		}
		validated = true;
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
