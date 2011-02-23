package eu.mapperproject.xmml.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import eu.mapperproject.xmml.ModelMetadata;
import eu.mapperproject.xmml.XMMLDocument;
import eu.mapperproject.xmml.definitions.Converter;
import eu.mapperproject.xmml.definitions.Datatype;
import eu.mapperproject.xmml.definitions.Submodel;
import eu.mapperproject.xmml.definitions.XMMLDefinitions;
import eu.mapperproject.xmml.topology.CouplingTopology;
import eu.mapperproject.xmml.util.Version;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 * Import a xMML file using the XOM library
 *
 * @author Joris Borgdorff
 */
public class XMMLDocumentImporter {
	final static Logger logger = Logger.getLogger(XMMLDocumentImporter.class.getName());
	
	/** XOM parser that will be used */
	private Builder parser;
	
	/** Versions of the xMML format that are supported by this importer, inclusive. */
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
		Version xmmlVersion = new Version(model.getAttributeValue("xmml_version"));
		
		if (!SUPPORTED_VERSIONS.contains(xmmlVersion)) {
			throw new IllegalArgumentException("Document xMML format version " + xmmlVersion + "is not supported, only versions " +
					SUPPORTED_VERSIONS.versionString() + " are supported.");
		}
		ModelMetadata mm = this.parseModelMetadata(model);
		return new XMMLDocument(mm, null, null, xmmlVersion);
	}
	
	/** Parses and returns the metadata of a model */
	private ModelMetadata parseModelMetadata(Element model) {
		String id = model.getAttributeValue("id");
		String name = model.getAttributeValue("name");
		Elements eDescription = model.getChildElements("description");
		String description = eDescription.size() > 0 ? eDescription.get(0).getValue() : null;
		
		String versionString = model.getAttributeValue("version");
		Version modelVersion = new Version(versionString);
		
		return new ModelMetadata(id, name, description, modelVersion);
	}
	
	private XMMLDefinitions parseDefinitions(Elements definitions) {
		return null;
	}
	
	private Map<String, Datatype> parseDatatypes(Elements datatypes) {
		Map<String, Datatype> map = new HashMap<String, Datatype>();
		
		for (int i = 0; i < datatypes.size(); i++) {
			Element datatype = datatypes.get(i);
			String id = datatype.getAttributeValue("id");
			String name = datatype.getAttributeValue("name");
			String size_estimate = datatype.getAttributeValue("size_estimate");
		}
		
		return map;
	}

	private List<Converter> parseConverters(Elements converters) {
		
	}

	private Map<String,Submodel> parseSubmodels(Elements submodels) {
		
	}
	
	private CouplingTopology parseTopology(Elements topology, XMMLDefinitions definitions) {
		return null;
	}
}
