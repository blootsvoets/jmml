package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Instance;
import eu.mapperproject.jmml.specification.Mapper;
import eu.mapperproject.jmml.specification.MultiDimensionalScale;
import eu.mapperproject.jmml.specification.OptionalChoice;
import eu.mapperproject.jmml.specification.Otherscale;
import eu.mapperproject.jmml.specification.Scale;
import eu.mapperproject.jmml.specification.Submodel;
import eu.mapperproject.jmml.specification.YesNoChoice;
import eu.mapperproject.jmml.specification.graph.Numbered;
import eu.mapperproject.jmml.specification.util.ArrayMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedInstance extends Instance implements Numbered {
	private transient final static Logger logger = Logger.getLogger(AnnotatedInstance.class.getName());
	private transient int number;
	private transient Mapper mapperInst;
	private transient Submodel submodelInst;
	private transient boolean isfinal;
	private transient boolean isinitial;
	private transient final static String[] spaceNames = {"x", "y", "z", "u", "v", "w"};
	private transient final static String[] otherNames = {"a", "b", "c", "d", "e", "f"};
	
	public AnnotatedInstance() {
		super();
		this.isfinal = false;
		this.isinitial = false;
		this.mapperInst = null;
		this.submodelInst = null;
	}
	
	@Override
	public String getId() {
		if (this.id == null) {
			return this.mapper == null ? this.submodel : this.mapper;
		}
		return this.id;
	}
	
	@Override
	public void setId(String value) {
		if (value != null) {
			this.id = value;
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
		if (this.mapperInst == null) this.setMapper(this.mapper);
		return this.mapperInst;
	}
	
	@Override
	public void setSubmodel(String sub) {
		this.submodel = sub;
		if (sub != null) {
			if (this.mapper != null) {
				throw new IllegalStateException("Cannot create an instance of both a submodel and a mapper.");
			}
			if (this.id == null) {
				this.id = sub;
			}
			this.submodelInst = ObjectFactoryAnnotated.getModel().getDefinitions().getSubmodel(this.submodel);
		}
	}

	public Submodel getSubmodelInstance() {
		if (this.submodelInst == null) this.setSubmodel(this.submodel);
		return this.submodelInst;
	}
	
	public boolean ofSubmodel() {
		return this.submodelInst != null;
	}
	
	public AnnotatedPorts getPorts() {
		if (this.submodelInst != null) {
			return (AnnotatedPorts)this.submodelInst.getPorts();
		}
		else if (this.mapperInst != null) {
			return (AnnotatedPorts)this.mapperInst.getPorts();
		}
		else {
			return null;
		}
	}
	
	public AnnotatedScale getTimescaleInstance() {
		if (this.ofSubmodel()) {
			AnnotatedScale as;
			if (this.timescale != null){
				as = this.timescale;
			}
			else {
				as = (AnnotatedScale)this.getSubmodelInstance().getTimescale();
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
			Map<String,MultiDimensionalScale> scales = new ArrayMap<String, MultiDimensionalScale>();
			overrideScales(this.submodelInst.getSpacescale(), scales, spaceNames);
			overrideScales(this.getSpacescale(), scales, spaceNames);
			
			return new ArrayList<MultiDimensionalScale>(scales.values());
		}
		return null;
	}

	public List<Otherscale> getOtherscaleInstance() {
		if (this.ofSubmodel()) {
			if (otherscale == null) {
				otherscale = new ArrayList<Otherscale>();
			}
			Map<String,Otherscale> scales = new ArrayMap<String, Otherscale>();
			overrideScales(this.submodelInst.getOtherscale(), scales, otherNames);
			overrideScales(this.getOtherscale(), scales, otherNames);
			
			return new ArrayList<Otherscale>(scales.values());
		}
		return null;
	}
	
	private <V extends Scale> void overrideScales(Collection<V> with, Map<String,V> scales, String[] names) {
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
			}
			else {
				sid = names[j++];
			}
			while (scales.containsKey(sid)) {
				if (j >= names.length) {
					logger.warning("With more than 6 scales, please provide names, now random names are used.");
					sid = names[0] + Math.random();
				}
				else {
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
		if (o == null || getClass() != o.getClass()) return false;
		return this.number == ((AnnotatedInstance)o).number;
	}
	
	@Override
	public int hashCode() {
		return number;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName()
			+ "(" + (ofSubmodel() ? "submodel " : "mapper ")
			+ this.id
			+ "<" + (ofSubmodel() ? submodel : mapper) + ">)";
	}
	
	public boolean isFinal() {
		return isfinal;
	}

	public void isFinal(boolean isfinal) {
		this.isfinal = isfinal;
	}
	
	/**
	 * Whether this instance should be completed after a given number of timesteps
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
			}
			else {
				return this.stateful;
			}
		}
		else {
			return null;
		}
	}
	
	public boolean isInit() {
		if (this.init == null && this.ofSubmodel()) {
			return this.getSubmodelInstance().getInit() == YesNoChoice.YES;
		}
		else if (this.init == null && this.mapper != null) {
			return this.getMapperInstance().getInit() == YesNoChoice.YES;
		}
		else {
			return this.init == YesNoChoice.YES;
		}
	}
}
