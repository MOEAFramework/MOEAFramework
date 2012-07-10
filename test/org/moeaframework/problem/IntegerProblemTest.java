package org.moeaframework.problem;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

/**
 * Tests a problem using integer-valued decision variables.
 */
public class IntegerProblemTest {
	
	public static class StepRosenbrockProblem extends AbstractProblem {

		public StepRosenbrockProblem() {
			super(2, 1);
		}

		@Override
		public void evaluate(Solution solution) {
			int[] x = EncodingUtils.getInt(solution);
			double f = Math.pow(1 - x[0], 2) + 100*Math.pow(x[1]-x[0]*x[0], 2);
			
			solution.setObjective(0, f);
		}

		@Override
		public Solution newSolution() {
			Solution solution = new Solution(numberOfVariables, 1);

			for (int i = 0; i < numberOfVariables; i++) {
				solution.setVariable(i, EncodingUtils.newInt(-100, 100));
			}

			return solution;
		}
		
	}
	
	@Test
	public void test() {
		NondominatedPopulation result = new Executor()
				.withAlgorithm("NSGAII")
				.withProblemClass(StepRosenbrockProblem.class)
				.withMaxEvaluations(10000)
				.run();
		
		Solution solution = result.get(0);
		Assert.assertArrayEquals(new int[] { 1, 1 }, 
				EncodingUtils.getInt(solution));
		Assert.assertEquals(0, solution.getObjective(0), Settings.EPS);
	}

}
