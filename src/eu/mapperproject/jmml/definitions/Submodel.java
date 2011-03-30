package eu.mapperproject.jmml.definitions;

import java.util.Map;

import eu.mapperproject.jmml.Identifiable;
import eu.mapperproject.jmml.ModelMetadata;
import eu.mapperproject.jmml.Param;
import eu.mapperproject.jmml.util.parser.MultiStringParseToken.Optional;

/** An xMML submodel
 * 
 * @author Joris Borgdorff
 */
public class Submodel implements Identifiable {
	/** Operators of the submodel execution loop (SEL) */
	public enum SEL {
		/** Initialization */
		finit(false),
		/** Intermediate observation */
		Oi(true),
		/** Boundary condition */
		B(false),
		/** Solver */
		S(false),
		/** Final observation */
		Of(true);
		
		private final boolean sending;
		private final String asString;
		private final static SEL values[] = SEL.values();
		
		private SEL(boolean send) {
			this.sending = send;
			this.asString = super.toString();
		}
		
		/** Whether the operator may send data */
		public boolean isSending() {
			return this.sending;
		}
		/** Whether the operator may receive data */
		public boolean isReceiving() {
			return !this.sending;
		}

		/** Get the SEL corresponding to their ordinal */
		public static SEL get(int i) {
			return values[i];
		}

		public SEL next() {
			return values[ordinal() + 1];
		}

		@Override
		public String toString() {
			return this.asString;
		}
	}
	
	private final Map<String,Port> in;
	private final Map<String,Port> out;
	private final ScaleSet scales;
	private final Map<String,Param> params;
	private final ModelMetadata meta;
	private final boolean initial;
	private final Optional stateful;
	private final Optional interactive;
	
	public Submodel(ModelMetadata meta, ScaleSet scales, Map<String,Port> in, Map<String,Port> out, Map<String,Param> params, boolean initial, Optional stateful, Optional interactive) {
		this.meta = meta;
		this.scales = scales;
		this.in = in;
		this.out = out;
		this.params = params;
		this.initial = initial;
		this.stateful = stateful;
		this.interactive = interactive;
	}

	@Override
	public String getId() {
		return meta.getId();
	}
	
	/** Get an in port by its name */
	public Port getInPort(String name) {
		return in.get(name);
	}

	/** Get an out port by its name */
	public Port getOutPort(String name) {
		return out.get(name);
	}

	/**
	 * @return the scale map of the submodel
	 */
	public ScaleSet getScaleMap() {
		return scales;
	}

	/**
	 * Whether the submodel is stateful
	 */
	public boolean isStateful() {
		return this.stateful != Optional.NO && this.stateful != null;
	}

	/**
	 * Whether the submodel starts at the beginning of the model
	 */
	public boolean isInitial() {
		return this.initial;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		return this.meta.equals(((Submodel)o).meta);
	}

	@Override
	public int hashCode() {
		return this.meta.hashCode();
	}

	@Override
	public boolean deepEquals(Object o) {
		if (!this.equals(o)) return false;
		final Submodel sub = (Submodel)o;
		return (this.in == null ? sub.in == null : this.in.equals(sub.in))
			&& (this.out == null ? sub.out == null : this.out.equals(sub.out))
			&& (this.scales == null ? sub.scales == null : this.scales.equals(sub.scales))
			&& (this.params == null ? sub.params == null : this.params.equals(sub.params))
			&& this.initial == sub.initial
			&& this.stateful == sub.stateful
			&& this.interactive == sub.interactive;
	}

	@Override
	public String toString() {
		return "Submodel " + this.meta.toString();
	}
}
