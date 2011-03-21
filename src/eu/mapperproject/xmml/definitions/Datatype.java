package eu.mapperproject.xmml.definitions;

import eu.mapperproject.xmml.Identifiable;
import eu.mapperproject.xmml.util.numerical.Formula;
import eu.mapperproject.xmml.util.numerical.SIUnit;
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
		if (id == null) {
			throw new IllegalArgumentException("For a dataype, the id parameter may not be null");
		}
		this.id = id;
		this.name = name;
		this.size_formula = size_formula;
		this.size = size;
	}

	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		return ((Datatype)o).id.equals(id);
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public boolean deepEquals(Object o) {
		if (!this.equals(o)) return false;
		final Datatype dt = (Datatype)o;
		return (name == null ? dt.name == null : this.name.equals(dt.name))
			&& (size_formula == null ? dt.size_formula == null : this.size_formula.equals(dt.size_formula))
			&& (size == null ? dt.size == null : this.size.equals(dt.size));
	}
}
