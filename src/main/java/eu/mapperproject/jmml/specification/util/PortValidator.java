package eu.mapperproject.jmml.specification.util;

import eu.mapperproject.jmml.specification.Port;
import eu.mapperproject.jmml.specification.SEL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;

/**
 *
 * @author Joris Borgdorff
 */
public class PortValidator implements Validator<JAXBElement<Port>> {
	private final static Logger logger = Logger.getLogger(PortValidator.class.getName());

	@Override
	public boolean isValid(JAXBElement<Port> element) {
		SEL op = element.getValue().getOperator();
		if (op != null) {
			if (element.getName().getLocalPart().equals("out") && op != SEL.OF && op != SEL.OI) {
				logger.log(Level.SEVERE, "Outgoing port {} should have operator Of or Oi, ignoring.", element.getValue().getId());
				return false;
			}
			if (element.getName().getLocalPart().equals("in") && (op == SEL.OF || op == SEL.OI)) {
				logger.log(Level.SEVERE, "Incoming port {} should not have operator Of or Oi, ignoring.", element.getValue().getId());
				return false;
			}
		}
		return true;
	}
	
}
