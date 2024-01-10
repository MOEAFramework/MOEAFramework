package org.moeaframework.problem.RWMOP;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class RCM02Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new RCM02();
		
		Assert.assertArrayEquals(new double[] { -0.0036491, 525.0 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 }, // Raw values: -1916.95000, -0.15000, 0.00000, 0.00000, -0.01000
				TestUtils.evaluateAtLowerBounds(problem).getConstraints(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { -0.0039348, 1980.0 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 2168.0, 0.0, 0.0, 0.0, 0.09 }, // Raw values: 2168.00000,  0.00000, -0.15000, -0.10000, 0.09000 
				TestUtils.evaluateAtUpperBounds(problem).getConstraints(),
				0.001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("RCM02", 2, false);
	}

}
