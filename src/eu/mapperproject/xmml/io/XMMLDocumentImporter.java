package eu.mapperproject.xmml.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import eu.mapperproject.xmml.XMMLDocument;
import eu.mapperproject.xmml.util.Version;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 * Import a xMML file using the XOM library
 *
 * @author Joris Borgdorff
 */
public class XMMLDocumentImporter {
	/** XOM parser that will be used */
	private Builder parser;
	
	/** Versions of the xMML format that are supported by this importer. */
	private static final Version SUPPORTED_VERSIONS = new Version("0.1.x");
	
	/** Create a simple XOM parser to do the import */
	public XMMLDocumentImporter() {
		this.parser = new Builder();
	}
	
	/** Parse an xMML file using the XOM library */
	public XMMLDocument parse(File f) throws ValidityException, ParsingException, IOException {
		return this.parse(this.parser.build(f));
	}
	
	/** Parse an xMML file from an input stream using the XOM library */
	public XMMLDocument parse(InputStream stream) throws ValidityException, ParsingException, IOException {
		return this.parse(this.parser.build(stream));
	}
	
	/** Parse an xMML document using the XOM library */
	private XMMLDocument parse(Document doc) {
		Element model = doc.getRootElement();
		
		if (!this.isSupported(model.getAttribute("xmml_version").getValue())) {
			throw new IllegalArgumentException("Document xMML format is not supported");
		}
		return null;
	}
	
	private boolean isSupported(String versionString) {
		Version version = new Version(versionString);
		
		return SUPPORTED_VERSIONS.contains(version);
	}
}
