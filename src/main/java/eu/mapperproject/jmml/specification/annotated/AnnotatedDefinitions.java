/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Definitions;
import java.util.AbstractList;
import javax.xml.bind.JAXBElement;

/**
 *
 * @author jborgdo1
 */
public class AnnotatedDefinitions extends Definitions {
	
	private class UniqueElementList extends AbstractList<JAXBElement<?>> {
		private List<JAXBElement<?>> filters = new ArrayList<JAXBElement<?>>();
		private List<JAXBElement<?>> mappers = new ArrayList<JAXBElement<?>>();
		private List<JAXBElement<?>> submodels = new ArrayList<JAXBElement<?>>();
		
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
