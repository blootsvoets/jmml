/*
 * 
 */
package eu.mapperproject.jmml.io;

import eu.mapperproject.jmml.specification.*;
import eu.mapperproject.jmml.specification.annotated.AnnotatedCoupling;
import eu.mapperproject.jmml.specification.annotated.AnnotatedInstance;
import eu.mapperproject.jmml.specification.annotated.AnnotatedModel;
import eu.mapperproject.jmml.specification.annotated.AnnotatedScale;
import eu.mapperproject.jmml.util.ArrayMap;
import eu.mapperproject.jmml.util.ArraySet;
import eu.mapperproject.jmml.util.FastArrayList;
import eu.mapperproject.jmml.util.numerical.SIUnit;
import eu.mapperproject.jmml.util.numerical.ScaleFactor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 *
 * @author Joris Borgdorff
 */
public class MUSCLEBuildExporter extends AbstractExporter {

	private final static Logger log = Logger.getLogger(MUSCLEBuildExporter.class.getName());
	private final AnnotatedModel model;
	private final static String MUSCLE_HOME = System.getenv("MUSCLE_HOME");

	public MUSCLEBuildExporter(AnnotatedModel model) {
		if (MUSCLE_HOME == null) {
			throw new IllegalStateException("Environment variable MUSCLE_HOME is not set. Set this");
		}
		this.model = model;
	}

	@Override
	protected void convert() throws IOException {
		log.info("Generating muscle snippet");
		STGroup stg = new STGroupFile("muscle_build.stg");
		ST cxa = stg.getInstanceOf("cxa");

		Set<String> classPaths = new ArraySet<String>();
		int classPathLength = 0;
		Set<String> libPaths = new ArraySet<String>();
		int libPathLength = 0;
		Map<String, Connection> connections = new ArrayMap<String, Connection>();
		SIUnit maxTime = new SIUnit(-1, ScaleFactor.SECOND);

		// Add kernels, class- and libpath, and parameters to template
		for (Instance oldInst : model.getTopology().getInstance()) {
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
					if (path != null && libPaths.add(path)) {
						libPathLength += path.length() + 1;
					}
				}

				path = impl.getPath();
				if (path != null && classPaths.add(path)) {
					classPathLength += path.length() + 1;
				}
			}

			if (terminal == null) {
				cxa.add("inst", inst);
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
					int dims = (int) ss.getDimensions().doubleValue();
					for (int dim = 1; dim < dims; dim++) {
						setParam(cxa, instName, "d" + AnnotatedInstance.spaceNames[iname], ss.getMinDelta().toString());
						setParam(cxa, instName, AnnotatedInstance.spaceNames[iname].toUpperCase(), ss.getMaxTotal().toString());
						iname++;
					}
				}
				for (Otherscale ss : inst.getOtherscaleInstance()) {
					setParam(cxa, instName, "d" + ss.getId(), ss.getMinDelta().toString());
					setParam(cxa, instName, ss.getId().toUpperCase(), ss.getMaxTotal().toString());
				}
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

		for (Coupling cOld : model.getTopology().getCoupling()) {
			AnnotatedCoupling coupling = (AnnotatedCoupling) cOld;
			String id = coupling.getFrom().getInstance().getId() + "." + coupling.getTo().getInstance().getId();
			Connection conn = connections.get(id);
			if (conn == null) {
				conn = new Connection();
				connections.put(id, conn);
			}
			conn.addCoupling(coupling);
		}

		cxa.add("connection", connections.values());
		this.out.write(cxa.render());
	}

	private static void setParam(ST cxa, String instance, String name, String value) {
		Param param = new Param();
		param.setId(name);
		param.setValue(value);
		cxa.addAggr("params.{instid, param}", instance, param);
	}

	private static String concat(Set<String> paths, int length) {
		StringBuilder path = new StringBuilder(length);
		for (String s : paths) {
			path.append(path);
			path.append(':');
		}
		// remove last ':'
		path.delete(length - 1, length);
		return path.toString();
	}

	private static String clazzToPath(String clazz) {
		if (clazz.startsWith("muscle.")) {
			return null;
		} else {
			return clazz.replace('.', '/') + ".java";
		}
	}

	static class Connection {

		FastArrayList<AnnotatedCoupling> couplings;

		Connection() {
			couplings = new FastArrayList<AnnotatedCoupling>();
		}

		void addCoupling(AnnotatedCoupling coupling) {
			couplings.add(coupling);
		}

		public String getFrom() {
			if (couplings.isEmpty()) {
				return null;
			} else {
				return couplings.get(0).getFrom().getInstance().getId();
			}
		}

		public String getTo() {
			if (couplings.isEmpty()) {
				return null;
			} else {
				return couplings.get(0).getTo().getInstance().getId();
			}
		}

		public List<AnnotatedCoupling> getCoupling() {
			if (couplings.isEmpty()) {
				return null;
			}
			return couplings;
		}
	}
}
