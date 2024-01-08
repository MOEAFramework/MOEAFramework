package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.Vector;

public class ZCAT14Test {

	@Test
	public void test() {
		Problem problem = new ZCAT14(3);
		
		Assert.assertArrayEquals(new double[] { 0.723625, 2.909376, 8.463205 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.861739, 3.448851, 17.617395 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.325705, 5.288008, 3.257053 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.259974, 3.173026, 19.002661 }, 
				TestUtils.evaluateAt(problem, 0.194864, -0.388135, 1.034028, -1.696126, 0.166230, 2.093021, -1.253685, 3.322547, 1.445646, 4.918825, 0.091900, 1.417759, -4.368520, -0.457188, 4.105505, -2.636894, -8.494194, -5.052289, 1.413904, 3.830699, 6.311318, -4.401094, 10.829250, -3.598686, 2.051302, 0.226811, 1.552274, -3.644900, -9.383884, 5.998858).getObjectives(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		TestUtils.assertGeneratedSolutionsAreNondominated(new ZCAT14(3), 1000);
	}
	
	@Test
	public void testProvider() {
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem("ZCAT14_2"));
		Assert.assertNotNull(ProblemFactory.getInstance().getReferenceSet("ZCAT14_2"));
		Assert.assertEquals(2, ProblemFactory.getInstance().getProblem("ZCAT14_2").getNumberOfObjectives());
		
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem("ZCAT14_3"));
		Assert.assertNull(ProblemFactory.getInstance().getReferenceSet("ZCAT14_3"));
		Assert.assertEquals(3, ProblemFactory.getInstance().getProblem("ZCAT14_3").getNumberOfObjectives());
	}
	
}
