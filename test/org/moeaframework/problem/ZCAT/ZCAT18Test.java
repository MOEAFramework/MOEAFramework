package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.util.Vector;

public class ZCAT18Test {

	@Test
	public void test() {
		Problem problem = new ZCAT18(3);
		
		Assert.assertArrayEquals(new double[] { 1.718801, 6.305782, 15.119073 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 5.077426, 19.655886, 54.000000 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.123292, 13.470310, 20.202709 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.792213, 4.835081, 14.547701 }, 
				TestUtils.evaluateAt(problem, 0.008080, -0.294797, -0.937362, 1.524178, 0.680273, 0.815700, 1.094423, -1.861807, -2.046777, -1.794311, 1.633101, -0.592151, -2.542164, -4.439247, -6.660531, -2.786532, 7.487693, 4.470735, 0.799924, -1.459398, -3.315136, -5.731919, 1.108282, 5.780402, 1.074402, -7.520765, -10.164966, 0.329650, 7.071596, 1.996874).getObjectives(),
				0.0001);
	}
	
}
