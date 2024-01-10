package org.moeaframework.problem.RWMOP;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class RCM09Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new RCM09();
		
		Assert.assertArrayEquals(new double[] { 1400.0, 0.04 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.001);

		Assert.assertArrayEquals(new double[] { 3497.05627485, 0.01333333 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("RCM09", 2, false);
	}

}
