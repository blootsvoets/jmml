/**
 * 
 */
package eu.mapperproject.jmml.specification;

import eu.mapperproject.jmml.specification.annotated.AnnotatedScale;
import eu.mapperproject.jmml.specification.annotated.AnnotatedUnit;
import static org.junit.Assert.assertSame;
import org.junit.Test;

/**
 * @author Joris Borgdorff
 *
 */
public class ScaleTest {
	@Test
	public void steps() {
		AnnotatedScale sc = new AnnotatedScale();
		Unit u1 = new AnnotatedUnit(), u2 = new AnnotatedUnit();
		u1.setValue("1 s");
		
		sc.setRegularDelta(u1); sc.setRegularTotal(u1);		
		
		assertSame(1, sc.getSteps());

		u2.setValue("3 s");
		sc.setRegularTotal(u2);
		assertSame(3, sc.getSteps());
	}
}
