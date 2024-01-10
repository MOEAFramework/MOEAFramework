package org.moeaframework.problem.RWMOP;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class RCM08Test extends ProblemTest {
	
	@Test
	public void test() {
		// These test cases use the Carside implementation from https://github.com/msu-coinlab/pymop because
		// the RWMOP source implementation is wrong.  Note that constraint values do not match exactly due to how they
		// are scaled, so we only check feasibility.
		Problem problem = new RCM08();
		
		Assert.assertArrayEquals(new double[] { 15.576004, 4.427250, 13.091381 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertTrue(TestUtils.evaluateAtLowerBounds(problem).violatesConstraints());
		
		Assert.assertArrayEquals(new double[] { 42.768012, 3.585250, 10.610644 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertFalse(TestUtils.evaluateAtUpperBounds(problem).violatesConstraints());
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("RCM08", 3, false);
	}

}
