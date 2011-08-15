package eu.mapperproject.jmml.specification.annotated;

import com.sun.istack.logging.Logger;
import eu.mapperproject.jmml.specification.Apply;
import eu.mapperproject.jmml.specification.Coupling;
import eu.mapperproject.jmml.specification.Datatype;
import eu.mapperproject.jmml.specification.Filter;
import eu.mapperproject.jmml.specification.InstancePort;
import eu.mapperproject.jmml.specification.SEL;
import eu.mapperproject.jmml.specification.numerical.InterpretedFormula;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedCoupling extends Coupling {
	private final static Logger logger = Logger.getLogger(Coupling.class);
	
	@Override
	public void setFrom(InstancePort ip) {
		super.setFrom(ip);
		this.validateCouplings();
	}
	
	protected void validateCouplings() {
		if (from == null || to == null) return;
		
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
				logger.warning("Datatypes in coupling from " + from.getValue() + " to " + to.getValue() + " are not converted correctly by applied filter " + f.getId() + ": " + next.getId() + " expected and " + d.getId() + " received.");
			}
			d = f.getDatatypeOutInstance();
		}
		
		if (!d.equals(toP.getDatatypeInstance())) {
			logger.warning("Datatypes in coupling from " + from.getValue() + " to " + to.getValue() + " are not converted correctly: " + toP.getDatatype() + " expected and " + d.getId() + " received.");
		}
		
		// Check if scales match from one end of the coupling to the other.
		AnnotatedScale dt = afrom.getInstance().getTimescale();
		
		if (dt != null) {
			for (Apply a : apply) {
				AnnotatedApply aa = (AnnotatedApply)a;
				AnnotatedFilter f = aa.getFilterInstance();
				if (f.getDimension().equals("time") || f.getDimension().equals("temporal") || f.getDimension().equals("t")) {
					if (f.getType() == Filter.Type.INTERPOLATION) {
						InterpretedFormula formula = aa.getFactor().interpret();
					}
				}
				Datatype next = f.getDatatypeInInstance();
				if (!d.equals(next)) {
					logger.warning("Datatypes in coupling from " + from.getValue() + " to " + to.getValue() + " are not converted correctly by applied filter " + f.getId() + ": " + next.getId() + " expected and " + d.getId() + " received.");
				}
				d = f.getDatatypeOutInstance();
			}			
		}
	}
	
	private void processFilters() {
		
	}
}
