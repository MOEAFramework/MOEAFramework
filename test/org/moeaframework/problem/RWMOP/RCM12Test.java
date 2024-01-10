package org.moeaframework.problem.RWMOP;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class RCM12Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new RCM12();
		
		Assert.assertArrayEquals(new double[] { 25.38000, 12.04202 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 428.3182 },
				TestUtils.evaluateAtLowerBounds(problem).getConstraints(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 850.0, 0.005902607 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 0.0 }, // Raw values: -13.98755
				TestUtils.evaluateAtUpperBounds(problem).getConstraints(),
				0.001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("RCM12", 2, false);
	}

}
