package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Coupling;
import eu.mapperproject.jmml.specification.InstancePort;
import eu.mapperproject.jmml.specification.SEL;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedCoupling extends Coupling {
	@Override
	public void setFrom(InstancePort ip) {
		super.setFrom(ip);
		this.validateCouplings();
	}
	
	protected void validateCouplings() {
		if (from == null || to == null) return;
		
		AnnotatedInstancePort afrom = (AnnotatedInstancePort)from;
		AnnotatedInstancePort ato = (AnnotatedInstancePort)to;
		
		SEL fromOp = afrom.getPort().getOperator();
		SEL toOp = ato.getPort().getOperator();
		if (fromOp != SEL.OI && fromOp != SEL.OF) {
			throw new IllegalStateException("The from port of a coupling must be of a sending SEL operator.");
		}
	}
}
