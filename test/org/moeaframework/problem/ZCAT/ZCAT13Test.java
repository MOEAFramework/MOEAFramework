package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.Vector;

public class ZCAT13Test {

	@Test
	public void test() {
		Problem problem = new ZCAT13(3);
		
		Assert.assertArrayEquals(new double[] { 1.562250, 6.085544, 15.750000 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.627477, 17.346272, 34.196120 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.169556, 16.086915, 40.608882 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.700372, 10.631518, 22.234614 }, 
				TestUtils.evaluateAt(problem, -0.308044, -0.167561, 0.794227, -0.259670, -1.158484, -1.594798, -0.383843, -1.656152, -3.597608, 0.386064, -4.530505, 0.943464, 4.829387, 1.206823, -5.838941, 4.246819, -4.630271, -8.301096, 4.691068, 3.304094, 4.479630, 7.271796, 3.767044, 4.819503, -4.450979, 4.831946, 3.926103, 7.897202, -12.317753, 8.057822).getObjectives(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		TestUtils.assertGeneratedSolutionsAreNondominated(new ZCAT13(3), 1000);
	}
	
	@Test
	public void testProvider() {
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem("ZCAT13_2"));
		Assert.assertNotNull(ProblemFactory.getInstance().getReferenceSet("ZCAT13_2"));
		Assert.assertEquals(2, ProblemFactory.getInstance().getProblem("ZCAT13_2").getNumberOfObjectives());
		
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem("ZCAT13_3"));
		Assert.assertNull(ProblemFactory.getInstance().getReferenceSet("ZCAT13_3"));
		Assert.assertEquals(3, ProblemFactory.getInstance().getProblem("ZCAT13_3").getNumberOfObjectives());
	}
	
}
