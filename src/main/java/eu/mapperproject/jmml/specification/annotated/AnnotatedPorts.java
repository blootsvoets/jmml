/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Port;
import eu.mapperproject.jmml.specification.Ports;
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
            inOrOut = new UniquePortList();
        }
        return this.inOrOut;
    }
	
	public Port getInPort(String id) {
		return ((UniquePortList)inOrOut).getInPort(id);
	}

	public Port getOutPort(String id) {
		return ((UniquePortList)inOrOut).getOutPort(id);
	}
	
	private class UniquePortList extends AbstractList<JAXBElement<Port>> {
		private List<JAXBElement<Port>> in = new ArrayList<JAXBElement<Port>>();
		private List<JAXBElement<Port>> out = new ArrayList<JAXBElement<Port>>();
		
		@Override
		public boolean add(JAXBElement<Port> port) {
			List<JAXBElement<Port>> ref = port.getName().getLocalPart().equals("in") ? in : out;

			JAXBElement<Port> jp = getPort(port.getValue().getId(), ref);
			if (jp != null) {
				throw new IllegalArgumentException("May not add two ports with the same name.");
			}
			
			return ref.add(port);
		}
		
		@Override
		public JAXBElement<Port> get(int i) {
			if (i < in.size()) {
				return in.get(i);
			}
			else {
				return out.get(i);
			}
		}
		
		public Port getInPort(String id) {
			JAXBElement<Port> port = getPort(id, in);
			return port == null ? null : port.getValue();
		}
		
		public Port getOutPort(String id) {
			JAXBElement<Port> port = getPort(id, out);
			return port == null ? null : port.getValue();
		}
		
		private JAXBElement<Port> getPort(String id, List<JAXBElement<Port>> ref) {
			for (JAXBElement<Port> jp : out) {
				if (jp != null && jp.getValue().getId().equals(id)) {
					return jp;
				}
			}
			return null;
		}

		@Override
		public int size() {
			return in.size() + out.size();
		}
	}
}
