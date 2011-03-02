package eu.mapperproject.xmml.definitions;

import java.util.Map;

import eu.mapperproject.xmml.ModelMetadata;
import eu.mapperproject.xmml.Param;
import eu.mapperproject.xmml.util.MultiStringParseToken.Optional;

public class Submodel {
	public enum SEL {
		finit(false), Oi(true), B(false), S(false), Of(true);
		
		private boolean sending;
		
		SEL(boolean send) {
			this.sending = send;
		}
		
		public boolean isSending() {
			return this.sending;
		}
		public boolean isReceiving() {
			return !this.sending;
		}
	}
	private Map<String,Port> in;
	private Map<String,Port> out;
	private Map<String,Scale> scales;
	private Map<String,Param> params;
	private ModelMetadata meta;
	private boolean initial;
	private Optional stateful;
	private Optional interactive;
	
	public Submodel(ModelMetadata meta, Map<String,Scale> scales, Map<String,Port> in, Map<String,Port> out, Map<String,Param> params, boolean initial, Optional stateful, Optional interactive) {
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
}
