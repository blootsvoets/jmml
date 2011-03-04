package eu.mapperproject.xmml.topology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.mapperproject.xmml.definitions.Submodel.SEL;

/**
 * Coupling Topology of a multiscale model
 * @author Joris Borgdorff
 */
public class CouplingTopology {

	private final Map<String, Instance> instances;
	private final Map<InstanceOperator,Collection<Coupling>> fromCouplings, toCouplings;
	private final Collection<Coupling> couplings;
	private final static Collection<Coupling> emptyCollection = new ArrayList<Coupling>(0);

	/**
	 * @param instances
	 * @param couplings
	 */
	public CouplingTopology(Map<String, Instance> instances,
			Collection<Coupling> couplings) {
		this.instances = instances;
		this.couplings = couplings;
		this.fromCouplings = new HashMap<InstanceOperator,Collection<Coupling>>();
		this.toCouplings = new HashMap<InstanceOperator,Collection<Coupling>>();
		for (Coupling c : couplings) {
			putCoupling(c, c.getFromOperator(), this.fromCouplings);
			putCoupling(c, new InstanceOperator(c.getFrom(), null), this.fromCouplings);
			putCoupling(c, c.getToOperator(), this.toCouplings);
			putCoupling(c, new InstanceOperator(c.getTo(), null), this.toCouplings);
		}
		
		for (Instance i : this.instances.values()) {
			if (this.getFrom(new InstanceOperator(i, SEL.Of)).isEmpty()) {
				i.setFinal();
			}
			if (this.getTo(new InstanceOperator(i, null)).isEmpty()) {
				i.setInitial();
			}
		}
	}

	/** Put a coupling in a map */
	private void putCoupling(Coupling c, InstanceOperator key, Map<InstanceOperator,Collection<Coupling>> map) {
		Collection<Coupling> cs = map.get(key);
		if (cs == null) {
			cs = new ArrayList<Coupling>();
			map.put(key, cs);
		}
		cs.add(c);
	}
	
	/** Get all couplings in the coupling topology */
	public Collection<Coupling> getCouplings() {
		return this.couplings;
	}

	/** Get all instances defined in the coupling topology */
	public Collection<Instance> getInstances() {
		return this.instances.values();
	}
	
	/** Get all couplings matching an instance operator.
	 * Returns an empty collection if no match was found. If a null operator is given, returns all couplings from a given instance.
	 */
	public Collection<Coupling> getFrom(InstanceOperator io) {
		return getFromMapOrEmpty(io, this.fromCouplings);
	}

	/** Get all couplings matching an instance operator.
	 * Returns an empty collection if no match was found. If a null operator is given, returns all couplings to a given instance.
	 */
	public Collection<Coupling> getTo(InstanceOperator io) {
		return getFromMapOrEmpty(io, this.toCouplings);
	}
	
	private static Collection<Coupling> getFromMapOrEmpty(InstanceOperator io, Map<InstanceOperator,Collection<Coupling>> map) {
		Collection<Coupling> cs = map.get(io);
		if (cs == null) {
			return emptyCollection;
		}
		else {
			return cs;
		}		
	}
}
