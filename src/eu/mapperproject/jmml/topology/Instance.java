package eu.mapperproject.jmml.topology;

import eu.mapperproject.jmml.Identifiable;
import eu.mapperproject.jmml.definitions.ScaleSet;
import eu.mapperproject.jmml.definitions.Submodel;
import eu.mapperproject.jmml.util.Numbered;

/**
 * An instance of a submodel, with its own id, domain and scales
 * @author Joris Borgdorff
 *
 */
public class Instance implements Identifiable, Domainable, Numbered, Comparable<Instance> {

	private final String id;
	private final Submodel submodel;
	private final Domain domain;
	private boolean initial;
	private boolean isfinal;
	private final ScaleSet scales;
	private final int num;

	public Instance(int num, String id, Submodel submodel, Domain domain,
			boolean initial, ScaleSet scales) {
		this.num = num;
		this.id = id;
		this.submodel = submodel;
		this.domain = domain;
		this.initial = initial;
		this.isfinal = false;
		this.scales = scales;
	}

	/**
	 * @return submodel of which this is an instance
	 */
	public Submodel getSubmodel() {
		return this.submodel;
	}


	@Override
	public String getId() {
		return id;
	}

	@Override
	public int getNumber() {
		return this.num;
	}

	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.topology.Domainable#getDomain()
	 */
	@Override
	public Domain getDomain() {
		return this.domain;
	}

	/** Set this instance to be among the ones initially active programmatically */
	public void setInitial() {
		this.initial = true;
	}
	
	/** Set this instance to be among the final ones to exit programmatically */
	public void setFinal() {
		this.isfinal = true;
	}

	/**
	 * Whether the instance will be among the ones that are initially active
	 */
	public boolean isInitial() {
		return initial;
	}

	/**
	 * Whether the instance will be among the final ones to exit
	 */
	public boolean isFinal() {
		return isfinal;
	}
	
	/**
	 * Whether this instance should be completed after a given number of timesteps
	 * @param steps the number of timesteps so far
	 */
	public boolean isCompleted(int steps) {
		return this.scales.getTimesteps() <= steps + 1;
	}

	public ScaleSet getScales() {
		return scales;
	}

	@Override
	public String toString() {
		String subId = submodel.getId();
		if (this.id.equals(subId)) {
			return this.id;
		}
		else {
			return this.id + "<" + subId + ">";
		}
	}
	
	@Override
	public int hashCode() {
		return this.num;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		return this.num == ((Instance)o).num;
	}
	
	/** Whether all aspects of this instance equal the other, not just the id */
	@Override
	public boolean deepEquals(Object o) {
		if (!this.equals(o)) return false;
		Instance i = (Instance)o;
		return this.submodel.equals(i.submodel) && this.domain.equals(i.domain) && this.initial == i.initial && this.isfinal == i.isfinal;
	}

	@Override
	public int compareTo(Instance t) {
		if (this.num > t.num) return 1;
		else if (this.num < t.num) return -1;
		else return 0;
	}
}
