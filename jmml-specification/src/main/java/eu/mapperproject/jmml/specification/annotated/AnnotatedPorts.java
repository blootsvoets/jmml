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
		JAXBElement res = (JAXBElement)((UniqueLists)inOrOut).getById(id);
		if (res == null) {
			throw new IllegalArgumentException("Port with id " + id + " does not exist.");
		}
		return (AnnotatedPort)res.getValue();
	}
	
	public AnnotatedPort getInPort(String id) {
		JAXBElement res = (JAXBElement)((UniqueLists)inOrOut).getById(0, id);
		if (res == null) {
			throw new IllegalArgumentException("In port with id " + id + " does not exist.");
		}
		return (AnnotatedPort)res.getValue();
	}

	public AnnotatedPort getOutPort(String id) {
		JAXBElement res = (JAXBElement)((UniqueLists)inOrOut).getById(1, id);
		if (res == null) {
			throw new IllegalArgumentException("Out port with id " + id + " does not exist.");
		}
		return (AnnotatedPort)res.getValue();
	}
	
	public boolean hasInPort() {
		return ((UniqueLists)inOrOut).hasType(0);
	}
	
	public boolean hasOutPort() {
		return ((UniqueLists)inOrOut).hasType(1);
	}
}
