package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.util.Vector;

public class ZCAT6Test {

	@Test
	public void test() {
		Problem problem = new ZCAT6(3);
		
		Assert.assertArrayEquals(new double[] { 0.833701, 3.205340, 7.200970 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.500000, 10.000000, 31.500000 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.500056, 19.555805, 34.998878 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.293693, 5.789108, 7.279147 }, 
				TestUtils.evaluateAt(problem, 0.439966, -0.741347, -0.247492, 0.691239, 0.416574, -0.383728, -2.397958, 2.983556, -2.304681, 4.803371, 4.922317, 5.588598, -0.392491, -3.895636, -3.426200, -7.768599, -7.891547, 6.796154, 7.868775, 2.273510, -5.590525, -8.305023, -2.538148, 5.850864, 3.314039, 7.592685, 0.903286, -1.270216, 8.759389, -7.843558).getObjectives(),
				0.0001);
	}
	
}
