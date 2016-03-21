package eu.mapperproject.jmml;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.FileConverter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joris Borgdorff
 */
public class JMMLOptions {
	private JCommander jcom;
	
	@Parameter(names={"--no-collapse","-C"}, description="In the task graph, do not collapse nodes that are just iterations of the model.")
	public boolean nocollapse = false;
	
	@Parameter(names={"--ssm", "-s"}, validateWith=WritableFile.class, description="SVG output file of the scale separation map.")
	public String ssm;
	
	@Parameter(names={"--muscle", "-m"}, description="Output directory for MUSCLE 2 skeleton code.")
	public String muscle;

	@Parameter(names={"--cxa", "-c"}, validateWith=WritableFile.class, description="Output CxA file, configuration files MUSCLE 2.")
	public String cxa;
	
	@Parameter(names={"--taskgraph", "-g"}, validateWith=WritableFile.class, description="PDF of the task graph.")
	public String taskgraph;
	
	@Parameter(names={"--topology","-t"}, validateWith=WritableFile.class, description="PDF of the MML topology.")
	public String topology;
	
	@Parameter(names={"--domain","-d"}, validateWith=WritableFile.class, description="SVG of the domains in the model.")
	public String domain;
	
	@Parameter(names={"--dotfile","-o"}, validateWith=WritableFile.class, description="Output the raw DOT file for a generated PDF file.")
	private String dotfile;
	
	@Parameter(description="XMML_FILE (input file)", validateWith=ReadableFile.class,arity=1)
	private List<String> xmmlFile = new ArrayList<String>();
	
	public JMMLOptions(String... args) {
		this.jcom = new JCommander(this);
		try {
			jcom.parse(args);
		} catch (ParameterException ex) {
			System.err.println("Could not parse command line arguments: " + ex);
			jcom.usage();
			System.exit(1);
		}
	}
	
	public boolean wantsOutput() {
		return this.topology != null || this.taskgraph != null || this.ssm != null || this.domain != null || this.cxa != null || this.muscle != null;
	}
	
	public void printUsage() {
		jcom.usage();
	}
	
	public File getDotFile() throws IOException {
		if (this.dotfile == null) {
			return File.createTempFile("xmml", "dot");
		}
		else {
			return new File(this.dotfile).getAbsoluteFile();
		}
	}
	
	public File getXMMLFile() {
		return new File(xmmlFile.get(0)).getAbsoluteFile();
	}
	
	public static class WritableFile implements IParameterValidator {
		@Override
		public void validate(String name, String value) throws ParameterException {
			File f = new File(value).getAbsoluteFile();

			File parent = f.getParentFile();
			if (parent == null || !parent.exists()) {
				throw new ParameterException("Directory of file " + value
						+ " of parameter " + name + " does not exist");
			}
		}
	}
	
	public static class ReadableFile implements IParameterValidator {
		@Override
		public void validate(String name, String value) throws ParameterException {
			File f = new File(value);

			if (!f.canRead()) {
				throw new ParameterException("File " + value
						+ " of parameter " + name + " can not be read");
			}
		}
	}
}



