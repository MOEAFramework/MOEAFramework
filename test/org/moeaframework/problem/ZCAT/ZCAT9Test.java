package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class ZCAT9Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT9(3);
		
		Assert.assertArrayEquals(new double[] { 1.343275, 5.606448, 12.544442 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.233394, 7.481752, 21.333909 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.316305, 12.294690, 23.157424 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.505401, 10.196959, 29.515051 }, 
				TestUtils.evaluateAt(problem, -0.397044, -0.602032, 0.767778, -0.141375, -1.519405, -0.839667, 1.092573, -0.268835, 0.913019, 4.111141, -1.932220, 2.962834, -4.838118, -6.384877, -2.262273, -3.260867, 4.609494, 4.805958, -0.786469, 7.459885, -4.372109, 9.912325, 9.936563, -6.995694, 11.111910, 1.346912, 5.486494, 3.391362, -6.292248, 0.450696).getObjectives(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		TestUtils.assertGeneratedSolutionsAreNondominated(new ZCAT9(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT9_2", 2, true);
		assertProblemDefined("ZCAT9_3", 3, false);
	}
	
}
