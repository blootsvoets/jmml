package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Instance;
import eu.mapperproject.jmml.specification.Topology;
import eu.mapperproject.jmml.specification.util.DistinguishClass;
import eu.mapperproject.jmml.specification.util.UniqueLists;
import java.util.List;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedTopology extends Topology {
	@SuppressWarnings("unchecked")
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
	
		/** Put the from and to couplings in the right place */
//	private void initialize() {
//		for (Coupling c : this.topology.getEdges()) {
//			putCoupling(c, c.getFromOperator(), this.fromCouplings);
//			putCoupling(c, new InstanceOperator(c.getFrom(), null), this.fromCouplings);
//			putCoupling(c, c.getToOperator(), this.toCouplings);
//			putCoupling(c, new InstanceOperator(c.getTo(), null), this.toCouplings);
//		}
//
//		for (Instance i : this.topology.getNodes()) {
//			if (this.getFrom(new InstanceOperator(i, SEL.Of)).isEmpty()) {
//				i.setFinal();
//			}
//
//			Collection<Coupling> allTo = this.getTo(new InstanceOperator(i, null));
//			if (allTo.isEmpty()) {
//				i.setInitial();
//				initialInstances.add(i);
//			}
//			else if (i.isInitial()) {
//				initialInstances.add(i);
//			}
//			else if (this.getTo(new InstanceOperator(i, SEL.finit)).isEmpty()) {
//				Collection<Coupling> newC = new ArrayList<Coupling>(allTo.size());
//				for (Coupling c : allTo) {
//					newC.add(c.copyWithToOperator(SEL.finit));
//				}
//				needInitInstances.put(i, newC);
//			}
//		}
//	}
//
//	/** Put a coupling in a map */
//	private void putCoupling(Coupling c, InstanceOperator key, Map<InstanceOperator,Collection<Coupling>> map) {
//		Collection<Coupling> cs = map.get(key);
//		if (cs == null) {
//			cs = new ArrayList<Coupling>();
//			map.put(key, cs);
//		}
//		cs.add(c);
//	}
}
