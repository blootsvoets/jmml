/**
 * 
 */
package eu.mapperproject.jmml.definitions;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import eu.mapperproject.jmml.definitions.Scale;
import eu.mapperproject.jmml.util.numerical.SIRange;
import eu.mapperproject.jmml.util.numerical.SIUnit;
import eu.mapperproject.jmml.util.numerical.ScaleModifier.Dimension;

/**
 * @author Joris Borgdorff
 *
 */
public class ScaleTest {
	@Test
	public void steps() {
		assertSame(1, new Scale("", Dimension.TIME, new SIRange(SIUnit.parseSIUnit("1 s")), false, new SIRange(SIUnit.parseSIUnit("1 s")), false, SIUnit.parseSIUnit("1 s")).getSteps());
		assertSame(3, new Scale("", Dimension.TIME, new SIRange(SIUnit.parseSIUnit("1 s")), false, new SIRange(SIUnit.parseSIUnit("3 s")), false, SIUnit.parseSIUnit("1 s")).getSteps());
	}
}
