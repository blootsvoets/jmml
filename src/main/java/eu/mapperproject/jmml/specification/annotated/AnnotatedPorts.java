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
	
	@SuppressWarnings("unchecked")
	public AnnotatedPort getPort(String id) {
		return ((JAXBElement<AnnotatedPort>)((UniqueLists)inOrOut).getById(id)).getValue();
	}
	
	@SuppressWarnings("unchecked")
	public AnnotatedPort getInPort(String id) {
		return ((JAXBElement<AnnotatedPort>)((UniqueLists)inOrOut).getById(0, id)).getValue();
	}

	@SuppressWarnings("unchecked")
	public AnnotatedPort getOutPort(String id) {
		return ((JAXBElement<AnnotatedPort>)((UniqueLists)inOrOut).getById(1, id)).getValue();
	}
}
