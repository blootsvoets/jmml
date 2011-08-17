package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Port;
import eu.mapperproject.jmml.specification.Ports;
import eu.mapperproject.jmml.specification.util.DistinguishQName;
import eu.mapperproject.jmml.specification.util.Distinguisher;
import eu.mapperproject.jmml.specification.util.PortValidator;
import eu.mapperproject.jmml.specification.util.UniqueLists;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedPorts extends Ports {

	@SuppressWarnings("unchecked")
	public AnnotatedPorts() {
		Distinguisher dist = new DistinguishQName(new String[] {"in", "out"});
		inOrOut = new UniqueLists(dist, false, new PortValidator());
	}
	
	@Override
	public List<JAXBElement<Port>> getInOrOut() {
        return this.inOrOut;
    }

	public AnnotatedPort getPort(String id) {
		return (AnnotatedPort)((JAXBElement)((UniqueLists)inOrOut).getById(id)).getValue();
	}
	
	public AnnotatedPort getInPort(String id) {
		return (AnnotatedPort)((JAXBElement)((UniqueLists)inOrOut).getById(0, id)).getValue();
	}

	public AnnotatedPort getOutPort(String id) {
		return (AnnotatedPort)((JAXBElement)((UniqueLists)inOrOut).getById(1, id)).getValue();
	}
}
