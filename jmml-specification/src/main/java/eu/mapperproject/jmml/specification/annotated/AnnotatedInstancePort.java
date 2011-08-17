package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.InstancePort;
import eu.mapperproject.jmml.specification.Port;
import eu.mapperproject.jmml.specification.Submodel;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedInstancePort extends InstancePort {
	private transient AnnotatedPort port;
	private transient AnnotatedInstance instance;
	
	public AnnotatedInstancePort() {
		super();
	}
	
	public AnnotatedInstancePort(AnnotatedInstance inst, AnnotatedPort p) {
		this.instance = inst;
		this.port = p;
		this.value = inst.getId() + "." + p.getId();
	}
	
	public AnnotatedPort getPort() {
		return port;
	}

	public AnnotatedInstance getInstance() {
		return instance;
	}
		
	@Override
	public void setValue(String value) {
		super.setValue(value);
		String[] split = value.split("\\.");
		this.instance = ObjectFactoryAnnotated.getModel().getTopology().getInstance(split[0]);
		this.port = this.instance.getPorts().getPort(split[1]);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof InstancePort)) return false;
		String ovalue = ((InstancePort)o).getValue();
		return this.value == null ? ovalue == null : this.value.equals(ovalue);
	}

	@Override
	public int hashCode() {
		return this.value.hashCode();
	}
}
