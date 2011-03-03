package eu.mapperproject.xmml.topology.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.mapperproject.xmml.topology.algorithms.CouplingDescription.CouplingType;
import eu.mapperproject.xmml.topology.algorithms.util.PTList;

/** Describes the model, with each of its processes and couplings */
public class ModelDescription extends AbstractDescription {
	private List<CouplingDescription> cds;
	private Map<ProcessReference,List<CouplingDescription>> pdfrom;
	private Map<ProcessReference,List<CouplingDescription>> pdto;
	private List<ProcessReference> pds;

	public ModelDescription(String name, List<ProcessReference> processes, List<CouplingDescription> couplings) {
		super(name);
		this.pds = processes;
		this.cds = couplings;
		this.initMap();
	}
	
	@SuppressWarnings("unchecked")
	private void initMap() {
		this.pdfrom = new HashMap<ProcessReference,List<CouplingDescription>>();
		this.pdto = new HashMap<ProcessReference,List<CouplingDescription>>();
		
		for (CouplingDescription cd : this.cds) {
			PTList.add(cd.getFrom(), cd, this.pdfrom);
			PTList.add(cd.getTo(), cd, this.pdto);
		}
		
		for (ProcessReference p : this.pds) {
			if (!this.pdfrom.containsKey(p)) this.pdfrom.put(p, PTList.EMPTY);
			if (!this.pdto.containsKey(p)) this.pdto.put(p, PTList.EMPTY);
		}
	}
	
	
	public List<CouplingDescription> getCouplings() {
		return this.cds;
	}
	
	public List<CouplingDescription> getToCouplings(ProcessReference p) {
		return this.pdto.get(p);
	}
	
	public List<CouplingDescription> getFromCouplings(ProcessReference p) {
		return this.pdfrom.get(p);		
	}

	public List<ProcessReference> getProcesses() {
		return this.pds;
	}
	
	public List<CouplingDescription> fromCouplingsMatching(ProcessReference p, CouplingType ct) {
		List<CouplingDescription> col = new ArrayList<CouplingDescription>();
		
		for (CouplingDescription cd : getFromCouplings(p)) {
			if (cd.fromMatches(p, ct)) col.add(cd);
		}
		
		return col;
	}
	
	public boolean hasToCouplingMatching(ProcessReference p, CouplingType ct) {		
		for (CouplingDescription cd : getToCouplings(p)) {
			if (cd.toMatches(p, ct)) return true;
		}
		
		return false;
	}

	public List<CouplingDescription> toCouplingMatching(ProcessReference p, CouplingType ct) {		
		List<CouplingDescription> col = new ArrayList<CouplingDescription>();
		for (CouplingDescription cd : getToCouplings(p)) {
			if (cd.toMatches(p, ct)) col.add(cd);
		}
		
		return col;
	}
	
	public boolean isInitial(ProcessReference p) {
		return this.pdto.get(p).isEmpty();
	}
	
	public boolean isFinal(ProcessReference p) {
		return this.fromCouplingsMatching(p, CouplingType.OF).isEmpty();
	}
	
	public boolean initInIteration(ProcessReference p) {
		return !this.hasToCouplingMatching(p, CouplingType.FINIT) && !this.isInitial(p);
	}
}
