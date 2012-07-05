package eu.mapperproject.jmml.specification;

import eu.mapperproject.jmml.specification.annotated.NamespacePrefixMapperImpl;
import eu.mapperproject.jmml.specification.annotated.ObjectFactoryAnnotated;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.junit.Test;

/**
 *
 * @author Joris Borgdorff
 */
public class UnmarshallingTest {
	@Test
	public void testUnmarshalling() throws JAXBException, FileNotFoundException {
		InputStream xmmlInputStream = new FileInputStream("src/test/resources/isr.xmml");
		
		JAXBContext context = JAXBContext.newInstance(Model.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setProperty("com.sun.xml.bind.ObjectFactory", new ObjectFactoryAnnotated());
		Object o = unmarshaller.unmarshal(xmmlInputStream);
		
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl());
		marshaller.marshal(o, new File("marshalled.xml"));
	}
	
	public static void main(String[] args) {
		try {
			new UnmarshallingTest().testUnmarshalling();
		} catch (Exception ex) {
			Logger.getLogger(UnmarshallingTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
