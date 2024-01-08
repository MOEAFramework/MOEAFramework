package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class ZCAT19Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT19(3);
		
		Assert.assertArrayEquals(new double[] { 0.723625, 2.909376, 6.599244 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.861739, 3.448851, 17.617395 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.311732, 5.385474, 3.163834 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.341549, 4.803322, 11.221287 }, 
				TestUtils.evaluateAt(problem, 0.069063, 0.888403, -0.142894, 0.332472, 1.271532, -1.378094, 1.226241, 3.069677, 1.344249, 0.671420, 1.862202, -0.844530, -4.110115, 0.804700, 5.318304, 6.902717, -7.957520, 1.741306, -4.361944, 7.756119, -0.100005, 5.782094, -2.517433, -5.244251, -5.103976, -1.175182, -10.611274, -13.185982, -2.746244, -9.786713).getObjectives(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		TestUtils.assertGeneratedSolutionsAreNondominated(new ZCAT19(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT19_2", 2, true);
		assertProblemDefined("ZCAT19_3", 3, false);
	}
	
}
