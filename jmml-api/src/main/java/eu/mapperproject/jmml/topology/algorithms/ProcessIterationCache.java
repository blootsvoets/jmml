/**
 * 
 */
package eu.mapperproject.jmml.topology.algorithms;

import eu.mapperproject.jmml.specification.annotated.AnnotatedInstance;
import java.util.Map;

import eu.mapperproject.jmml.util.PTList;

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

	void putIteration(AnnotatedInstance instance, AnnotationSet key, ProcessIteration value) {
		Map<AnnotationSet, ProcessIteration> instMap = PTList.getMap(instance.getNumber(), processIterations);
		instMap.put(key, value);
	}

	ProcessIteration getIteration(AnnotatedInstance instance, AnnotationSet key) {
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

	void remove(AnnotatedInstance instance, AnnotationSet key) {
		int num = instance.getNumber();
		if (processIterations.size() >= num) {
			Map<AnnotationSet, ProcessIteration> map = processIterations.get(instance.getNumber());
			if (map != null && map.containsKey(key)) map.remove(key);
		}
	}
}
