package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.util.Vector;

public class ZCAT8Test {

	@Test
	public void test() {
		Problem problem = new ZCAT8(3);
		
		Assert.assertArrayEquals(new double[] { 0.991129, 3.974731, 7.067086 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.500000, 14.000000, 31.500000 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.500056, 19.555805, 34.998878 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.791019, 6.084546, 12.705449 }, 
				TestUtils.evaluateAt(problem, 0.058101, -0.575384, 1.474225, -0.105137, 1.686453, 2.604903, 1.727306, 1.612908, 3.634956, 1.446326, 2.445413, -4.237059, -6.448302, 0.903883, -5.946247, 0.647537, -5.042570, 1.557467, 1.392931, 5.666681, 1.156338, 3.092158, 9.118774, 11.463569, 2.573770, 8.331361, -2.806535, 12.706957, 11.879237, 2.165165).getObjectives(),
				0.0001);
	}
	
}
