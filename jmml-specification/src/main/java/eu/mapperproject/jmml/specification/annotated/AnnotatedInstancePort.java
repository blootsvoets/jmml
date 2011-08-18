package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.InstancePort;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedInstancePort extends InstancePort {
	private transient AnnotatedPort port;
	private transient AnnotatedInstance instance;
	private final static Logger logger = Logger.getLogger(InstancePort.class.getName());
	
	public AnnotatedInstancePort() {
		super();
	}
	
	public AnnotatedInstancePort(AnnotatedInstance inst, AnnotatedPort p) {
		this.instance = inst;
		this.port = p;
		this.value = inst.getId() + "." + p.getId();
	}
	
	public AnnotatedPort getPort() {
		if (port == null) setValue(value);
		return port;
	}

	public AnnotatedInstance getInstance() {
		if (instance == null) setValue(value);
		return instance;
	}
		
	@Override
	public void setValue(String value) {
		super.setValue(value);
		String[] split = value.split("\\.");
		this.instance = ObjectFactoryAnnotated.getModel().getTopology().getInstance(split[0]);
		if (this.instance == null) {
			throw new IllegalArgumentException("Instance.Port " + value + " in coupling does not reference an existing instance.");
		}
		else {
			this.port = this.instance.getPorts().getPort(split[1]);
		}
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
