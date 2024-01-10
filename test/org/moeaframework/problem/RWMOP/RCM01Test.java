package org.moeaframework.problem.RWMOP;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class RCM01Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new RCM01();
		
		Assert.assertArrayEquals(new double[] { 12.401, -7330.383 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 0.032900, 0.130500 }, 
				TestUtils.evaluateAtLowerBounds(problem).getConstraints(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 692489.95664, -58643062.86701 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0 }, // Raw values: -4.2795, -2.3275 
				TestUtils.evaluateAtUpperBounds(problem).getConstraints(),
				0.001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("RCM01", 2, false);
	}

}
