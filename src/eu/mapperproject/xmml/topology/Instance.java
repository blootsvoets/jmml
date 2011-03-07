package eu.mapperproject.xmml.topology;

import eu.mapperproject.xmml.Identifiable;
import eu.mapperproject.xmml.definitions.ScaleMap;
import eu.mapperproject.xmml.definitions.Submodel;

/**
 * An instance of a submodel, with its own id, domain and scales
 * @author Joris Borgdorff
 *
 */
public class Instance implements Identifiable, Domainable {

	private final String id;
	private final Submodel submodel;
	private final Domain domain;
	private boolean initial;
	private boolean isfinal;
	private final ScaleMap scales;

	public Instance(String id, Submodel submodel, Domain domain,
			boolean initial, ScaleMap scales) {
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

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
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
		return this.id.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !this.getClass().equals(o.getClass())) return false;
		return this.id.equals(((Instance)o).id);
	}
	
	/** Whether all aspects of this instance equal the other, not just the id */
	public boolean deepEquals(Object o) {
		if (!this.equals(o)) return false;
		Instance i = (Instance)o;
		return this.submodel.equals(i.submodel) && this.domain.equals(i.domain) && this.initial == i.initial && this.isfinal == i.isfinal;
	}
}
