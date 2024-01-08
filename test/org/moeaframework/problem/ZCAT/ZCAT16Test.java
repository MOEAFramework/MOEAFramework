package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.util.Vector;

public class ZCAT16Test {

	@Test
	public void test() {
		Problem problem = new ZCAT16(3);
		
		Assert.assertArrayEquals(new double[] { 1.462439, 5.019840, 12.053325 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.257218, 13.019840, 39.034466 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.253447, 17.019840, 30.072183 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.477041, 4.185910, 11.590992 }, 
				TestUtils.evaluateAt(problem, 0.279341, 0.171896, 0.377137, 1.416472, 2.052186, 1.695658, -1.579087, -2.432268, -2.418886, -3.673868, -2.690783, 3.284320, -5.996409, -3.726970, -3.924829, -3.164205, -4.356797, 0.382675, 0.302581, 5.823157, 9.908309, 0.465543, -1.807873, 9.883464, 3.964032, -9.848410, -10.900923, -8.289450, -13.954855, -5.211829).getObjectives(),
				0.0001);
	}
	
}
