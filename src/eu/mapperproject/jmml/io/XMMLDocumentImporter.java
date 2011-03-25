package eu.mapperproject.jmml.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import eu.mapperproject.jmml.ModelMetadata;
import eu.mapperproject.jmml.Param;
import eu.mapperproject.jmml.MMLDocument;
import eu.mapperproject.jmml.definitions.Converter;
import eu.mapperproject.jmml.definitions.Datatype;
import eu.mapperproject.jmml.definitions.Port;
import eu.mapperproject.jmml.definitions.ScaleMap;
import eu.mapperproject.jmml.definitions.Submodel;
import eu.mapperproject.jmml.definitions.MMLDefinitions;
import eu.mapperproject.jmml.definitions.Submodel.SEL;
import eu.mapperproject.jmml.topology.Coupling;
import eu.mapperproject.jmml.topology.CouplingTopology;
import eu.mapperproject.jmml.topology.Domain;
import eu.mapperproject.jmml.topology.Filter;
import eu.mapperproject.jmml.topology.Instance;
import eu.mapperproject.jmml.topology.InstancePort;
import eu.mapperproject.jmml.util.Version;
import eu.mapperproject.jmml.util.numerical.Formula;
import eu.mapperproject.jmml.util.numerical.SIUnit;
import eu.mapperproject.jmml.util.numerical.ScaleModifier.Dimension;
import eu.mapperproject.jmml.util.parser.MultiStringParseToken;
import eu.mapperproject.jmml.util.parser.MultiStringParseToken.Optional;

import java.util.Set;
import java.util.TreeMap;

/**
 * Import a xMML file using the XOM library
 *
 * @author Joris Borgdorff
 */
public class XMMLDocumentImporter {
	private final static Logger logger = Logger.getLogger(XMMLDocumentImporter.class.getName());
	
	/** XOM parser that will be used */
	private Builder parser;
	
	/** Versions of the xMML format that are supported by this importer, inclusive. */
	private static final Version SUPPORTED_VERSIONS = new Version("0.1.x");
	
	/** Create a simple XOM parser to do the import */
	public XMMLDocumentImporter() {
		this.parser = new Builder();
	}
	
	/** Parse an xMML file using the XOM library */
	public MMLDocument parse(File f) throws ValidityException, ParsingException, IOException {
		return this.parse(this.parser.build(f));
	}
	
	/** Parse an xMML file from an input stream using the XOM library */
	public MMLDocument parse(InputStream stream) throws ValidityException, ParsingException, IOException {
		return this.parse(this.parser.build(stream));
	}
	
	/** Parse an xMML document using the XOM library */
	private MMLDocument parse(Document doc) {
		Element model = doc.getRootElement();
		Version xmmlVersion = new Version(model.getAttributeValue("xmml_version"));
		
		if (!SUPPORTED_VERSIONS.contains(xmmlVersion)) {
			throw new IllegalArgumentException("Document xMML format version " + xmmlVersion + "is not supported, only versions " +
					SUPPORTED_VERSIONS.versionString() + " are supported.");
		}
		ModelMetadata mm = this.parseModelMetadata(model);
		MMLDefinitions def = this.parseDefinitions(model.getFirstChildElement("definitions"));
		CouplingTopology ct = this.parseTopology(model.getFirstChildElement("topology"), def);
		
		return new MMLDocument(mm, def, ct, xmmlVersion);
	}
	
	/** Parses and returns the metadata of a model */
	private ModelMetadata parseModelMetadata(Element model) {
		String id = model.getAttributeValue("id");
		String name = model.getAttributeValue("name");
		Element eDescription = model.getFirstChildElement("description");
		String description = eDescription == null ? null : eDescription.getValue();
		
		String versionString = model.getAttributeValue("version");
		Version modelVersion = null;
		if (versionString != null) {
			modelVersion = new Version(versionString);
		}
		
		return new ModelMetadata(id, name, description, modelVersion);
	}
	
	private MMLDefinitions parseDefinitions(Element definition) {
		Map<String, Datatype> dtypes = parseDatatypes(definition.getChildElements("datatype"));
		Map<String, Converter> cverter = parseConverters(definition.getChildElements("converter"), dtypes);
		Map<String, Submodel> smodel = parseSubmodels(definition.getChildElements("submodel"), dtypes);
		return new MMLDefinitions(dtypes, cverter, smodel);
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
					logger.log(Level.WARNING, "size estimate of datatype ''{0}'' does not contain a valid size formula or size in bytes. Instead, it contains ''{1}''", new Object[]{id, size_estimate});
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
				logger.log(Level.WARNING, "converter ''{0}'' contains unknown data type ''{1}'' in its from-field and will not be processed", new Object[]{id, fromData});
				continue;
			}
			if (to == null) {
				logger.log(Level.WARNING, "converter ''{0}'' contains unknown data type ''{1}'' in its to-field and will not be processed", new Object[]{id, toData});
				continue;
			}
			if (id == null) {
				id = from + "2" + to;
				logger.log(Level.WARNING, "converter from ''{0}'' to ''{1}'' should have an id, using ''{2}'' as an id", new Object[]{from, to, id});
			}
			if (map.containsKey(id)) {
				logger.log(Level.WARNING, "converter with id ''{}'' already exists; skipping", id);
				continue;
			}
			
			Element requires = converter.getFirstChildElement("requires");
			if (requires != null) {
				String reqName = requires.getAttributeValue("name");
				String reqDataName = requires.getAttributeValue("datatype");
				Datatype reqData = datatypes.get(reqDataName);
				String src = requires.getAttributeValue("src");
				
				if (reqData == null) {
					logger.log(Level.WARNING, "requirement of converter ''{0}'' contains unknown data type ''{1}'' and will not be considered", new Object[]{id, reqDataName});
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
			
			ScaleParser scParser = new ScaleParser(meta.getId());
			scParser.parseElements(submodel.getChildElements("timescale"), Dimension.TIME);
			scParser.parseElements(submodel.getChildElements("spacescale"), Dimension.SPACE);
			scParser.parseElements(submodel.getChildElements("otherscale"), Dimension.OTHER);
			ScaleMap scales = scParser.getScaleMap();

			Element ports = submodel.getFirstChildElement("ports");
			Map<String, Port> in = parsePorts(ports.getChildElements("in"), true, datatypes);
			Map<String, Port> out = parsePorts(ports.getChildElements("out"), false, datatypes);
			
			Map<String,Param> params = parseParams(submodel.getChildElements("param")); 
			
			map.put(meta.getId(), new Submodel(meta, scales, in, out, params, initial, stateful, interactive));
		}
		
		return map;
	}
	
	/** Parse param elements */
	private Map<String, Param> parseParams(Elements params) {
		Map<String, Param> map = new HashMap<String, Param>();
		
		for (int i = 0; i < params.size(); i++) {
			Element param = params.get(i);
			String id = param.getAttributeValue("id");
			String value = param.getAttributeValue("value");
			map.put(id, new Param(id, value));
		}
		
		return map;
	}

	/** Parse in or out ports */
	private Map<String, Port> parsePorts(Elements ports, boolean in,
			Map<String, Datatype> datatypes) {
		Map<String, Port> map = new HashMap<String, Port>();
		
		for (int i = 0; i < ports.size(); i++) {
			Element port = ports.get(i);
			
			String id = port.getAttributeValue("id");
			SEL operator = SEL.valueOf(port.getAttributeValue("operator"));
			if ((in && operator.isSending()) || (!in && operator.isReceiving())) {
				String prefix = in ? " not" : "";
				logger.log(Level.WARNING, "port ''{0}'' contains the wrong type of operator for its type, it should{1} be able to send. This port will be disregarded.", new Object[]{id, prefix});
				continue;
			}
			String dataStr = port.getAttributeValue("datatype");
			Datatype datatype = null;
			if (dataStr != null) {
				datatype = datatypes.get(dataStr);
				if (datatype == null) {
					logger.log(Level.WARNING, "port ''{0}'' does not contain a valid datatype but ''{1}'' instead, the datatype will be disregarded", new Object[]{id, dataStr});
					continue;
				}
			}
			Port.Type state = port.getAttributeValue("type").equals("state") ? Port.Type.STATE : Port.Type.NORMAL;
			
			map.put(id, new Port(id, operator, datatype, state));
		}
		
		return map;
	}

	private CouplingTopology parseTopology(Element topology, MMLDefinitions definitions) {
		Map<String,Instance> instances = parseInstances(topology.getChildElements("instance"), definitions.getSubmodels());
		Collection<Coupling> couplings = parseCouplings(topology.getChildElements("coupling"), instances, definitions);
		return new CouplingTopology(instances, couplings);
	}

	/**
	 * @param childElements
	 * @param instances
	 * @return
	 */
	private Collection<Coupling> parseCouplings(Elements couplings,
			Map<String, Instance> instances, MMLDefinitions definitions) {
		Collection<Coupling> list = new ArrayList<Coupling>();
		
		for (int i = 0; i < couplings.size(); i++) {
			Element coupling = couplings.get(i);
			String name = coupling.getAttributeValue("name");
			InstancePort from = parseCouplingPort(coupling.getAttributeValue("from"), true, instances);
			InstancePort to = parseCouplingPort(coupling.getAttributeValue("to"), false, instances);
			
			if (from == null || to == null) {
				logger.warning("Coupling could not be properly parsed, skipping.");
				continue;
			}
			
			Datatype dfrom = from.getPort().getDatatype(), dto = to.getPort().getDatatype();
			List<Filter> filters = parseFilters(coupling.getChildElements("filter"), definitions, dfrom, dto, from, to);
			
			list.add(new Coupling(name, from, to, filters));
		}
		
		return list;
	}
	
	/** Parse filters of a conduit */
	private List<Filter> parseFilters(Elements filters, MMLDefinitions definitions, Datatype dfrom, Datatype dto, InstancePort from, InstancePort to) {
		List<Filter> list = new ArrayList<Filter>();
		Datatype tmpto = null;
		
		for (int i = 0; i < filters.size(); i++) {
			Element filter = filters.get(i);
			String name = filter.getAttributeValue("name");
			Filter.Type type = Filter.Type.valueOf(filter.getAttributeValue("type").toUpperCase());
			
			if (type == Filter.Type.CONVERTER && dfrom != null && dto != null) {
				Converter c = definitions.getConverter(name);
				if (c != null) {
					if (!c.getFrom().equals(dfrom)) {
						logger.log(Level.WARNING, "converter ''{0}'' in conduit from ''{1}'' to ''{2}'' does not convert from datatype ''{3}'' but is listed as a filter for that datatype", new Object[]{c.getId(), from, to, dfrom.getId()});
					}
					else {
						tmpto = c.getTo();
					}
				}
			}
			
			String scaleStr = filter.getAttributeValue("scale").toLowerCase();
			Dimension scale = null;
			if (scaleStr != null) {
				if (scaleStr.equals("time") || scaleStr.equals("temporal")) {
					scale = Dimension.TIME;
				}
				if (scaleStr.equals("space") || scaleStr.equals("spatial") || scaleStr.equals("length")) {
					scale = Dimension.SPACE;
				}
				else {
					scale = Dimension.OTHER;
				}
			}
			double factor = 1d;
			try {
				factor = Double.parseDouble(filter.getAttributeValue("factor"));
			} catch (NumberFormatException e) {
				logger.log(Level.WARNING, "factor of filter ''{0}'' in conduit from ''{1}'' to ''{2}'' should be a double but could not be parsed and will be ignored", new Object[]{name, from, to});
			}
			list.add(new Filter(name, type, scale, factor));
		}
		if (tmpto != null && to != null && !tmpto.equals(dto)) {
			logger.log(Level.WARNING, "list of converters in conduit from ''{0}'' to ''{1}'' does not convert from datatype ''{2}'' to datatype ''{3}'' but to datatype ''{4}'' instead", new Object[]{from, to, dfrom.getId(), dto.getId(), tmpto.getId()});
		}
		
		return list;
	}

	/** Parse the from or to port of a coupling */ 
	private InstancePort parseCouplingPort(String value, boolean from, Map<String, Instance> instances) {
		String[] id = value.split("\\.");
		if (id.length != 2) {
			String prefix = from ? "sending" : "receiving";
			logger.log(Level.WARNING, "{0} coupling port ''{1}'' can not be split into an instance name and port name", new Object[]{prefix, value});
			return null;
		}
		
		Instance inst = instances.get(id[0]);
		if (inst == null) {
			String prefix = from ? "sending" : "receiving";
			logger.log(Level.WARNING, "{0} coupling port ''{1}'' does not translate to a submodel instance", new Object[]{prefix, value});
			return null;
		}
		
		Submodel s = inst.getSubmodel();
		Port port = from ? s.getOutPort(id[1]) : s.getInPort(id[1]);
		if (port == null) {
			String prefix = from ? "sending" : "receiving";
			logger.log(Level.WARNING, "instance ''{0}'' does not have a {1} port named ''{2}'' for coupling", new Object[]{id[0], prefix, id[1]});
			return null;			
		}
		return new InstancePort(inst, port);
	}

	/**
	 * @param childElements
	 * @param submodels
	 * @return
	 */
	private Map<String, Instance> parseInstances(Elements instances, Map<String, Submodel> submodels) {
		Map<String, Instance> map = new HashMap<String, Instance>();
		Map<String, List<Domain>> domains = new TreeMap<String, List<Domain>>();
		int domainNum = 1;

		for (int i = 0; i < instances.size(); i++) {
			Element instance = instances.get(i);
			String id = instance.getAttributeValue("id");
			String subStr = instance.getAttributeValue("submodel");
			Submodel submodel = submodels.get(subStr);
			if (submodel == null) {
				logger.log(Level.WARNING, "instance with non-existant submodel ''{0}'' is excluded from the topology.", subStr);
				continue;
			}
			if (id == null) {
				id = submodel.getId();
			}
			if (map.containsKey(id)) {
				logger.log(Level.WARNING, "an instance with id ''{0}'' already exists, a duplicate will not be added", id);
				continue;
			}
			
			String domainStr = instance.getAttributeValue("domain");
			Domain domain = domainStr == null ? Domain.GENERIC : Domain.parseDomain(domainStr, domainNum, domains);
			if (domain.getNumber() >= domainNum) {
				domainNum = domain.getNumber() + 1;
			}
			
			String initialStr = instance.getAttributeValue("init");
			boolean initial = initialStr == null ? submodel.isInitial() : initialStr.equals("yes");

			ScaleParser scParser = new ScaleParser(id, submodel.getScaleMap());
			scParser.parseElements(instance.getChildElements("timescale"), Dimension.TIME);
			scParser.parseElements(instance.getChildElements("spacescale"), Dimension.SPACE);
			scParser.parseElements(instance.getChildElements("otherscale"), Dimension.OTHER);
			ScaleMap scales = scParser.getScaleMap();

			int num = map.size();
			map.put(id, new Instance(num, id, submodel, domain, initial, scales));
		}
		return map;
	}
}
