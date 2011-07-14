package eu.mapperproject.jmml.topology.algorithms;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import eu.mapperproject.jmml.ModelMetadata;
import eu.mapperproject.jmml.definitions.Port;
import eu.mapperproject.jmml.definitions.Scale;
import eu.mapperproject.jmml.definitions.ScaleSet;
import eu.mapperproject.jmml.definitions.Submodel;
import eu.mapperproject.jmml.definitions.Submodel.SEL;
import eu.mapperproject.jmml.topology.Coupling;
import eu.mapperproject.jmml.topology.Domain;
import eu.mapperproject.jmml.topology.Instance;
import eu.mapperproject.jmml.topology.InstancePort;
import eu.mapperproject.jmml.util.numerical.SIRange;
import eu.mapperproject.jmml.util.numerical.SIUnit;
import eu.mapperproject.jmml.util.numerical.ScaleModifier.Dimension;

public class ProcessIterationTest {
	
	private Coupling cdBasic2other, cdOther2basic, cdOther2other;
	private ProcessIteration piBasic1, piBasic2, piBasicz, piOther;
	
	@Before
	public void setUp() throws Exception {
		Domain d = new Domain(1, "bas");
		ScaleSet scales = new ScaleSet();
		scales.putScale(new Scale("t", Dimension.TIME, new SIRange(SIUnit.parseSIUnit("1 s")), true, new SIRange(SIUnit.parseSIUnit("4 s")), true, SIUnit.parseSIUnit("1 s")));
		ScaleSet scaleOther = new ScaleSet();
		scaleOther.putScale(new Scale("t", Dimension.TIME, new SIRange(SIUnit.parseSIUnit("1 s")), true, new SIRange(SIUnit.parseSIUnit("1 s")), true, SIUnit.parseSIUnit("1 s")));
		
		Submodel sbbasz = new Submodel(new ModelMetadata("basicz", null, null, null), null, null, null, null, false, null, null);
		Submodel sbbas = new Submodel(new ModelMetadata("basic", null, null, null), null, null, null, null, false, null, null);
		Submodel sboth = new Submodel(new ModelMetadata("other", null, null, null), null, null, null, null, false, null, null);
		
		Instance pdBasicz = new Instance(1, "basicz", sbbasz, d, false, scaleOther);
		Instance pdBasic = new Instance(2, "basic", sbbas, d, false, scales);
		Instance pdOther = new Instance(3, "other", sboth, d, false, scales);

		Port in = new Port("in", SEL.finit, null, Port.Type.NORMAL);
		Port out = new Port("out", SEL.Of, null, Port.Type.NORMAL);
		cdBasic2other = new Coupling(null, new InstancePort(pdBasic, out), new InstancePort(pdOther, in), null);
		cdOther2basic = new Coupling(null, new InstancePort(pdOther, out), new InstancePort(pdBasic, in), null);
		cdOther2other = new Coupling(null, new InstancePort(pdOther, out), new InstancePort(pdOther, in), null);

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
		ProcessIteration out;
		assertSame(SEL.finit, piBasicz.getOperator());
		
		out = piBasicz.nextStep();
		assertSame(null, out);
		assertSame(SEL.Oi, piBasicz.getOperator());
		
		out = piBasicz.nextStep();
		assertSame(null, out);
		assertSame(SEL.S, piBasicz.getOperator());
		
		out = piBasicz.nextStep();
		assertSame(null, out);
		assertSame(SEL.B, piBasicz.getOperator());

		out = piBasicz.nextStep();
		assertSame(null, out);
		assertSame(SEL.Of, piBasicz.getOperator());
		assertTrue(piBasicz.instanceCompleted());
		// Error
		out = piBasicz.nextStep();
	}
}
