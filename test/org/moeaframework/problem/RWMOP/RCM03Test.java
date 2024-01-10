package org.moeaframework.problem.RWMOP;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class RCM03Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new RCM03();
		
		Assert.assertArrayEquals(new double[] { 0.000055373, 8246211.251235321 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 8146211.251235, 11213708.498985 }, // Raw values: -0.099945, 8146211.251235, 11213708.498985
				TestUtils.evaluateAtLowerBounds(problem).getConstraints(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 816.22777, 0.33333 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 816.12777, 0.0, 0.0 }, // Raw values: 816.12777, -99999.66667, -99999.15673
				TestUtils.evaluateAtUpperBounds(problem).getConstraints(),
				0.001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("RCM03", 2, false);
	}

}
