package eu.mapperproject.jmml.specification;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import eu.mapperproject.jmml.specification.annotated.ObjectFactoryAnnotated;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import java.io.InputStream;
import javax.xml.bind.JAXBException;
import org.junit.Test;

/**
 *
 * @author jborgdo1
 */
public class UnmarshallingTest {
	@Test
	public void testUnmarshalling() throws JAXBException, FileNotFoundException {
		InputStream xmmlInputStream = new FileInputStream("/Users/bobby/Documents/dev/java/xmml-specification/src/test/resources/isr.xmml");
		
		JAXBContext context = JAXBContext.newInstance(Model.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setProperty("com.sun.xml.bind.ObjectFactory", new ObjectFactoryAnnotated());
		Object o = unmarshaller.unmarshal(xmmlInputStream);
		
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
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
