package eu.mapperproject.jmml.specification.util;

import eu.mapperproject.jmml.util.Distinguisher;
import eu.mapperproject.jmml.util.Identifiable;
import javax.xml.bind.JAXBElement;

/**
 *
 * @author Joris Borgdorff
 */
public class DistinguishQName extends Distinguisher<String, JAXBElement<? extends Identifiable>> {
	public DistinguishQName(String[] names) {
		super(names);
	}

	@Override
	protected String extractType(JAXBElement<? extends Identifiable> element) {
		return element.getName().getLocalPart();
	}
	@Override
	protected boolean belongsToPartition(int p, String name) {
		return partition[p].equals(name);
	}
	@Override
	public String getId(JAXBElement<? extends Identifiable> element) {
		return element.getValue().getId();
	}
}
