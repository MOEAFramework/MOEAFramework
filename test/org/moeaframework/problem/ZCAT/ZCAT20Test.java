package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class ZCAT20Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT20(3);
		
		Assert.assertArrayEquals(new double[] { 1.840315, 6.845845, 15.193698 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.179539, 14.131285, 40.409218 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.501091, 19.560405, 34.978177 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.755148, 5.612683, 26.130553 }, 
				TestUtils.evaluateAt(problem, 0.282270, 0.816827, 0.340810, -0.903613, -1.235864, 2.196344, 1.042931, 2.790136, -3.329406, 4.530856, -1.162009, -5.322769, 3.415518, -0.445302, -0.921937, -3.035557, 5.749473, 3.760910, -1.231435, -9.856307, 7.135791, -1.939915, 7.305066, 4.073053, -0.949078, -12.501428, 0.693517, -3.202567, -12.044570, 1.963300).getObjectives(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		TestUtils.assertGeneratedSolutionsAreNondominated(new ZCAT20(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT20_2", 2, true);
		assertProblemDefined("ZCAT20_3", 3, false);
	}
	
}
