/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.algorithm;

import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Tests the {@link CMAES} class.
 */
public class CMAESTest {
	
	// TODO: extend with better tests
	
	@Test
	public void testSingleObjective() {
		// the rosenbrock problem
		AbstractProblem problem = new AbstractProblem(2, 1, 0) {

			@Override
			public void evaluate(Solution solution) {
				double result = 0.0;
				double[] x = EncodingUtils.getReal(solution);

				for (int i = 0; i < x.length-1; i++) {
					result += 100 * (x[i]*x[i] - x[i+1])*(x[i]*x[i] - x[i+1]) + (x[i] - 1)*(x[i] - 1);
				}

				solution.setObjective(0, result);
			}

			@Override
			public Solution newSolution() {
				Solution solution = new Solution(2, 1, 0);
				solution.setVariable(0, EncodingUtils.newReal(-10, 10));
				solution.setVariable(1, EncodingUtils.newReal(-10, 10));
				return solution;
			}

		};

		CMAES cmaes = new CMAES(problem, 100);

		for (int i = 0; i < 100; i++) {
			cmaes.step();
		}
		
		TestUtils.assertEquals(0.0, cmaes.getResult().get(0).getObjective(0));
	}

	@Test
	public void testMultiObjective() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		CMAES cmaes = new CMAES(problem, 100);

		for (int i = 0; i < 100; i++) {
			cmaes.step();
		}
	}
	
}
