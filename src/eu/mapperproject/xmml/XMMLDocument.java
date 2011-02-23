package eu.mapperproject.xmml;

import eu.mapperproject.xmml.definitions.XMMLDefinitions;
import eu.mapperproject.xmml.topology.CouplingTopology;
import eu.mapperproject.xmml.util.Version;

public class XMMLDocument {
	private final ModelMetadata model;
	private final Version xmmlVersion;
	
	private final XMMLDefinitions definitions;
	private final CouplingTopology topology;
	
	public XMMLDocument(ModelMetadata model, XMMLDefinitions definitions, CouplingTopology topology, Version xmmlVersion) {
		this.model = model;
		this.definitions = definitions;
		this.topology = topology;
		this.xmmlVersion = xmmlVersion;
	}
}
