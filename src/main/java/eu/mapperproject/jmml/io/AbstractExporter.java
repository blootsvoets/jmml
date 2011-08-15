package eu.mapperproject.jmml.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exports a file
 * @author Joris Borgdorff
 */
public abstract class AbstractExporter {
	protected Writer out;

	public void export(File f) throws IOException {
		Writer fout = null;
		try {
			fout = new OutputStreamWriter(new FileOutputStream(f), Charset.forName("UTF-8"));
			this.export(fout);
			fout.close();
		} finally {
			if (fout != null)
				fout.close();
		}
	}

	public void export(Writer out) throws IOException {
		this.out = out;
		this.convert();
	}

	public void print() {
		try {
			this.export(new OutputStreamWriter(System.out));
		} catch (IOException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "System.out could not be used for writing: {}", ex);
		}
	}

	protected abstract void convert() throws IOException;
	
	protected void print(StringBuilder sb) throws IOException {
		this.out.write(sb.toString());
		sb.setLength(0);
	}
}
