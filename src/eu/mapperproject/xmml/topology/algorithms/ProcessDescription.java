package eu.mapperproject.xmml.topology.algorithms;

import eu.mapperproject.xmml.topology.algorithms.graph.Category;
import eu.mapperproject.xmml.topology.algorithms.graph.Node;

public class ProcessDescription extends AbstractDescription implements GraphvizNode {
	private final int steps;
	private final boolean stateful;
	
	public ProcessDescription(String name) {
		this(name, 1, false);
	}
	
	public ProcessDescription(String name, int steps, boolean stateful) {
		super(name);
		this.steps = steps;
		this.stateful = stateful;
	}
	
	public ProcessReference getReference(Domain domain, boolean micro) {
		return this.getReference(domain, this.getName(), micro);
	}
	
	public ProcessReference getReference(Domain domain, String name, boolean micro) {
		return new ProcessReference(this, name, domain, micro);
	}
	
	public boolean isCompleted(int currentSteps) {
		return currentSteps >= steps - 1;
	}
	
	public boolean multistep() {
		return steps > 1;
	}
	
	public int getSteps() {
		return this.steps;
	}
	
	public boolean stateful() {
		return this.stateful;
	}
	
	@Override
	public String getStyle() {
		return null;
	}
	
	@Override
	public Category getCatagory() {
		return Category.NO_CLUSTER;
	}
}
