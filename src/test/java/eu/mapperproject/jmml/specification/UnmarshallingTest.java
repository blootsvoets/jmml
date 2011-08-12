package eu.mapperproject.jmml.specification;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import java.io.InputStream;
import javax.xml.bind.JAXBException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jborgdo1
 */
public class UnmarshallingTest {
	@Test
	public void testUnmarshalling() throws JAXBException {
		InputStream xmmlInputStream = UnmarshallingTest.class.getClassLoader().getResourceAsStream("isr.xmml");
		
		JAXBContext context = JAXBContext.newInstance(Model.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Object o = unmarshaller.unmarshal(xmmlInputStream);
		
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(o, new File("marshalled.xml"));
	}
	
	public static void main(String[] args) {
		try {
			new UnmarshallingTest().testUnmarshalling();
		} catch (JAXBException ex) {
			Logger.getLogger(UnmarshallingTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
