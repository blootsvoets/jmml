package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.*;
import eu.mapperproject.jmml.util.ArrayMap;
import eu.mapperproject.jmml.util.Numbered;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;

/**
 * Adds scale names to scales, if they were unnamed, and adds timestep
 * functionality.
 *
 * @author Joris Borgdorff
 */
public class AnnotatedInstance extends Instance implements Numbered {

	private transient final static Logger logger = Logger.getLogger(AnnotatedInstance.class.getName());
	private transient int number;
	private transient Terminal terminalInst;
	private transient Mapper mapperInst;
	private transient Submodel submodelInst;
	public final static String[] spaceNames = {"x", "y", "z", "u", "v", "w"};
	public final static String[] otherNames = {"a", "b", "c", "d", "e", "f"};

	public AnnotatedInstance() {
		super();
		this.mapperInst = null;
		this.submodelInst = null;
	}

	@Override
	public String getId() {
		if (this.id == null) {
			if (this.submodel != null) {
				return this.submodel;
			} else if (this.mapper != null) {
				return this.mapper;
			} else {
				return this.terminal;
			}
		}
		return this.id;
	}

	@Override
	public void setId(String value) {
		if (value != null) {
			this.id = value;
		}
	}

	public String getClazz() {
		if (this.ofSubmodel()) {
			return this.submodelInst.getClazz();
		} else if (this.ofMapper()) {
			return this.mapperInst.getClazz();
		} else if (this.ofTerminal()) {
			return this.terminalInst.getClazz();
		} else {
			throw new IllegalStateException("Instance " + this + " does not describe a computational element.");
		}
	}

	@Override
	public void setMapper(String map) {
		this.mapper = map;
		if (map != null) {
			if (this.submodel != null) {
				throw new IllegalStateException("Cannot create an instance of both a submodel and a mapper.");
			}
			if (this.id == null) {
				this.id = map;
			}
			this.mapperInst = ObjectFactoryAnnotated.getModel().getDefinitions().getMapper(this.mapper);
		}
	}

	public Mapper getMapperInstance() {
		if (this.mapperInst == null) {
			this.setMapper(this.mapper);
		}
		return this.mapperInst;
	}

	@Override
	public void setSubmodel(String sub) {
		this.submodel = sub;
		if (sub != null) {
			if (this.mapper != null || this.terminal != null) {
				throw new IllegalStateException("Cannot create an instance of both a submodel and a mapper or a terminal.");
			}
			if (this.id == null) {
				this.id = sub;
			}
			this.submodelInst = ObjectFactoryAnnotated.getModel().getDefinitions().getSubmodel(this.submodel);
		}
	}

	public Terminal getTerminalInstance() {
		if (this.terminalInst == null) {
			this.setTerminal(this.terminal);
		}
		return this.terminalInst;
	}

	@Override
	public void setTerminal(String term) {
		this.terminal = term;
		if (term != null) {
			if (this.submodel != null || this.mapper != null) {
				throw new IllegalStateException("Cannot create an instance of both a terminal and a submodel or a mapper.");
			}
			if (this.id == null) {
				this.id = term;
			}
			this.terminalInst = ObjectFactoryAnnotated.getModel().getDefinitions().getTerminal(this.terminal);
		}
	}

	public Submodel getSubmodelInstance() {
		if (this.submodelInst == null) {
			this.setSubmodel(this.submodel);
		}
		return this.submodelInst;
	}

	public boolean ofSubmodel() {
		return this.getSubmodelInstance() != null;
	}

	public boolean ofMapper() {
		return this.getMapperInstance() != null;
	}

	public boolean ofTerminal() {
		return this.getTerminalInstance() != null;
	}

	public AnnotatedPorts getPorts() {
		if (this.ofSubmodel()) {
			return (AnnotatedPorts) this.submodelInst.getPorts();
		} else if (this.ofMapper()) {
			return (AnnotatedPorts) this.mapperInst.getPorts();
		} else if (this.ofTerminal()) {
			return (AnnotatedPorts) this.terminalInst.getPorts();
		} else {
			throw new IllegalStateException("Instance " + getId() + " does can not return ports, since it does not have a mapper or submodel to point to.");
		}
	}

	public AnnotatedScale getTimescaleInstance() {
		if (this.ofSubmodel()) {
			AnnotatedScale as = null;
			AnnotatedScale instTimescale = (AnnotatedScale) this.getSubmodelInstance().getTimescale();
			if (this.timescale == null) {
				as = instTimescale;
			} else {
				as = this.timescale;
				if (as.getDelta() == null) {
					as.setDelta(instTimescale.getDelta());
				}
				if (as.getTotal() == null) {
					as.setTotal(instTimescale.getTotal());
				}
			}
			if (as.getId() == null) {
				as.setId("t");
			}
			return as;
		}
		return null;
	}

	public List<MultiDimensionalScale> getSpacescaleInstance() {
		if (this.ofSubmodel()) {
			if (spacescale == null) {
				spacescale = new ArrayList<MultiDimensionalScale>();
			}
			Map<String, MultiDimensionalScale> scales = new ArrayMap<String, MultiDimensionalScale>();
			overrideScales(this.submodelInst.getSpacescale(), scales, spaceNames);
			overrideScales(spacescale, scales, spaceNames);

			return new ArrayList<MultiDimensionalScale>(scales.values());
		}
		return null;
	}

	public List<Otherscale> getOtherscaleInstance() {
		if (this.ofSubmodel()) {
			if (otherscale == null) {
				otherscale = new ArrayList<Otherscale>();
			}
			Map<String, Otherscale> scales = new ArrayMap<String, Otherscale>();
			overrideScales(this.submodelInst.getOtherscale(), scales, otherNames);
			overrideScales(otherscale, scales, otherNames);

			return new ArrayList<Otherscale>(scales.values());
		}
		return null;
	}

	private <V extends Scale> void overrideScales(Collection<V> with, Map<String, V> scales, String[] names) {
		List<V> nameless = new ArrayList<V>();
		String sid;
		for (V os : with) {
			sid = os.getId();
			if (sid != null) {
				if (scales.containsKey(sid)) {
					logger.log(Level.WARNING, "Multiple scales with the same id have been defined in submodel ''{0}''. Scales should have a unique id per submodel.", submodel);
				}
				scales.put(sid, os);
			} else {
				nameless.add(os);
			}
		}
		int j = 0;
		for (V os : nameless) {
			if (j >= names.length) {
				logger.warning("With more than 6 scales, please provide names, now random names are used.");
				sid = names[0] + Math.random();
			} else {
				sid = names[j++];
			}
			while (scales.containsKey(sid)) {
				if (j >= names.length) {
					logger.warning("With more than 6 scales, please provide names, now random names are used.");
					sid = names[0] + Math.random();
				} else {
					sid = names[j++];
				}
			}

			os.setId(sid);
			scales.put(sid, os);
		}
	}

	@Override
	public boolean deepEquals(Object o) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getNumber() {
		return this.number;
	}

	@Override
	public void setNumber(int num) {
		this.number = num;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		return this.number == ((AnnotatedInstance) o).number;
	}

	@Override
	public int hashCode() {
		return number;
	}

	@Override
	public String toString() {
		return (ofSubmodel() ? "submodel " : "mapper ")
				+ this.id
				+ "<" + (ofSubmodel() ? submodel : mapper) + ">)";
	}

	public boolean isFinal() {
		if (this.ofSubmodel()) {
			Submodel sub = this.getSubmodelInstance();
			for (JAXBElement<Port> port : ((AnnotatedPorts) sub.getPorts()).getInOrOut()) {
				if (port.getValue().getOperator() == SEL.OF) {
					return false;
				}
			}
			return true;
		} else {
			Mapper map = this.getMapperInstance();
			return !((AnnotatedPorts) map.getPorts()).hasOutPort();
		}
	}

	/**
	 * Whether this instance should be completed after a given number of
	 * timesteps
	 *
	 * @param steps the number of timesteps so far
	 */
	public boolean isCompleted(int steps) {
		if (this.ofSubmodel()) {
			return this.getTimescaleInstance().getSteps() <= steps + 1;
		}
		return true;
	}

	public OptionalChoice isStateful() {
		if (this.ofSubmodel()) {
			if (this.stateful == null) {
				return this.getSubmodelInstance().getStateful();
			} else {
				return this.stateful;
			}
		} else {
			return null;
		}
	}

	public boolean isInit() {
		if (this.init == null && this.ofSubmodel()) {
			Submodel sub = this.getSubmodelInstance();
			return sub.getInit() == YesNoChoice.YES || !((AnnotatedPorts) sub.getPorts()).hasInPort();
		} else if (this.init == null && this.mapper != null) {
			Mapper map = this.getMapperInstance();
			return this.getMapperInstance().getInit() == YesNoChoice.YES || !((AnnotatedPorts) map.getPorts()).hasInPort();
		} else {
			return this.init == YesNoChoice.YES;
		}
	}
}
