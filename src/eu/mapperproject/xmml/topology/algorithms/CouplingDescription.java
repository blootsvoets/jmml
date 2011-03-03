package eu.mapperproject.xmml.topology.algorithms;

import java.util.EnumMap;
import java.util.Map;

import eu.mapperproject.xmml.topology.algorithms.Annotation.AnnotationType;
import eu.mapperproject.xmml.topology.algorithms.graph.Category;
import eu.mapperproject.xmml.topology.algorithms.graph.Edge;

public class CouplingDescription extends AbstractDescription implements GraphvizEdge {
	public enum CouplingType {
		// First four are ordered
		OI(true), S(false), FINIT(false, OI), OF(true, S),
		DINIT(false, FINIT), U(false, S), B(false, S), C(false, S), P(false, S);
		
		private boolean send;
		private CouplingType canonical;
		private static CouplingType[] cts = {OI, S};

		CouplingType(boolean send) {
			this.send = send;
			this.canonical = this;
		}

		CouplingType(boolean send, CouplingType canonical) {
			this.send = send;
			this.canonical = canonical;
		}
		
		public static CouplingType getCoupling(int i) {
			return cts[i];
		}
		
		public int getNum() {
			return this.canonical.ordinal();
		}
		
		public boolean canSend(boolean complete) {
			return this.send || complete;
		}
		
		public boolean canReceive(boolean initial) {
			return !this.send || initial;
		}
	}
	
	public enum DomainCoupling {
		SINGLE(true), MULTI(false), SUB(false), SUPER(false);
		
		boolean same;
		DomainCoupling(boolean same) {
			this.same = same;
		}
		
		public boolean matchesDescription(ProcessIteration p1, ProcessIteration p2) {
			if (this.same && p1.getDomain().equals(p2.getDomain())) return true;
			if (!this.same && !p1.getDomain().equals(p2.getDomain())) return true;
			
			return false;
		}
	}
	
	public final static int DYNAMIC_MULTIPLICITY = -1;
	private final ProcessReference from, to;
	private final CouplingType fromType, toType;
	private final DomainCoupling domainCoupling;
	private final Map<AnnotationType,AnnotationType> following;
	private final int multiplicity;
	private String label;
	
	public CouplingDescription(ProcessReference from, CouplingType fromType, ProcessReference to, CouplingType toType, int multiplicity) {
		this(null, from, fromType, to, toType, multiplicity);
	}

	public CouplingDescription(String label, ProcessReference from, CouplingType fromType, ProcessReference to, CouplingType toType, int multiplicity) {
		this(label, from, fromType, to, toType, multiplicity, DomainCoupling.SINGLE);
	}
	
	public CouplingDescription(ProcessReference from, CouplingType fromType, ProcessReference to, CouplingType toType, int multiplicity, DomainCoupling dc) {
		this(null, from, fromType, to, toType, multiplicity, dc);
	}
	
	public CouplingDescription(String label, ProcessReference from, CouplingType fromType, ProcessReference to, CouplingType toType, int multiplicity, DomainCoupling dc) {
		super(from.getName() + "(" + fromType + ") -> " + to.getName() + "(" + toType + ")");
		this.from = from;
		this.fromType = fromType;
		this.domainCoupling = dc;
		this.to = to;
		this.toType = toType;
		this.multiplicity = multiplicity;
		this.following = new EnumMap<AnnotationType, AnnotationType>(AnnotationType.class);
		this.label = label;
	}
	
	public boolean fromMatches(ProcessReference p, CouplingType t) {
		return eqOrNull(this.from, p) && fromMatches(t);
	}

	public boolean fromMatches(CouplingType t) {
		return eqOrNull(this.fromType.canonical, t.canonical);
	}

	public boolean toMatches(ProcessReference p, CouplingType t) {
		return eqOrNull(this.to, p) && toMatches(t);
	}

	public boolean toMatches(CouplingType t) {
		return eqOrNull(this.toType.canonical, t.canonical);
	}
	
	public boolean processMatches(ProcessReference from, ProcessReference to) {
		return this.from.equals(from) && this.to.equals(to);
	}
	
	public Domain getDomain() {
		return Domain.getDomain(this.from, this.to);
	}
	
	public DomainCoupling getDomainCoupling() {
		return this.domainCoupling;
	}
	
	public void addFollowingAnnotation(AnnotationType to) {
		this.addFollowingAnnotation(to, to);
	}

	public void addFollowingAnnotation(AnnotationType to, AnnotationType from) {
		this.following.put(to, from);
	}
	
	public AnnotationType follow(AnnotationType to) {
		return this.following.get(to);
	}

	public boolean hasMultiple() {
		return this.multiplicity > 1;
	}
	
	public boolean removesMultiple() {
		return this.multiplicity < 0;
	}
	
	public int getMultiplicity() {
		return this.multiplicity;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CouplingDescription)) return false;
		CouplingDescription cd = (CouplingDescription)o;
		
		return eqOrNull(this.from, cd.from) && eqOrNull(this.fromType, cd.fromType)
		    && eqOrNull(this.to, cd.to) && eqOrNull(this.toType, cd.toType)
		    && this.multiplicity == cd.multiplicity && this.domainCoupling.equals(cd.domainCoupling); 
	}

	@Override
	public ProcessReference getFrom() {
		return this.from;
	}

	@Override
	public ProcessReference getTo() {
		return this.to;
	}
	
	public CouplingType getToType() {
		return this.toType;
	}

	@Override
	public String getLabel() {
		return this.label;
	}
	
	@Override
	public String getStyle() {
		String ret = "dir=both arrowtail=";
		switch (this.fromType) {
		case OI:
			ret += "dot";
			break;
		case OF:
			ret += "diamond";
			break;
		default:
			ret += "none";
		}
		
		ret += " arrowhead=";
		switch (this.toType) {
		case FINIT:
			ret += "odiamond";
			break;
		case B:
			ret += "onormal";
			break;
		case C:
			ret += "odot";
			break;
		default:
			ret += "vee";
		}
		
		if (multiplicity > 1) {
			ret += " labeldistance=1.3 taillabel=1 headlabel=" + multiplicity;
		}
		else if (multiplicity < 0) {
			ret += " labeldistance=1.3 headlabel=1 taillabel=\" *\"";			
		}
		return ret;
	}

	@Override
	public Category getCatagory() {
		return new Category(this.getDomain());
	}
}
