/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Instance;
import eu.mapperproject.jmml.specification.Topology;
import eu.mapperproject.jmml.specification.util.DistinguishClass;
import eu.mapperproject.jmml.specification.util.UniqueLists;
import java.util.List;

/**
 *
 * @author jborgdo1
 */
public class AnnotatedTopology extends Topology {
	public AnnotatedTopology() {
		this.instance = new UniqueLists(new DistinguishClass(new Class[]{Instance.class}));
	}
	
	@Override
	public List<Instance> getInstance() {
        return this.instance;
    }
	
	public AnnotatedInstance getInstance(String id) {
		return (AnnotatedInstance)((UniqueLists)this.instance).getById(0, id);
	}
}
