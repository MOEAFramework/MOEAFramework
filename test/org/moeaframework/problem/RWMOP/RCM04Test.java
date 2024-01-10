package org.moeaframework.problem.RWMOP;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class RCM04Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new RCM04();
		
		Assert.assertArrayEquals(new double[] { 0.010205, 17561.6 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 21886996.56568, 403170000.0, 0.0, 0.0 }, // Raw values: 21886996.56568, 403170000.00000, 0.00000, -4210.28732
				TestUtils.evaluateAtLowerBounds(problem).getConstraints(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 333.9095, 0.00043904 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.0, 0.0 }, // Raw values: -13425.50392, -28992.00000, 0.00000, -9206837.19700
				TestUtils.evaluateAtUpperBounds(problem).getConstraints(),
				0.001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("RCM04", 2, false);
	}

}
