package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.util.Vector;

public class ZCAT3Test {

	@Test
	public void test() {
		Problem problem = new ZCAT3(3);
		
		Assert.assertArrayEquals(new double[] { 0.576915, 2.317877, 5.203125 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.500000, 12.000000, 31.500000 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.500056, 17.555805, 34.998878 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.285791, 3.644319, 7.052878 }, 
				TestUtils.evaluateAt(problem, 0.199976, 0.864537, 0.277067, 1.123872, 2.026562, 1.418068, 1.409816, -1.805995, 3.701700, 3.439141, 1.370076, -4.062809, -2.375099, -1.875144, 0.882099, 4.580727, -0.231853, -8.675746, -0.664547, -3.070162, -2.617749, -1.865204, -1.463649, 0.509115, 9.378694, -5.804617, -4.640183, 3.301225, 13.004847, 2.888292).getObjectives(),
				0.0001);
	}
	
}
