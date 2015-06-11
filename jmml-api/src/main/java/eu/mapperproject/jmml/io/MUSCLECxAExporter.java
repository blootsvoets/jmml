/*
 * 
 */
package eu.mapperproject.jmml.io;

import eu.mapperproject.jmml.specification.Apply;
import eu.mapperproject.jmml.specification.Coupling;
import eu.mapperproject.jmml.specification.Formula;
import eu.mapperproject.jmml.specification.Implementation;
import eu.mapperproject.jmml.specification.Instance;
import eu.mapperproject.jmml.specification.Library;
import eu.mapperproject.jmml.specification.Mapper;
import eu.mapperproject.jmml.specification.MultiDimensionalScale;
import eu.mapperproject.jmml.specification.Otherscale;
import eu.mapperproject.jmml.specification.Param;
import eu.mapperproject.jmml.specification.Submodel;
import eu.mapperproject.jmml.specification.Terminal;
import eu.mapperproject.jmml.specification.annotated.AnnotatedCoupling;
import eu.mapperproject.jmml.specification.annotated.AnnotatedFormula;
import eu.mapperproject.jmml.specification.annotated.AnnotatedInstance;
import eu.mapperproject.jmml.specification.annotated.AnnotatedScale;
import eu.mapperproject.jmml.specification.annotated.AnnotatedTopology;
import eu.mapperproject.jmml.util.ArrayMap;
import eu.mapperproject.jmml.util.ArraySet;
import eu.mapperproject.jmml.util.FastArrayList;
import eu.mapperproject.jmml.util.numerical.SIUnit;
import eu.mapperproject.jmml.util.numerical.ScaleFactor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 *
 * @author Joris Borgdorff
 */
public class MUSCLECxAExporter extends AbstractExporter {

	private final static Logger log = Logger.getLogger(MUSCLECxAExporter.class.getName());
	private final AnnotatedTopology topology;

	public MUSCLECxAExporter(AnnotatedTopology topology) {
		this.topology = topology;
	}

	@Override
	protected void convert() throws IOException {
		log.info("Generating muscle snippet");
		STGroup stg = new STGroupFile("muscle_cxa.stg");
		ST cxa = stg.getInstanceOf("cxa");

		List<Instance> insts = topology.getInstance();
		Set<String> classPaths = new ArraySet<String>();
		int classPathLength = 0;
		Set<String> libPaths = new ArraySet<String>();
		int libPathLength = 0;
		SIUnit maxTime = new SIUnit(-1, ScaleFactor.SECOND);

		// Add kernels, class- and libpath, and parameters to template
		for (Instance oldInst : insts) {
			try {
				Submodel submodel = null;
				Mapper mapper;
				Terminal terminal = null;
				Implementation impl;
				AnnotatedInstance inst = (AnnotatedInstance) oldInst;
				if (inst.ofSubmodel()) {
					submodel = inst.getSubmodelInstance();
					impl = submodel.getImplementation();
				} else if (inst.ofMapper()) {
					mapper = inst.getMapperInstance();
					impl = mapper.getImplementation();
				} else if (inst.ofTerminal()) {
					terminal = inst.getTerminalInstance();
					impl = terminal.getImplementation();
				} else {
					throw new IllegalStateException("Instance " + inst + " is not defined as a submodel, mapper, or terminal");
				}

				if (impl != null) {
					String path;
					for (Library lib : impl.getLibrary()) {
						path = lib.getPath();
						if (path != null) {
							libPaths.add(path);
							libPathLength += path.length() + 1;
						}
					}

					if (impl.getLanguage() == null || impl.getLanguage().toLowerCase().startsWith("java")) {
						path = impl.getPath();
						if (path != null) {
							classPaths.add(path);
							classPathLength += path.length() + 1;
						}
					}
				}

				if (terminal == null) {
					if (impl != null && impl.getLanguage() != null) {
						String language = impl.getLanguage().toLowerCase();
						if (language.startsWith("python")) {
							cxa.addAggr("pythoninst.{inst, script}", inst, impl.getPath());
						} else if (language.equals("c") || language.startsWith("c-") || language.startsWith("c++") || language.startsWith("fortran")) {
							boolean usesMpi = false;
							if (impl.getCores() != null && impl.getCores().intValue() > 12) {
								usesMpi = true;
							} else {
								for (String value : impl.getOtherAttributes().values()) {
									if (value.contains("mpi")) {
										usesMpi = true;
										break;
									}
								}
							}
							if (usesMpi) {
								cxa.addAggr("mpiinst.{inst, executable}", inst, impl.getPath());
							} else {
								cxa.addAggr("nativeinst.{inst, executable}", inst, impl.getPath());
							}
						} else if (language.equals("matlab")) {
							cxa.addAggr("matlabinst.{inst, script}", inst, impl.getPath());
						} else {
							cxa.add("inst", inst);
						}
					} else {
						cxa.add("inst", inst);
					}
				} else {
					cxa.add("term", inst);
				}

				String instName = inst.getId();
				for (Param param : inst.getParam()) {
					cxa.addAggr("params.{instid, param}", instName, param);
				}

				if (submodel != null) {
					AnnotatedScale scale = inst.getTimescaleInstance();
					SIUnit total = scale.getMaxTotal();
					if (total.compareTo(maxTime) > 0) {
						maxTime = total;
					}
					setParam(cxa, instName, "dt", scale.getMinDelta().toString());
					setParam(cxa, instName, "T", maxTime.toString());
					int iname = 0;
					for (MultiDimensionalScale ss : inst.getSpacescaleInstance()) {
						setParam(cxa, instName, "d" + ss.getId(), ss.getMinDelta().toString());
						setParam(cxa, instName, ss.getId().toUpperCase(), ss.getMaxTotal().toString());
						iname++;
						if (ss.getDimensions() != null) {
							int dims = ss.getDimensions().intValue();
							for (int dim = 1; dim < dims; dim++) {
								setParam(cxa, instName, "d" + AnnotatedInstance.spaceNames[iname], ss.getMinDelta().toString());
								setParam(cxa, instName, AnnotatedInstance.spaceNames[iname].toUpperCase(), ss.getMaxTotal().toString());
								iname++;
							}
						}
					}
					for (Otherscale ss : inst.getOtherscaleInstance()) {
						setParam(cxa, instName, "d" + ss.getId(), ss.getMinDelta().toString());
						setParam(cxa, instName, ss.getId().toUpperCase(), ss.getMaxTotal().toString());
					}
				}
			} catch (NullPointerException ex) {
				log.log(Level.SEVERE, "Instance " + oldInst.getId() + " not well-defined", ex);
				throw ex;
			}
		}

		cxa.add("max_timesteps", maxTime);

		if (classPathLength > 0) {
			cxa.add("classpath", concat(classPaths, classPathLength));
		}

		if (libPathLength > 0) {
			cxa.add("libpath", concat(libPaths, libPathLength));
		}

		// Add couplings to template
		for (Coupling cOld : topology.getCoupling()) {
			AnnotatedCoupling coupling = (AnnotatedCoupling) cOld;
			Connection conn = new Connection(coupling);
			for (Apply filter : coupling.getApply()) {
				conn.addFilter(filter.getFilter(), filter.getFactor());
				System.out.println("added filter " + filter.getFilter());
			}
			cxa.add("connection", conn);
		}

		this.out.write(cxa.render());
	}

	private static void setParam(ST cxa, String instance, String name, String value) {
		Param param = new Param();
		param.setId(name);
		param.setValue(value);
		cxa.addAggr("params.{instid, param}", instance, param);
	}

	private static String concat(Set<String> paths, int length) {
		if (paths.isEmpty()) {
			return "";
		}
		StringBuilder path = new StringBuilder(length);
		for (String s : paths) {
			path.append(s);
			path.append(':');
		}
		// remove last ':'
		path.delete(path.length() - 1, path.length());
		return path.toString();
	}

	static class Connection {

		final AnnotatedCoupling coupling;
		FastArrayList<String> filters;

		Connection(AnnotatedCoupling coupling) {
			this.coupling = coupling;
			filters = new FastArrayList<String>(0);
		}

		public String getFrom() {
			return coupling.getFrom().getInstance().getId();
		}

		public String getTo() {
			return coupling.getTo().getInstance().getId();
		}

		public String getEqualPort() {
			if (coupling.getTo().getPort().getId().equals(coupling.getFrom().getPort().getId())) {
				return coupling.getTo().getPort().getId();
			} else {
				return null;
			}
		}

		public AnnotatedCoupling getCoupling() {
			return coupling;
		}

		void addFilter(String clazz, Formula value) {
			if (value == null) {
				filters.add(clazz);
			} else {
				filters.add(clazz + "_" + ((AnnotatedFormula) value).interpret().evaluate(new HashMap<String, Integer>(0)));
			}
		}

		public List<String> getFilters() {
			if (this.filters.isEmpty()) {
				return null;
			}
			return filters;
		}
	}
}
