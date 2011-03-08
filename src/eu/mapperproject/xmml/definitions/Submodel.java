package eu.mapperproject.xmml.definitions;

import java.util.Map;

import eu.mapperproject.xmml.Identifiable;
import eu.mapperproject.xmml.ModelMetadata;
import eu.mapperproject.xmml.Param;
import eu.mapperproject.xmml.util.MultiStringParseToken.Optional;

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
		
		private boolean sending;
		
		private SEL(boolean send) {
			this.sending = send;
		}
		
		/** Whether the operator may send data */
		public boolean isSending() {
			return this.sending;
		}
		/** Whether the operator may receive data */
		public boolean isReceiving() {
			return !this.sending;
		}
	}
	private Map<String,Port> in;
	private Map<String,Port> out;
	private ScaleMap scales;
	private Map<String,Param> params;
	private ModelMetadata meta;
	private boolean initial;
	private Optional stateful;
	private Optional interactive;
	
	public Submodel(ModelMetadata meta, ScaleMap scales, Map<String,Port> in, Map<String,Port> out, Map<String,Param> params, boolean initial, Optional stateful, Optional interactive) {
		this.meta = meta;
		this.scales = scales;
		this.in = in;
		this.out = out;
		this.params = params;
		this.initial = initial;
		this.stateful = stateful;
		this.interactive = interactive;
	}

	/**
	 * @return metadata id
	 */
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
	public ScaleMap getScaleMap() {
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
}
