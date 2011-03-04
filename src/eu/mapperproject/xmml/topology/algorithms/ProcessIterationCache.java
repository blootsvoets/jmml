/**
 * 
 */
package eu.mapperproject.xmml.topology.algorithms;

import java.util.HashMap;
import java.util.Map;

import eu.mapperproject.xmml.topology.Instance;
import eu.mapperproject.xmml.util.PTList;

/**
 * Stores processes while computing, so each ProcessIteration can be uniquely
 * identified
 * 
 * @author Joris Borgdorff
 */
public class ProcessIterationCache {
	private Map<String, Map<int[], ProcessIteration>> processIterations;

	public ProcessIterationCache() {
		processIterations = new HashMap<String, Map<int[], ProcessIteration>>();
	}

	public ProcessIteration getIteration(Instance instance,
			Annotation<Instance> iter, Annotation<Instance> inst,
			Annotation<Instance> oper) {
		
		Map<int[], ProcessIteration> instMap = PTList.getMap(instance.getId(), processIterations);
		int[] key = { iter.getCounter(), inst.getCounter(), oper.getCounter() };

		ProcessIteration pi = instMap.get(key);
		if (pi == null) {
			pi = new ProcessIteration(instance, iter, inst, oper);
			instMap.put(key, pi);
		}

		return pi;
	}
}
