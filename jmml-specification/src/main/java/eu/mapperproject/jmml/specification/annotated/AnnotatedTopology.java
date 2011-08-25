package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Coupling;
import eu.mapperproject.jmml.specification.Instance;
import eu.mapperproject.jmml.specification.SEL;
import eu.mapperproject.jmml.specification.Topology;
import eu.mapperproject.jmml.specification.YesNoChoice;
import eu.mapperproject.jmml.specification.util.DistinguishClass;
import eu.mapperproject.jmml.specification.util.FastArrayList;
import eu.mapperproject.jmml.specification.util.UniqueLists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedTopology extends Topology {
	private transient List<AnnotatedCoupling> tmpList;
	
	@SuppressWarnings("unchecked")
	public AnnotatedTopology() {
		super();
		this.instance = new UniqueLists(new DistinguishClass(new Class[]{Instance.class}));
		this.tmpList = new ArrayList<AnnotatedCoupling>(10);
	}
	
	@Override
	public List<Instance> getInstance() {
        return this.instance;
    }
	
	public AnnotatedInstance getInstance(String id) {
		return (AnnotatedInstance)((UniqueLists)this.instance).getById(0, id);
	}
	
	public List<AnnotatedInstance> getInitialInstances() {
		List<AnnotatedInstance> insts = new FastArrayList<AnnotatedInstance>();
		for (Instance inst : instance) {
			AnnotatedInstance ainst = (AnnotatedInstance)inst;
			if (ainst.isInit()) {
				insts.add(ainst);
			}
			else if (!hasIncomingCouplings(ainst)) {
				insts.add(ainst);
			}
		}
		return insts;
	}
	
	public boolean needsExternalInitialization(AnnotatedInstance ainst) {
		if (ainst.isInit()) {
			return false;
		}
		boolean hasIncoming = false;
		for (Coupling c : this.coupling) {
			AnnotatedInstancePort ato = (AnnotatedInstancePort) c.getTo();
			if (ato.getInstance().equals(ainst)) {
				if (ato.getPort().getOperator() == SEL.FINIT) return false;
				else hasIncoming = true;
			}
		}
		return hasIncoming;
	}
	
	public List<AnnotatedCoupling> externalInitializationCouplings(AnnotatedInstance ainst) {
		if (ainst.isInit()) {
			return null;
		}
		tmpList.clear();
		for (Coupling c : this.coupling) {
			AnnotatedInstancePort ato = (AnnotatedInstancePort) c.getTo();
			if (ato.getInstance().equals(ainst)) {
				if (ato.getPort().getOperator() == SEL.FINIT) return null;
				tmpList.add((AnnotatedCoupling)c);
			}
		}
		return tmpList.isEmpty() ? null : tmpList;
	}


	private boolean hasIncomingCouplings(AnnotatedInstance ainst) {
		for (Coupling c : this.coupling) {
			if (((AnnotatedInstancePort)c.getTo()).getInstance().equals(ainst)) {
				return true;
			}
		}
		return false;
	}
	
	public List<AnnotatedCoupling> getCouplingsTo(AnnotatedInstance ainst, SEL operator) {
		tmpList.clear();
		for (Coupling c : this.coupling) {
			AnnotatedCoupling ac = (AnnotatedCoupling) c;
			if (ac.getTo().getInstance().equals(ainst)) {
				if (ac.getTo().getPort().getOperator() == operator) {
					tmpList.add(ac);
				}
			}
		}
		return tmpList;
	}
	
	public List<AnnotatedCoupling> getCouplingsFrom(AnnotatedInstance ainst, SEL operator) {
		tmpList.clear();
		for (Coupling c : this.coupling) {
			AnnotatedCoupling ac = (AnnotatedCoupling) c;
			if (ac.getFrom().getInstance().equals(ainst)) {
				if (ac.getFrom().getPort().getOperator() == operator) {
					tmpList.add(ac);
				}
			}
		}
		return tmpList;
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
