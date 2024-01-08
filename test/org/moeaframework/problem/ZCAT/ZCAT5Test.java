package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class ZCAT5Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT5(3);
		
		Assert.assertArrayEquals(new double[] { 0.786754, 3.088933, 2.687215 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.566786, 2.519048, 13.911830 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.165194, 9.178640, 11.696120 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.080177, 6.704797, 13.806611 }, 
				TestUtils.evaluateAt(problem, -0.226217, -0.552727, 0.905407, 0.869005, -1.173034, 2.142226, -2.226385, 0.717899, 2.285384, -4.339431, 0.476892, 4.943018, -4.396410, 5.171364, -2.653841, -2.321288, 7.642499, -0.186172, -4.536227, -8.627882, 1.345566, 10.640629, 6.844331, 3.563167, 7.893982, 2.178644, -5.206301, 1.573645, -1.780694, 4.814732).getObjectives(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		TestUtils.assertGeneratedSolutionsAreNondominated(new ZCAT5(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT5_2", 2, true);
		assertProblemDefined("ZCAT5_3", 3, false);
	}
	
}
