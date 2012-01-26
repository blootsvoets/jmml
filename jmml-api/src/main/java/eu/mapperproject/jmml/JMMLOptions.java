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
	
	@Parameter(names={"--no-collapse","-C"})
	public boolean nocollapse = false;
	
	@Parameter(names={"--ssm", "-s"},converter=FileConverter.class,validateWith=WritableFile.class)
	public File ssm;
	
	@Parameter(names={"--cxa", "-c"},converter=FileConverter.class,validateWith=WritableFile.class)
	public File cxa;
	
	@Parameter(names={"--taskgraph", "-g"},converter=FileConverter.class,validateWith=WritableFile.class)
	public File taskgraph;
	
	@Parameter(names={"--topology","-t"},converter=FileConverter.class,validateWith=WritableFile.class)
	public File topology;
	
	@Parameter(names={"--domain","-d"},converter=FileConverter.class,validateWith=WritableFile.class)
	public File domain;
	
	@Parameter(names={"--dotfile","-o"},converter=FileConverter.class,validateWith=WritableFile.class)
	private File dotfile;
	
	@Parameter(description="XMML_FILE",validateWith=ReadableFile.class,arity=1)
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
		return this.topology != null || this.taskgraph != null || this.ssm != null || this.domain != null || this.cxa != null;
	}
	
	public void printUsage() {
		jcom.usage();
	}
	
	public File getDotFile() throws IOException {
		if (this.dotfile == null) {
			return File.createTempFile("xmml", "dot");
		}
		else {
			return this.dotfile;
		}
	}
	
	public File getXMMLFile() {
		return new File(xmmlFile.get(0));
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



