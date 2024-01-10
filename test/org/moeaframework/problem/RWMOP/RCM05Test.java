package org.moeaframework.problem.RWMOP;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class RCM05Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new RCM05();
		
		Assert.assertArrayEquals(new double[] { 1.2740, 9.0845 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.0, 0.0 }, // Raw values: 0.00000, -0.27751, -0.91609, -27853.57692
				TestUtils.evaluateAtLowerBounds(problem).getConstraints(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 5.3067, 1.1391 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.0, 0.0 }, // Raw values: -10.00000, -0.23238, -0.83212, -228420.00000
				TestUtils.evaluateAtUpperBounds(problem).getConstraints(),
				0.001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("RCM05", 2, false);
	}

}
