package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.util.Vector;

public class ZCAT7Test {

	@Test
	public void test() {
		Problem problem = new ZCAT7(3);
		
		Assert.assertArrayEquals(new double[] { 1.313224, 4.976575, 11.545471 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.068842, 13.617866, 39.546382 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.557605, 16.335284, 28.544560 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.100773, 10.342234, 12.020951 }, 
				TestUtils.evaluateAt(problem, 0.348970, 0.705492, 0.864410, 1.259723, -0.837834, -2.204263, 0.955083, -3.224799, -2.436147, 2.681704, 5.404753, 5.229724, 1.976725, -1.334688, 6.895635, -3.258287, 4.507806, 8.316556, 3.042343, 1.550775, -2.777561, -3.161572, 4.803347, -2.142699, -6.152846, -4.195338, 9.409657, 6.114285, 8.966450, 1.007867).getObjectives(),
				0.0001);
	}
	
}
