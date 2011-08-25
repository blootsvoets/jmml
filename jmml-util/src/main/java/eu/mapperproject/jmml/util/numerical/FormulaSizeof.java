package eu.mapperproject.jmml.util.numerical;

import java.util.Map;

/** Represent a single number */
class FormulaSizeof extends InterpretedFormula {
	/** Datatypes and their sizes */
	private enum Datatype {
		LONG(64), DOUBLE(64), FLOAT(32), INT(32), SHORT(16), BYTE(8), CHAR(8);
		private int size;
		Datatype(int size) {
			this.size = size;
		}
		
		public int getSize() {
			return this.size;
		}
	}
	
	private FormulaSizeof.Datatype value;

	FormulaSizeof(String var) {
		this.value = Datatype.valueOf(var.toUpperCase());
	}

	@Override
	public double evaluate(Map<String, Integer> variables) {
		return this.value.getSize();
	}
}