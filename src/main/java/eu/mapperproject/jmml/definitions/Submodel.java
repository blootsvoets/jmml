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
public class Submodel extends AbstractElement {
	/** Operators of the submodel execution loop (SEL) */
	public enum SEL {
		/** Initialization */
		finit(false),
		/** Intermediate observation */
		Oi(true),
		/** Solver */
		S(false),
		/** Boundary condition */
		B(false),
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
	
	private final ScaleSet scales;
	private final Map<String,Param> params;
	private final boolean initial;
	private final Optional stateful;
	private final Optional interactive;
	
	public Submodel(ModelMetadata meta, ScaleSet scales, Map<String,Port> in, Map<String,Port> out, Map<String,Param> params, boolean initial, Optional stateful, Optional interactive) {
		super(meta, in, out);
		this.scales = scales;
		this.params = params;
		this.initial = initial;
		this.stateful = stateful;
		this.interactive = interactive;
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
