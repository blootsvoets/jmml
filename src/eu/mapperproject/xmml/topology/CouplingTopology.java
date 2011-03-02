package eu.mapperproject.xmml.topology;

import java.util.List;
import java.util.Map;

/**
 * Coupling Topology of a multiscale model
 * @author Joris Borgdorff
 */
public class CouplingTopology {

	private Map<String, Instance> instances;
	private List<Coupling> couplings;

	/**
	 * @param instances
	 * @param couplings
	 */
	public CouplingTopology(Map<String, Instance> instances,
			List<Coupling> couplings) {
		this.instances = instances;
		this.couplings = couplings;
	}

}
