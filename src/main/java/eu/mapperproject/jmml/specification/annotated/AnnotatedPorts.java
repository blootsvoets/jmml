/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Port;
import eu.mapperproject.jmml.specification.Ports;
import eu.mapperproject.jmml.specification.util.UniqueLists;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;

/**
 *
 * @author jborgdo1
 */
public class AnnotatedPorts extends Ports {

	@Override
	public List<JAXBElement<Port>> getInOrOut() {
        if (inOrOut == null) {
            inOrOut = new UniqueLists(new String[] {"in", "out"});
        }
        return this.inOrOut;
    }
	
	public Port getInPort(String id) {
		return ((UniqueLists<Port>)inOrOut).getById(0, id);
	}

	public Port getOutPort(String id) {
		return ((UniqueLists<Port>)inOrOut).getById(1, id);
	}
}
