package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.util.Vector;

public class ZCAT15Test {

	@Test
	public void test() {
		Problem problem = new ZCAT15(3);
		
		Assert.assertArrayEquals(new double[] { 1.739504, 6.196056, 14.395043 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 5.074577, 20.000000, 53.254233 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.056309, 12.945101, 22.789547 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.567551, 9.228666, 23.461998 }, 
				TestUtils.evaluateAt(problem, 0.198936, -0.367582, -1.375415, -1.591970, 2.193607, 2.508740, 2.884077, 2.957715, -1.764280, -4.313750, 4.988815, -1.817740, 3.228524, -1.291136, -4.234178, -4.083253, -0.283208, 5.227770, -3.743611, 7.512911, 0.905729, 9.565398, 8.514093, -10.834280, -8.729754, -7.267325, -0.201983, 7.760443, -11.686637, 12.481976).getObjectives(),
				0.0001);
	}
	
}
