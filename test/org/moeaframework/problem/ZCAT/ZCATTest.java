package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.util.Vector;

/**
 * The expected values are computed using https://github.com/evo-mx/ZCAT by changing 
 * <pre>
 *   for (i = 0; i < nvar; ++i)
 *   {
 *      x[i] = rnd_real(LB[i], UB[i]);
 *   }
 * </pre>
 * to one of the following:
 * <pre>
 *   x[i] = 0.0;    // Evaluate at (0, 0, ..., 0)
 *   x[i] = LB[i];  // Evaluate at lower bounds
 *   x[i] = UB[i];  // Evaluate at upper bounds
 * </pre>
 */
public class ZCATTest {
	
	@Test
	public void testInvalidLevel() {
		Assert.assertThrows(IllegalArgumentException.class, () -> createTestInstance(3, 0, false, false, true));
		Assert.assertThrows(IllegalArgumentException.class, () -> createTestInstance(3, 7, false, false, true));
	}

	@Test
	public void testLevel1() {
		Problem problem = createTestInstance(3, 1, false, false, true);
		
		Assert.assertArrayEquals(new double[] { 0.833701, 3.205340, 5.337009 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
	}
		
	@Test
	public void testLevel2() {
		Problem problem = createTestInstance(3, 2, false, false, true);
		
		Assert.assertArrayEquals(new double[] { 3.000000, 11.749279, 25.136039 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
	}
		
	@Test
	public void testLevel3() {
		Problem problem = createTestInstance(3, 3, false, false, true);
		
		
		Assert.assertArrayEquals(new double[] { 2.787357, 11.639522, 26.351971 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
	}
		
	@Test
	public void testLevel4() {
		Problem problem = createTestInstance(3, 4, false, false, true);
		
		Assert.assertArrayEquals(new double[] { 4.590581, 18.693609, 42.143607 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
	}
		
	@Test
	public void testLevel5() {
		Problem problem = createTestInstance(3, 5, false, false, true);
		
		Assert.assertArrayEquals(new double[] { 7.866591, 30.676823, 75.678593 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
	}
		
	@Test
	public void testLevel6() {
		Problem problem = createTestInstance(3, 6, false, false, true);
		
		Assert.assertArrayEquals(new double[] { 7.600428, 30.164975, 64.648922 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
	}
	
	@Test
	public void testBias() {
		Problem problem = createTestInstance(3, 1, true, false, true);
		
		Assert.assertArrayEquals(new double[] { 8.028801, 31.592222, 76.557626 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
	}
	
	@Test
	public void testImbalance() {
		Problem problem = createTestInstance(3, 1, false, true, true);
		
		Assert.assertArrayEquals(new double[] { 0.833701, 18.693609, 5.337009 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
	}
	
	@Test
	public void testCombination() {
		Problem problem = createTestInstance(3, 6, true, true, true);
		
		Assert.assertArrayEquals(new double[] { 8.028801, 25.583731, 76.557626 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
	}
	
	@Test
	public void testSimplePS() {
		Problem problem = createTestInstance(3, 1, false, false, false);
		
		Assert.assertArrayEquals(new double[] { 1.278410, 5.113640, 9.641729 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
	}
	
	@Test
	public void testTwoObjective() {
		Problem problem = createTestInstance(2, 1, false, false, true);
		
		Assert.assertArrayEquals(new double[] { 1.035232, 2.352128 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
	}
	
	// Creates other variants of the ZCAT1 instance
	private ZCAT createTestInstance(int numberOfObjectives, int level, boolean bias, boolean imbalance,
			boolean complicatedPS) {
		return new ZCAT(numberOfObjectives, level, bias, imbalance, PFShapeFunction.F1,
				complicatedPS ? PSShapeFunction.G4 : PSShapeFunction.G0) {};
	}
	
}
