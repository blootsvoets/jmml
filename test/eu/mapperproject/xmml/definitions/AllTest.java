/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mapperproject.xmml.definitions;

import eu.mapperproject.xmml.topology.algorithms.AnnotationTest;
import eu.mapperproject.xmml.topology.algorithms.ProcessIterationTest;
import eu.mapperproject.xmml.topology.algorithms.TraceTest;
import eu.mapperproject.xmml.util.FormulaTest;
import eu.mapperproject.xmml.util.SIUnitTest;
import eu.mapperproject.xmml.util.VersionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author bobby
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ScaleTest.class, AnnotationTest.class, ProcessIterationTest.class, TraceTest.class, FormulaTest.class, SIUnitTest.class, VersionTest.class})
public class AllTest {
}