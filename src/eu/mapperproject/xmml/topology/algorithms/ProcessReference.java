package eu.mapperproject.xmml.topology.algorithms;

import eu.mapperproject.xmml.topology.algorithms.graph.Category;
import eu.mapperproject.xmml.topology.algorithms.graph.Node;

/** Refers to a process of a single domain */
public class ProcessReference extends AbstractInstance<ProcessDescription> implements Description, GraphvizNode {
	private final String name;
	private final boolean micro;
	
	public ProcessReference(ProcessDescription pd, String name, Domain dom, boolean micro) {
		super(pd, dom);
		this.name = name;
		this.micro = micro;
	}
	
	public boolean isCompleted(int iteration) {
		return this.desc.isCompleted(iteration);
	}
	
	public boolean multistep() {
		return this.desc.multistep();
	}
	
	public boolean isMicromodel() {
		return this.micro;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!super.equals(o)) return false;
		ProcessReference pr = (ProcessReference)o;
		return this.name.equals(pr.name);
	}
	
	@Override
	public String getName() {
		String ret = this.name;
		if (!this.desc.getName().equals(this.name))
			ret += "<" + this.desc.getName() + ">";
		return ret;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public String getStyle() {
		return "shape=rectangle";
	}

	@Override
	public Category getCatagory() {
		return new Category(this.getDomain());
	}	
}
