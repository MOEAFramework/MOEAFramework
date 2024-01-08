package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class ZCAT17Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT17(3);
		
		Assert.assertArrayEquals(new double[] { 1.769356, 6.913971, 11.411876 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.627477, 15.346272, 43.196120 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 5.169556, 18.086915, 31.608882 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.108118, 6.830303, 18.736357 }, 
				TestUtils.evaluateAt(problem, 0.381744, 0.192930, 1.018495, -1.452971, -0.971630, -0.921319, -0.817430, -2.590116, 1.340213, -0.114376, 2.687711, 5.123537, 5.051055, 5.277635, 4.373355, 0.175044, -6.607048, 6.106702, 8.399712, -1.592255, -6.735093, -3.489696, -0.935984, -8.406567, 4.803897, -11.030065, 5.831859, 11.660102, 12.901155, 6.516655).getObjectives(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		TestUtils.assertGeneratedSolutionsAreNondominated(new ZCAT17(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT17_2", 2, true);
		assertProblemDefined("ZCAT17_3", 3, false);
	}
	
}
