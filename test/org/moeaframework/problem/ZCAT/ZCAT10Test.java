package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.util.Vector;

public class ZCAT10Test {

	@Test
	public void test() {
		Problem problem = new ZCAT10(3);
		
		Assert.assertArrayEquals(new double[] { 0.786754, 3.088933, 11.352262 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.566786, 2.519048, 13.911830 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.165194, 9.178640, 11.696120 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.738749, 3.582365, 13.877770 }, 
				TestUtils.evaluateAt(problem, 0.280879, 0.192992, 1.325509, -1.232875, -2.294169, 1.627938, 0.141870, 1.359980, 0.239838, -0.751369, 3.117022, -1.938914, -0.413250, 0.051893, -5.016322, -3.155558, 8.340104, 8.577536, 3.968839, -0.836990, -8.263584, 1.646909, 4.060898, 4.824122, 4.345153, 4.958540, 6.573738, -0.179425, -3.900610, -11.592669).getObjectives(),
				0.0001);
	}
	
}
