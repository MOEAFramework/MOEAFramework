package org.moeaframework.problem.RWMOP;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

public class RCM11Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new RCM11();
		
		Assert.assertArrayEquals(new double[] { 63840.2774000, 30.0000000, 285346.8964942, 6575303.1262349, 346735.0000000 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 12.869400, 1.972220, 77615.102400, 4363.753300, 10753.793900, 0.0, 1041.831300 }, // Raw values: 12.869400, 1.972220, 77615.102400, 4363.753300, 10753.793900, -2119.327358, 1041.831300
				TestUtils.evaluateAtLowerBounds(problem).getConstraints(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 83060.7440000, 1350.0000000, 2853468.9649418, 447902.6720089, 11122.2222222 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }, // Raw values: -0.5551111, -0.9836000, -40734.6671111, -15845.4547778, -9869.1898889, -1964.3952350, -537.7225556
				TestUtils.evaluateAtUpperBounds(problem).getConstraints(),
				0.001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("RCM11", 5, false);
	}

}
