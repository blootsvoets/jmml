package eu.mapperproject.xmml.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import eu.mapperproject.xmml.ModelMetadata;
import eu.mapperproject.xmml.XMMLDocument;
import eu.mapperproject.xmml.definitions.Converter;
import eu.mapperproject.xmml.definitions.Datatype;
import eu.mapperproject.xmml.definitions.Scale;
import eu.mapperproject.xmml.definitions.Submodel;
import eu.mapperproject.xmml.definitions.XMMLDefinitions;
import eu.mapperproject.xmml.topology.CouplingTopology;
import eu.mapperproject.xmml.util.Formula;
import eu.mapperproject.xmml.util.MultiStringParseToken;
import eu.mapperproject.xmml.util.MultiStringParseToken.Optional;
import eu.mapperproject.xmml.util.SIRange;
import eu.mapperproject.xmml.util.SIUnit;
import eu.mapperproject.xmml.util.ScaleModifier.Dimension;
import eu.mapperproject.xmml.util.Version;

import nu.xom.Attribute;
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
		XMMLDefinitions def = this.parseDefinitions(model.getChildElements("definitions"));
		
		return new XMMLDocument(mm, def, null, xmmlVersion);
	}
	
	/** Parses and returns the metadata of a model */
	private ModelMetadata parseModelMetadata(Element model) {
		String id = model.getAttributeValue("id");
		String name = model.getAttributeValue("name");
		Elements eDescription = model.getChildElements("description");
		String description = eDescription.size() > 0 ? eDescription.get(0).getValue() : null;
		
		String versionString = model.getAttributeValue("version");
		Version modelVersion = null;
		if (versionString != null) {
			modelVersion = new Version(versionString);
		}
		
		return new ModelMetadata(id, name, description, modelVersion);
	}
	
	private XMMLDefinitions parseDefinitions(Elements definitions) {
		Element definition = definitions.get(0);
		Map<String, Datatype> dtypes = parseDatatypes(definition.getChildElements("datatype"));
		Map<String, Converter> cverter = parseConverters(definition.getChildElements("converter"), dtypes);
		Map<String, Submodel> smodel = parseSubmodels(definition.getChildElements("submodel"), dtypes);
		return new XMMLDefinitions(dtypes, cverter, smodel);
	}
	
	/** Parses and returns the datatypes of a model */
	private Map<String, Datatype> parseDatatypes(Elements datatypes) {
		Map<String, Datatype> map = new HashMap<String, Datatype>();
		
		for (int i = 0; i < datatypes.size(); i++) {
			Element datatype = datatypes.get(i);
			String id = datatype.getAttributeValue("id");
			String name = datatype.getAttributeValue("name");
			
			String size_estimate = datatype.getAttributeValue("size_estimate");
			Formula size_formula = null; SIUnit size_bytes = null;
			if (size_estimate != null) {
				try {
					size_formula = Formula.parseFormula(size_estimate);
				}
				catch (ParseException e) {
					try {
						size_bytes = SIUnit.parseSIUnit(size_estimate);
						if (size_bytes.getDimension() != Dimension.DATA) {
							size_bytes = null;
						}
					} catch (IllegalArgumentException iae) {
						// Check for null later
					}
				}
				if (size_formula == null && size_bytes == null) {
					logger.warning("size estimate of datatype '" + id + "' does not contain a valid size formula or size in bytes. Instead, it contains '" + size_estimate + "'");
				}
			}
			map.put(id, new Datatype(id, name, size_formula, size_bytes));
		}
		
		return map;
	}

	/** Parses and returns the converters of a model */
	private Map<String, Converter> parseConverters(Elements converters, Map<String, Datatype> datatypes) {
		Map<String, Converter> map = new HashMap<String, Converter>(converters.size());
		
		for (int i = 0; i < converters.size(); i++) {
			Converter ret = null;
			Element converter = converters.get(i);
			String id = converter.getAttributeValue("id");
			String fromData = converter.getAttributeValue("from");
			Datatype from = datatypes.get(fromData);
			String toData = converter.getAttributeValue("to");
			Datatype to = datatypes.get(toData);
			
			if (from == null) {
				logger.warning("converter '" + id + "' contains unknown data type '" + fromData + "' in its from-field and will not be processed");
				continue;
			}
			if (to == null) {
				logger.warning("converter '" + id + "' contains unknown data type '" + toData + "' in its to-field and will not be processed");
				continue;
			}
			
			Element requires = converter.getFirstChildElement("requires");
			if (requires != null) {
				String reqName = requires.getAttributeValue("name");
				String reqDataName = requires.getAttributeValue("datatype");
				Datatype reqData = datatypes.get(reqDataName);
				String src = requires.getAttributeValue("src");
				
				if (reqData == null) {
					logger.warning("requirement of converter '" + id + "' contains unknown data type '" + reqDataName + "' and will not be considered");
				}
				else {
					ret = new Converter(id, from, to, reqName, reqData, src);
				}
			}
			
			if (ret == null) {
				ret = new Converter(id, from, to);
			}
			map.put(id, ret);
		}
		
		return map;
	}

	/** Parses and returns the submodels of a model */
	private Map<String,Submodel> parseSubmodels(Elements submodels, Map<String, Datatype> datatypes) {
		Map<String, Submodel> map = new HashMap<String, Submodel>();
		
		for (int i = 0; i < submodels.size(); i++) {
			Element submodel = submodels.get(i);
			ModelMetadata meta = parseModelMetadata(submodel);
			boolean initial = submodel.getAttributeValue("init").equals("yes");
			Optional stateful = MultiStringParseToken.findObject(submodel.getAttributeValue("stateful"), MultiStringParseToken.optionalTokens);
			Optional interactive = MultiStringParseToken.findObject(submodel.getAttributeValue("interactive"), MultiStringParseToken.optionalTokens);
			
			Map<String, Scale> scales = new HashMap<String, Scale>();
			scales.putAll(parseScale(submodel.getChildElements("timescale"), Dimension.TIME));
			scales.putAll(parseScale(submodel.getChildElements("spacescale"), Dimension.LENGTH));
			scales.putAll(parseScale(submodel.getChildElements("otherscale"), Dimension.OTHER));

			// TODO: finish submodels
			
			map.put(meta.getId(), new Submodel(meta, scales, null, null, null, initial, stateful, interactive));
		}
		
		return map;
	}
	
	/** Parses and returns the scales of a submodel */
	private Map<String, Scale> parseScale(Elements scales, Dimension dim) {
		Map<String, Scale> map = new HashMap<String, Scale>();
		
		for (int i = 0; i < scales.size(); i++) {
			Element scale = scales.get(i);
			String id = scale.getAttributeValue("id");
			if (id == null) {
				if (dim == Dimension.TIME) {
					id = "t";
				}
				else if (dim == Dimension.LENGTH) {
					if (!map.containsKey("x")) id = "x";
					else if (!map.containsKey("y")) id = "y";
					else if (!map.containsKey("z")) id = "z";
					else if (!map.containsKey("u")) id = "u";
					else if (!map.containsKey("v")) id = "v";
					else if (!map.containsKey("w")) id = "w";
					else {
						logger.warning("With more than 6 length scales, please provide names, now random names are used.");
						id = "x" + Math.random();
					}
				}
				else {
					if (!map.containsKey("a")) id = "a";
					else if (!map.containsKey("b")) id = "b";
					else if (!map.containsKey("c")) id = "c";
					else if (!map.containsKey("d")) id = "d";
					else if (!map.containsKey("e")) id = "e";
					else if (!map.containsKey("f")) id = "f";
					else {
						logger.warning("With more than 6 length scales, please provide names, now random names are used.");
						id = "a" + Math.random();
					}		
				}
			}
			
			SIRange delta = null; boolean deltaFixed = true;
			String dattr = scale.getAttributeValue("delta");
			if (dattr != null) {
				delta = new SIRange(SIUnit.parseSIUnit(dattr));
			}
			else {
				Element eDelta = scale.getFirstChildElement("delta");
				if (eDelta == null) {
					throw new IllegalArgumentException("Can not parse submodel scale if it contains no information on step size");
				}
				else {
					String minStr = eDelta.getAttributeValue("min"), maxStr = eDelta.getAttributeValue("max");
					SIUnit min = minStr == null ? null : SIUnit.parseSIUnit(minStr);
					SIUnit max = maxStr == null ? null : SIUnit.parseSIUnit(maxStr);
					
					delta = new SIRange(min, max);
					if (eDelta.getAttributeValue("type").equals("variable")) {
						deltaFixed = false;
					}
				}
			}
			
			SIRange max = null; boolean maxFixed = true;
			String mattr = scale.getAttributeValue("max");
			if (mattr != null) {
				max = new SIRange(SIUnit.parseSIUnit(dattr));
			}
			else {
				Element eMax = scale.getFirstChildElement("max");
				if (eMax == null) {
					throw new IllegalArgumentException("Can not parse submodel scale if it contains no information on maximum size");
				}
				else {
					String minStr = eMax.getAttributeValue("min"), maxStr = eMax.getAttributeValue("max");
					SIUnit min = minStr == null ? null : SIUnit.parseSIUnit(minStr);
					SIUnit mx = maxStr == null ? null : SIUnit.parseSIUnit(maxStr);
					
					delta = new SIRange(min, mx);
					if (eMax.getAttributeValue("type").equals("variable")) {
						maxFixed = false;
					}
				}
			}

			Attribute dims = scale.getAttribute("dimensions");
			int dimensions = 1;
			if (dims != null) {
				dimensions = Integer.parseInt(dims.getValue());
			}
			String dimName = scale.getAttributeValue("name"); 
			
			map.put(id, new Scale(id, dim, delta, deltaFixed, max, maxFixed, dimensions, dimName));
		}
		
		return map;
	}
	
	private CouplingTopology parseTopology(Elements topology, XMMLDefinitions definitions) {
		return null;
	}
}
