package eu.mapperproject.xmml.topology;

import java.util.Collection;
import java.util.Map;

import eu.mapperproject.xmml.topology.algorithms.ProcessReference;
import eu.mapperproject.xmml.topology.algorithms.CouplingDescription.CouplingType;

/**
 * Coupling Topology of a multiscale model
 * @author Joris Borgdorff
 */
public class CouplingTopology {

	private Map<String, Instance> instances;
	private Collection<Coupling> couplings;

	/**
	 * @param instances
	 * @param couplings
	 */
	public CouplingTopology(Map<String, Instance> instances,
			Collection<Coupling> couplings) {
		this.instances = instances;
		this.couplings = couplings;
	}
	
	/** Get all couplings in the coupling topology */
	public Collection<Coupling> getCouplings() {
		return this.couplings;
	}

	/** Get all instances defined in the coupling topology */
	public Collection<Instance> getInstances() {
		return this.instances.values();
	}
}
