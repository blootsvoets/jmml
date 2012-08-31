/*
 * 
 */
package eu.mapperproject.jmml.io;

import eu.mapperproject.jmml.specification.Datatype;
import eu.mapperproject.jmml.specification.Filter;
import eu.mapperproject.jmml.specification.Mapper;
import eu.mapperproject.jmml.specification.Submodel;
import eu.mapperproject.jmml.specification.Terminal;
import eu.mapperproject.jmml.specification.annotated.AnnotatedDefinition;
import eu.mapperproject.jmml.specification.annotated.AnnotatedDefinitions;
import eu.mapperproject.jmml.specification.annotated.AnnotatedModel;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joris Borgdorff
 */
public class MUSCLECodeGenerator {
	private final static Logger log = Logger.getLogger(MUSCLECodeGenerator.class.getName());
	private final AnnotatedModel model;
	
	public MUSCLECodeGenerator(AnnotatedModel model) {
		this.model = model;
	}
	
	public void create(File dir) throws IOException {
		dir = dir.getAbsoluteFile();
		log.log(Level.INFO, "Generating muscle directory layout in <{0}>", dir);
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IllegalArgumentException("Can not create MUSCLE skeleton directory " + dir.getAbsolutePath());
		}
		
		mkdir(dir, "lib");
		
		File srcdir = mkdir(dir, "src");
		if (srcdir != null) {
			AnnotatedDefinitions defs = model.getDefinitions();
			List<Datatype> datatypes = defs.getDatatype();
			for (AnnotatedDefinition def : defs.getTerminalOrFilterOrMapper()) {
				String clazz = def.getClazz();
				String id = def.getId();
				if (clazz == null) {
					if (def instanceof Submodel) {
						clazz = "submodel.";
					} else if (def instanceof Mapper) {
						clazz = "mapper.";
					} else if (def instanceof Filter) {
						clazz = "filter.";
					} else if (def instanceof Terminal) {
						clazz = "terminal.";
					}
					
					clazz +=  id.toLowerCase() + "."
							// First uppercase
							+ id.substring(0,1).toUpperCase()+id.substring(1);
					def.setClazz(clazz);
				}

				String pkg = def.getPackage();
				if (pkg.startsWith("muscle.")) {
					log.log(Level.INFO, "Not creating MUSCLE class {0}", clazz);
					continue;
				}
				String dirname = pkg.replace('.', '/');
				File javaDir = mkdir(srcdir, dirname);

				try {
					MUSCLEJavaExporter javaExport = new MUSCLEJavaExporter(def, datatypes);
					javaExport.export(new File(javaDir, def.getClazzName() + ".java"));
				} catch (Exception ex) {
					log.log(Level.WARNING, "CxA file could not be generated: " + ex, ex);
				}
			}
		}
		
		File cxadir = mkdir(dir, "cxa");
		if (cxadir != null) {
			MUSCLECxAExporter cxaExport = new MUSCLECxAExporter(model.getTopology());
			try {
				cxaExport.export(new File(cxadir,model.getId() + ".cxa.rb"));
			} catch (Exception ex) {
				log.log(Level.WARNING, "CxA file could not be generated: " + ex, ex);
			}
		}
	}
	
	private static File mkdir(File dir, String name) {
		File newdir = new File(dir, name);
		if (newdir.isDirectory() || newdir.mkdirs()) {
			log.log(Level.INFO, "created {0} directory {1}", new Object[]{name, newdir});
			return newdir;
		} else {
			log.log(Level.WARNING, "Can not create directory {0}", newdir);
			return null;
		}
	}
}
