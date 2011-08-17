package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Model;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Joris Borgdorff
 */
public class AnnotatedModel extends Model {
	@Override
	public AnnotatedDefinitions getDefinitions() {
		return this.definitions;
	}
	
	@Override
	public AnnotatedTopology getTopology() {
		return this.topology;
	}
	
	public static AnnotatedModel getModel(File file) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Model.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setProperty("com.sun.xml.bind.ObjectFactory", new ObjectFactoryAnnotated());
		Object o = unmarshaller.unmarshal(file);
		return (AnnotatedModel)o;
	}
	
	public void export(File file) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Model.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl());
		marshaller.marshal(this, file);
	}
}
