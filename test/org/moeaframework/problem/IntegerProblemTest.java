/* Copyright 2009-2024 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.problem;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.single.Rosenbrock;

/**
 * Tests a problem using integer-valued decision variables.
 */
public class IntegerProblemTest {

	/**
	 * The Rosenbrock problem defined using integers instead of floating-point values.
	 */
	public static class StepRosenbrockProblem extends Rosenbrock {
		
		public StepRosenbrockProblem() {
			super();
		}

		@Override
		public void evaluate(Solution solution) {
			int x = EncodingUtils.getInt(solution.getVariable(0));
			int y = EncodingUtils.getInt(solution.getVariable(1));
			
			solution.setObjective(0, 100*(y - x*x)*(y - x*x) + (1 - x)*(1 - x));
		}

		@Override
		public Solution newSolution() {
			Solution solution = new Solution(2, 1);
			solution.setVariable(0, EncodingUtils.newInt(-10, 10));
			solution.setVariable(1, EncodingUtils.newInt(-10, 10));
			return solution;
		}
		
	}
	
	@Test
	public void test() {
		NSGAII algorithm = new NSGAII(new StepRosenbrockProblem());
		algorithm.run(100000);
		
		Solution solution = algorithm.getResult().get(0);
		Assert.assertArrayEquals(new int[] { 1, 1 }, EncodingUtils.getInt(solution));
		Assert.assertEquals(0, solution.getObjective(0), TestThresholds.HIGH_PRECISION);
	}

}
