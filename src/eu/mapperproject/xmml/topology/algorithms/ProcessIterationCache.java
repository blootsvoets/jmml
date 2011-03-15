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
	private Map<String, Map<AnnotationSet, ProcessIteration>> processIterations;

	public ProcessIterationCache() {
		processIterations = new HashMap<String, Map<AnnotationSet, ProcessIteration>>();
	}

	ProcessIteration getIteration(Instance instance, AnnotationSet key) {
		
		Map<AnnotationSet, ProcessIteration> instMap = PTList.getMap(instance.getId(), processIterations);

		ProcessIteration pi = instMap.get(key);
		if (pi == null) {
			pi = new ProcessIteration(instance, key);
			instMap.put(key, pi);
		}
		else {
			pi.merge(key);
		}

		return pi;
	}
}
