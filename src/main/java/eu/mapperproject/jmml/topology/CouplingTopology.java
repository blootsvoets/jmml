package eu.mapperproject.jmml.topology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.mapperproject.jmml.definitions.Submodel.SEL;
import eu.mapperproject.jmml.util.graph.PTGraph;

/**
 * Coupling Topology of a multiscale model
 * @author Joris Borgdorff
 */
public class CouplingTopology {
	private final PTGraph<Instance,Coupling> topology;
	private final Collection<Instance> initialInstances;
	private final Map<InstanceOperator,Collection<Coupling>> fromCouplings, toCouplings;
	private final Map<Instance,Collection<Coupling>> needInitInstances;
	private final static Collection<Coupling> emptyCollection = new ArrayList<Coupling>(0);

	/**
	 * @param instances
	 * @param couplings
	 */
	public CouplingTopology(Map<String, Instance> instances,
			Collection<Coupling> couplings) {
		this.topology = new PTGraph<Instance,Coupling>(true, instances.values(), couplings);
		this.fromCouplings = new HashMap<InstanceOperator,Collection<Coupling>>();
		this.toCouplings = new HashMap<InstanceOperator,Collection<Coupling>>();
		this.initialInstances = new ArrayList<Instance>();
		this.needInitInstances = new HashMap<Instance,Collection<Coupling>>();
		this.initialize();
	}

	/** Put the from and to couplings in the right place */
	private void initialize() {
		for (Coupling c : this.topology.getEdges()) {
			putCoupling(c, c.getFromOperator(), this.fromCouplings);
			putCoupling(c, new InstanceOperator(c.getFrom(), null), this.fromCouplings);
			putCoupling(c, c.getToOperator(), this.toCouplings);
			putCoupling(c, new InstanceOperator(c.getTo(), null), this.toCouplings);
		}

		for (Instance i : this.topology.getNodes()) {
			if (this.getFrom(new InstanceOperator(i, SEL.Of)).isEmpty()) {
				i.setFinal();
			}

			Collection<Coupling> allTo = this.getTo(new InstanceOperator(i, null));
			if (allTo.isEmpty()) {
				i.setInitial();
				initialInstances.add(i);
			}
			else if (i.isInitial()) {
				initialInstances.add(i);
			}
			else if (this.getTo(new InstanceOperator(i, SEL.finit)).isEmpty()) {
				Collection<Coupling> newC = new ArrayList<Coupling>(allTo.size());
				for (Coupling c : allTo) {
					newC.add(c.copyWithToOperator(SEL.finit));
				}
				needInitInstances.put(i, newC);
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

	/** Return this topology as a graph */
	public PTGraph<Instance,Coupling> getGraph() {
		return this.topology;
	}
	
	/** Get all couplings in the coupling topology */
	public Collection<Coupling> getCouplings() {
		return this.topology.getEdges();
	}

	/** Get all instances defined in the coupling topology */
	public Collection<Instance> getInstances() {
		return this.topology.getNodes();
	}

	/** Get number of instances */
	public int getInstanceCount() {
		return this.topology.nodeCount();
	}
	
	/** Get all instances that are initially active */
	public Collection<Instance> getInitialInstances() {
		return this.initialInstances;
	}
	
	/** Whether given instance is not able to initialize by itself */
	public boolean needsInitInstances(Instance i) {
		return this.needInitInstances.containsKey(i);
	}

	/** Whether given instance is not able to initialize by itself */
	public Collection<Coupling> needsInitCouplings(Instance i) {
		return this.needInitInstances.get(i);
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
