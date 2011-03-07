package eu.mapperproject.xmml.topology.algorithms;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import eu.mapperproject.xmml.ModelMetadata;
import eu.mapperproject.xmml.definitions.Port;
import eu.mapperproject.xmml.definitions.Scale;
import eu.mapperproject.xmml.definitions.ScaleMap;
import eu.mapperproject.xmml.definitions.Submodel;
import eu.mapperproject.xmml.definitions.Submodel.SEL;
import eu.mapperproject.xmml.topology.Coupling;
import eu.mapperproject.xmml.topology.Domain;
import eu.mapperproject.xmml.topology.Instance;
import eu.mapperproject.xmml.topology.InstancePort;
import eu.mapperproject.xmml.util.SIRange;
import eu.mapperproject.xmml.util.SIUnit;
import eu.mapperproject.xmml.util.ScaleModifier.Dimension;

public class ProcessIterationTest {
	
	private Coupling cdBasic2other, cdOther2basic, cdOther2other;
	private ProcessIteration piBasic1, piBasic2, piBasicz, piOther;
	private Coupling cdBasicz2basicz;

	@Before
	public void setUp() throws Exception {
		Domain d = new Domain("bas");
		ScaleMap scales = new ScaleMap();
		scales.putScale(new Scale("t", Dimension.TIME, new SIRange(SIUnit.parseSIUnit("1 s")), true, new SIRange(SIUnit.parseSIUnit("4 s")), true));
		ScaleMap scaleOther = new ScaleMap();
		scaleOther.putScale(new Scale("t", Dimension.TIME, new SIRange(SIUnit.parseSIUnit("1 s")), true, new SIRange(SIUnit.parseSIUnit("1 s")), true));
		
		Submodel sbbasz = new Submodel(new ModelMetadata("basicz", null, null, null), null, null, null, null, false, null, null);
		Submodel sbbas = new Submodel(new ModelMetadata("basic", null, null, null), null, null, null, null, false, null, null);
		Submodel sboth = new Submodel(new ModelMetadata("other", null, null, null), null, null, null, null, false, null, null);
		
		Instance pdBasicz = new Instance("basicz", sbbasz, d, false, scaleOther);
		Instance pdBasic = new Instance("basic", sbbas, d, false, scales);
		Instance pdOther = new Instance("other", sboth, d, false, scales);
		
		cdBasicz2basicz = new Coupling(null, new InstancePort(pdBasicz, new Port("out", SEL.Of, null, null)), new InstancePort(pdBasicz, new Port("in", SEL.finit, null, null)), null);
		cdBasic2other = new Coupling(null, new InstancePort(pdBasic, new Port("out", SEL.Of, null, null)), new InstancePort(pdOther, new Port("in", SEL.finit, null, null)), null);
		cdOther2basic = new Coupling(null, new InstancePort(pdOther, new Port("out", SEL.Of, null, null)), new InstancePort(pdBasic, new Port("in", SEL.finit, null, null)), null);
		cdOther2other = new Coupling(null, new InstancePort(pdOther, new Port("out", SEL.Of, null, null)), new InstancePort(pdOther, new Port("in", SEL.finit, null, null)), null);

		piBasic1 = new ProcessIteration(pdBasic);
		piBasic2 = new ProcessIteration(pdBasic);
		piOther = new ProcessIteration(pdOther);
		piBasicz = new ProcessIteration(pdBasicz);
	}
	
	@Test
	public void eq() {
		assertEquals(piBasic1, piBasic2);
		assertFalse(piBasic1.equals(piOther));
	}
	
	@Test
	public void instanceOrderIndifferent() {
		piBasic1 = piBasic1.nextInstance(cdBasic2other);
		piBasic1 = piBasic1.nextInstance(cdOther2other);
		piOther = piOther.nextInstance(cdOther2basic);
		piOther = piOther.nextInstance(cdBasic2other);
		assertEquals(piBasic1, piOther);
	}

	@Test(expected= IllegalStateException.class)
	public void completion() {
		// Oi
		piBasicz = piBasicz.nextStep();
		// B
		piBasicz = piBasicz.nextStep();
		// S
		piBasicz = piBasicz.nextStep();
		// Of
		piBasicz = piBasicz.nextStep();
		assertTrue(piBasicz.instanceCompleted());
		// Error
		piBasicz = piBasicz.nextStep();
	}
}
