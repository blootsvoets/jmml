package eu.mapperproject.xmml.topology.algorithms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import eu.mapperproject.xmml.topology.algorithms.CouplingDescription.CouplingType;
import eu.mapperproject.xmml.topology.algorithms.graph.PTGraph;
import eu.mapperproject.xmml.topology.algorithms.io.GraphToGraphvizExporter;

public class ModelManager {
	private final ModelDescription model;
	private final Map<GraphType, PTGraph> graphMap;
	private GraphToGraphvizExporter exporter;

	public ModelManager(ModelDescription model) {
		this.model = model;
		this.graphMap = new EnumMap<GraphType, PTGraph>(GraphType.class);
		this.exporter = new GraphToGraphvizExporter(false, false, false); 
	}
	
	public enum GraphType {
		DOMAIN, TASK, TOPOLOGY;
	}
	
	public void setExporter(GraphToGraphvizExporter e) {
		this.exporter = e;
	}
	
	private static ModelManager isrModel() {
		// Define domains
		Domain artery = new Domain("Artery");
		Domain blood = artery.getChild("Blood");
		Domain tissue = artery.getChild("Tissue");
		
		// BF and DD can be used multiple times
		ProcessDescription bfDesc = new ProcessDescription("BF", 1, true);
		ProcessDescription ddDesc = new ProcessDescription("DD", 1, false);
		
		// Define the process and right after reference it with a name to a domain.
		List<ProcessReference> pd = new ArrayList<ProcessReference>(8);
		ProcessReference init = new ProcessDescription("Deploy").getReference(artery, true);
		//ProcessReference bfinit = bfDesc.getReference(blood, "BF_init");
		ProcessReference thrombos = new ProcessDescription("Thrombus").getReference(artery, true);
		ProcessReference smc = new ProcessDescription("SMC", 3, false).getReference(tissue, false);
		ProcessReference bf = bfDesc.getReference(blood, true);
		ProcessReference dd = ddDesc.getReference(tissue, true);
//		ProcessReference ddsub = new ProcessDescription("DDsub").getReference(tissue);
		
		pd.add(init); pd.add(smc); pd.add(thrombos); pd.add(bf); pd.add(dd);// pd.add(ddsub);
		
		// Thrombos is a special case
		//CouplingDescription thrombos2bf = new CouplingDescription(thrombos, CouplingType.OF, bf, CouplingType.FINIT, 1);
		//thrombos2bf.addFollowingAnnotation(AnnotationType.INSTANCE);
		
		List<CouplingDescription> cd = new ArrayList<CouplingDescription>(4);
		cd.add(new CouplingDescription("geometry", init, CouplingType.OF, thrombos, CouplingType.FINIT, 1));
//		cd.add(new CouplingDescription(init, CouplingType.OF, bfinit, CouplingType.FINIT, 1));
//		cd.add(new CouplingDescription(bfinit, CouplingType.OF, thrombos, CouplingType.FINIT, 1));
//		cd.add(new CouplingDescription(bfinit, CouplingType.OF, bf, CouplingType.FINIT, 1));
		//cd.add(thrombos2bf);
		//cd.add(new CouplingDescription(thrombos, CouplingType.OF, dd, CouplingType.FINIT, 1));
//		cd.add(new CouplingDescription(dd, CouplingType.OI, ddsub, CouplingType.FINIT, 2));
//		cd.add(new CouplingDescription(ddsub, CouplingType.OF, dd, CouplingType.C, -1));
		cd.add(new CouplingDescription("geometry", thrombos, CouplingType.OF, smc, CouplingType.FINIT, 1));
		cd.add(new CouplingDescription("shear stress", bf, CouplingType.OF, smc, CouplingType.B, 1));
		cd.add(new CouplingDescription("drug diffusion",dd, CouplingType.OF, smc, CouplingType.C, 1));
		cd.add(new CouplingDescription("boundary",smc, CouplingType.OI, bf, CouplingType.FINIT, 1));
		cd.add(new CouplingDescription("geometry",smc, CouplingType.OI, dd, CouplingType.FINIT, 1));
		
		return new ModelManager(new ModelDescription("ISR3D", pd, cd));
	}
	
	private static ModelManager canalsV1() {
		// Define domains
		Domain csystem = new Domain("Canal system");
		Domain cd1 = csystem.getChild("Canal1");
		Domain cd2 = csystem.getChild("Canal2");
		Domain cd3 = csystem.getChild("Canal3");
		
		// BF and DD can be used multiple times
		ProcessDescription cDesc = new ProcessDescription("Canal", 4, false);
		ProcessDescription jDesc = new ProcessDescription("Junction", 4, false);
		
		// Define the process and right after reference it with a name to a domain.
		List<ProcessReference> pd = new ArrayList<ProcessReference>(8);
		ProcessReference init = new ProcessDescription("Init", 1, false).getReference(csystem, true);
		ProcessReference c1 = cDesc.getReference(cd1, "C1", false);
		ProcessReference c2 = cDesc.getReference(cd2, "C2", false);
		ProcessReference c3 = cDesc.getReference(cd3, "C3", false);
		ProcessReference j = jDesc.getReference(csystem, "J", false);
		
		pd.add(init); pd.add(c1); pd.add(c2); pd.add(c3); pd.add(j);
				
		List<CouplingDescription> cd = new ArrayList<CouplingDescription>(6);
		cd.add(new CouplingDescription(init, CouplingType.OF, c1, CouplingType.FINIT, 1));
		cd.add(new CouplingDescription(init, CouplingType.OF, c2, CouplingType.FINIT, 1));
		cd.add(new CouplingDescription(init, CouplingType.OF, c3, CouplingType.FINIT, 1));
		cd.add(new CouplingDescription(init, CouplingType.OF, j, CouplingType.FINIT, 1));
		cd.add(new CouplingDescription(c1, CouplingType.OI, j, CouplingType.C, 1));
		cd.add(new CouplingDescription(c2, CouplingType.OI, j, CouplingType.C, 1));
		cd.add(new CouplingDescription(c3, CouplingType.OI, j, CouplingType.C, 1));
		cd.add(new CouplingDescription(j, CouplingType.OI, c1, CouplingType.C, 1));
		cd.add(new CouplingDescription(j, CouplingType.OI, c2, CouplingType.C, 1));
		cd.add(new CouplingDescription(j, CouplingType.OI, c3, CouplingType.C, 1));
		
		return new ModelManager(new ModelDescription("Canals", pd, cd));
	}
	
	public void export(GraphType gt, String dotStr, String pdfStr) {
		PTGraph graph = this.getGraph(gt);
		
		System.out.println("Exporting graphviz file...");

		File dot = new File(dotStr);
		File pdf = new File(pdfStr);
		
		try {
			exporter.export(graph, dot, pdf);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(2);
		}
		
		System.out.println("Done.");		
	}
	
	public void print(GraphType gt) {
		exporter.print(this.getGraph(gt));
	}
	
	public PTGraph getGraph(GraphType gt) {
		PTGraph graph = this.graphMap.get(gt);
		if (graph == null) {
			switch (gt) {
			case DOMAIN:
				graph = this.getGraph(GraphType.TOPOLOGY);
				graph = new PTGraph(graph.partition());
				break;
			case TASK:
				graph = new TaskGraph(this.model).getGraph();
				break;
			default:
				graph = new CouplingTopology(this.model).getGraph();
				break;
			}
			
			this.graphMap.put(gt, graph);
		}
		
		return graph;
	}
	
	public static void main(String[] args) {
		ModelManager manager = isrModel(); //canalsV1(); //

		if (args.length == 2) {
			manager.export(GraphType.TASK, args[0], args[1]);
			manager.export(GraphType.TOPOLOGY, args[0] + "_topology.dot", args[1] + "_topology.pdf");
		}
		else {
			manager.print(GraphType.TASK);
		}
	}
}
