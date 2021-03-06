package eu.mapperproject.jmml.specification.annotated;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import java.util.logging.Logger;

/**
 * Prefixes xMML with xmml and MAD with mad; warns for non-namespaces elements.
 * @author Joris Borgdorff
 */
public class NamespacePrefixMapperImpl extends NamespacePrefixMapper {

	@Override
	public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
		//lets make the xmml namespace the default one
		if ("".equals(namespaceUri)) {
			Logger.getLogger(NamespacePrefixMapperImpl.class.getName()).fine("Certain non-xMML elements are not marked as transient.");
		}
		if ("http://www.mapper-project.eu/xmml".equals(namespaceUri)) {
			return "";
		}
		if ("http://www.mapper-project.eu/mad".equals(namespaceUri)) {
			return "mad";
		}
		return suggestion;
	}
	
}
