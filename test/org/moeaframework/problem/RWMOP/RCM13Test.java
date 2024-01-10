package org.moeaframework.problem.RWMOP;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class RCM13Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new RCM13();
		
		Assert.assertArrayEquals(new double[] { 2352.346, 1695.964, 1004.658 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 0.0091352780, 0.0002002953, 0.0, 0.0, 0.0, 0.0, 1.2857142857, 0.0, 0.1000000000, 395.9638774581, 0.0 }, // Raw values: 0.0091352780, 0.0002002953, -0.0559350490, -0.4658299083, -28.1000000000, -8.2857142857, 1.2857142857, -1.0500000000, 0.1000000000, 395.9638774581, -95.3424802877
				TestUtils.evaluateAtLowerBounds(problem).getConstraints(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 7144.6950, 694.5867, 754.4965 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,  }, // Raw values: -0.021536045, -0.001962116, -0.407796023, -0.490239128, -17.600000000, -7.500000000, 0.500000000, -0.550000000, -0.350000000, -605.413304647, -345.503468945
				TestUtils.evaluateAtUpperBounds(problem).getConstraints(),
				0.001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("RCM13", 3, false);
	}

}
