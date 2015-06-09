/*
 * 
 */
package eu.mapperproject.jmml.io;

import eu.mapperproject.jmml.specification.*;
import eu.mapperproject.jmml.specification.Mapper.Type;
import eu.mapperproject.jmml.specification.annotated.AnnotatedCoupling;
import eu.mapperproject.jmml.specification.annotated.AnnotatedDefinition;
import eu.mapperproject.jmml.specification.annotated.AnnotatedDefinitions;
import eu.mapperproject.jmml.specification.annotated.AnnotatedFilter;
import eu.mapperproject.jmml.specification.annotated.AnnotatedInstance;
import eu.mapperproject.jmml.specification.annotated.AnnotatedPort;
import eu.mapperproject.jmml.specification.annotated.AnnotatedTopology;
import eu.mapperproject.jmml.util.ArrayMap;
import eu.mapperproject.jmml.util.ArraySet;
import eu.mapperproject.jmml.util.FastArrayList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 *
 * @author Joris Borgdorff
 */
public class MUSCLEJavaExporter extends AbstractExporter {
	private final static Logger log = Logger.getLogger(MUSCLEJavaExporter.class.getName());
	private final AnnotatedDefinition def;
	private final Map<String,Datatype> datatypes;
	
	public MUSCLEJavaExporter(AnnotatedDefinition def, List<Datatype> datatypes) {
		this.def = def;
		this.datatypes = new HashMap<String,Datatype>();
		for (Datatype datatype : datatypes) {
			this.datatypes.put(datatype.getId(), datatype);
		}
	}
	
	@Override
	protected void convert() throws IOException {
		log.info("Generating muscle snippet");
		final STGroup stg = new STGroupFile("muscle_java.stg");
		
		ST st;
		Map<String,Datatype> chooseData = new HashMap<String,Datatype>();
		List<Param> params = null;
		
		if (this.def instanceof Submodel) {
			st = stg.getInstanceOf("submodel");
			
			Submodel model = (Submodel)this.def;
			params = model.getParam();			
			boolean hasState = false;
			
			for (JAXBElement<Port> port : model.getPorts().getInOrOut()) {
				Port p = port.getValue();
				if (p.getType() == Port.Type.STATE) {
					if (!hasState) {
						st.add("state", p);
						hasState = true;
					}
					continue;
				}
				Datatype datatype = datatypes.get(p.getDatatype());
				chooseData.put(datatype.getId(), datatype);
//				((AnnotatedPort)p).setDataclass(datatype.getClazzName());
				
				switch (p.getOperator()) {
					case OI:
						st.add("oiports", p);
						break;
					case OF:
						st.add("ofports", p);
						break;
					case FINIT:
						st.add("finitports", p);
						break;
					case S: case B:
						st.add("sports", p);
						break;
					default:
						log.log(Level.WARNING, "Operator {0} for port {1} not supported.", new Object[]{p.getOperator(), p});
						break;
				}
			}
		} else if (this.def instanceof Mapper) {
			Mapper map = (Mapper)this.def;
			if (map.getType() == Type.FAN_IN) {
				st = stg.getInstanceOf("faninmapper");
			} else if (map.getType() == Type.FAN_OUT) {
				st = stg.getInstanceOf("fanoutmapper");
			} else {
				st = stg.getInstanceOf("mapper");
			}
			for (JAXBElement<Port> port : map.getPorts().getInOrOut()) {
				Port p = port.getValue();
				
				Datatype datatype = datatypes.get(p.getDatatype());
				chooseData.put(datatype.getId(), datatype);
//				((AnnotatedPort)p).setDataclass(datatype.getClazzName());
				
				if (port.getName().getLocalPart().equals("in")) {
					st.add("inports", p);
				} else {
					st.add("outports", p);
				}
			}
		} else if (this.def instanceof Filter) {
			st = stg.getInstanceOf("filter");
			AnnotatedFilter f = (AnnotatedFilter)def;
			st.add("datatype_in", f.getDatatypeInInstance());
		} else {
			return;
		}
		
//		st.add("pkg", def.getPackage());
//		st.add("cls", def.getClazzName());
		st.add("params", params);
		st.add("datatypes", chooseData.values());

		this.out.write(st.render());
	}
	

}
