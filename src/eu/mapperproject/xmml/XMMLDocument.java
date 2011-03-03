package eu.mapperproject.xmml;

import java.io.File;
import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;

import eu.mapperproject.xmml.definitions.XMMLDefinitions;
import eu.mapperproject.xmml.io.XMMLDocumentImporter;
import eu.mapperproject.xmml.topology.CouplingTopology;
import eu.mapperproject.xmml.util.Version;

/**
 * An xMML document
 * @author Joris Borgdorff
 *
 */
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
	
	public static void main(String[] args) {
		try {
			new XMMLDocumentImporter().parse(new File("/Users/jborgdo1/Desktop/isr_test.xmml"));
		} catch (ValidityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
