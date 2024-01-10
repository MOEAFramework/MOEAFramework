package org.moeaframework.problem.RWMOP;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class RCM06Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new RCM06();
		
		Assert.assertArrayEquals(new double[] { 2352.346, 1695.964 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 0.00913528, 0.00020030, 0.0, 0.0, 0.0, 0.0, 1.28571429, 0.0, 0.1, 395.96387746, 154.65751971 }, // Raw values: 0.00913528, 0.00020030, -0.05593505, -0.46582991, -28.10000000, -8.28571429, 1.28571429, -1.05000000, 0.10000000, 395.96387746, 154.65751971
				TestUtils.evaluateAtLowerBounds(problem).getConstraints(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 7144.6950, 694.5867 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5000000, 0.0, 0.0, 0.0, 0.0 }, // Raw values: -0.0215360, -0.0019621, -0.4077960, -0.4902391, -17.6000000, -7.5000000, 0.5000000, -0.5500000, -0.3500000, -605.4133046, -95.5034689
				TestUtils.evaluateAtUpperBounds(problem).getConstraints(),
				0.001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("RCM06", 2, false);
	}

}
