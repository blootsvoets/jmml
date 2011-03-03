package eu.mapperproject.xmml.definitions;

import eu.mapperproject.xmml.Identifiable;
import eu.mapperproject.xmml.util.Formula;
import eu.mapperproject.xmml.util.SIUnit;
/**
 * An xMML datatype, possibly representing a real data object
 * @author Joris Borgdorff
 *
 */
public class Datatype implements Identifiable {
	private final String id;
	private final String name;
	private final Formula size_formula;
	private final SIUnit size;

	public Datatype(String id, String name, Formula size_formula, SIUnit size) {
		this.id = id;
		this.name = name;
		this.size_formula = size_formula;
		this.size = size;
	}

	/**
	 * @return the id of the datatype
	 */
	public String getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!this.getClass().equals(o.getClass())) return false;
		return ((Datatype)o).id.equals(id);
	}
}
