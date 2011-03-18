/**
 * 
 */
package eu.mapperproject.xmml.topology.algorithms;

import java.util.HashMap;
import java.util.Map;

import eu.mapperproject.xmml.topology.Instance;
import eu.mapperproject.xmml.util.PTList;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores processes while computing, so each ProcessIteration can be uniquely
 * identified
 * 
 * @author Joris Borgdorff
 */
public class ProcessIterationCache {
	private List<Map<AnnotationSet, ProcessIteration>> processIterations;

	public ProcessIterationCache() {
		processIterations = new ArrayList<Map<AnnotationSet, ProcessIteration>>();
	}

	void putIteration(Instance instance, AnnotationSet key, ProcessIteration value) {
		Map<AnnotationSet, ProcessIteration> instMap = PTList.getMap(instance.getNumber(), processIterations);
		instMap.put(key, value);
	}

	ProcessIteration getIteration(Instance instance, AnnotationSet key) {
		Map<AnnotationSet, ProcessIteration> instMap = PTList.getMap(instance.getNumber(), processIterations);

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

	void remove(Instance instance, AnnotationSet key) {
		processIterations.get(instance.getNumber()).remove(key);
	}
}
