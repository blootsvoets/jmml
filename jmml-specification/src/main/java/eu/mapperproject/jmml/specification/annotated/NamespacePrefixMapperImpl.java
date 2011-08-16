package eu.mapperproject.jmml.specification.annotated;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import java.util.logging.Logger;

/**
 *
 * @author Joris Borgdorff
 */
public class NamespacePrefixMapperImpl extends NamespacePrefixMapper {

	@Override
	public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
		System.out.println(namespaceUri);
		//lets make the xmml namespace the default one
		if ("".equals(namespaceUri)) {
			Logger.getLogger(NamespacePrefixMapperImpl.class.getCanonicalName()).fine("Certain non-xMML elements are not marked as transient.");
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
