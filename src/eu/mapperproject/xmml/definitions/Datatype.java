package eu.mapperproject.xmml.definitions;

import eu.mapperproject.xmml.util.Formula;
import eu.mapperproject.xmml.util.SIUnit;

public class Datatype {
	private String id;
	private String name;
	private Formula size_formula;
	private SIUnit size;

	public Datatype(String id, String name, Formula size_formula, SIUnit size) {
		this.id = id;
		this.name = name;
		this.size_formula = size_formula;
		this.size = size;
	}
}
