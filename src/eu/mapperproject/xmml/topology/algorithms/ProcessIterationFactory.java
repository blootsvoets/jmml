package eu.mapperproject.xmml.topology.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.mapperproject.xmml.topology.algorithms.util.PTList;

public class ProcessIterationFactory {
	private List<List<List<Map<LabelStack, Map<ProcessReference,ProcessIteration>>>>> iterMap;
	
	private ProcessIterationFactory() {
		this.iterMap = new ArrayList<List<List<Map<LabelStack, Map<ProcessReference,ProcessIteration>>>>>();
	}
	
	public ProcessIteration getIteration(ProcessReference pd, Annotation<ProcessReference> it, Annotation<ProcessReference> nt, Annotation<ProcessReference> op, LabelStack lb) {
		Map<ProcessReference,ProcessIteration> map = this.getIteration(pd, it.getCounter(), nt.getCounter(), op.getCounter(), lb);
		ProcessIteration pdi = map.get(pd);
		if (pdi == null) {
			pdi = new ProcessIteration(pd, it, nt, op, lb);
			map.put(pd, pdi);
		}
		return pdi;
	}
	
	private Map<ProcessReference,ProcessIteration> getIteration(ProcessReference pd, int i, int n, int o, LabelStack lb) {
		return PTList.getMap(lb, PTList.getMap(o, PTList.get(n, PTList.get(i, iterMap))));
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ProcessIterationFactory)) return false;
		ProcessIterationFactory other = (ProcessIterationFactory)o;
		
		return this.iterMap.equals(other.iterMap);
	}

	
	private static ProcessIterationFactory instance = null;
	static {
		instance = new ProcessIterationFactory();
	}
	
	public static ProcessIterationFactory getInstance() {
		return instance;
	}
}
