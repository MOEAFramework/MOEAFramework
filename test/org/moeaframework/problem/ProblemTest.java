/* Copyright 2009-2022 David Hadka
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

import org.junit.Assert;
import org.junit.Assume;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Utilities for testing problems against multiple implementations.
 */
public abstract class ProblemTest {
	
	/**
	 * Call from any test to skip if JMetal does not exist.
	 */
	public void assumeJMetalExists() {
		Assume.assumeTrue(ProblemFactory.getInstance().hasProvider(
				"org.moeaframework.problem.jmetal.JMetalProblems"));
	}
	
	/**
	 * Tests the MOEA Framework implementation against the JMetal implementation.
	 * 
	 * @param problem the problem name
	 */
	public void test(String problem) {
		test(problem, true);
	}

	/**
	 * Tests the MOEA Framework implementation against the JMetal implementation.
	 * 
	 * @param problem the problem name
	 * @param exactConstraints if {@code true}, require identical constraint values
	 */
	public void test(String problem, boolean exactConstraints) {
		assumeJMetalExists();
		
		Problem problemA = ProblemFactory.getInstance().getProblem(problem);
		Problem problemB = ProblemFactory.getInstance().getProblem(problem + "-JMetal");

		test(problemA, problemB, exactConstraints);
	}
	
	/**
	 * Tests if two problems produce identical results.
	 * 
	 * @param problemA the first problem
	 * @param problemB the second problem
	 * @param exactConstraints if {@code true}, require identical constraint values
	 */
	protected void test(Problem problemA, Problem problemB, boolean exactConstraints) {
		RandomInitialization initialization = new RandomInitialization(problemA, 1);
		
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			Solution solutionA = initialization.initialize()[0];
			Solution solutionB = solutionA.copy();
			
			problemA.evaluate(solutionA);
			problemB.evaluate(solutionB);
			
			compare(solutionA, solutionB, exactConstraints);
		}
	}

	/**
	 * Compares the objectives and constraint values of two solutions.
	 * 
	 * @param solutionA the first solution
	 * @param solutionB the second solution
	 * @param exactConstraints if {@code true}, require identical constraint values
	 */
	protected void compare(Solution solutionA, Solution solutionB, boolean exactConstraints) {
		for (int i = 0; i < solutionA.getNumberOfObjectives(); i++) {
			TestUtils.assertEquals(solutionA.getObjective(i), solutionB.getObjective(i));
		}
		
		for (int i = 0; i < solutionA.getNumberOfConstraints(); i++) {
			if (exactConstraints) {
				TestUtils.assertEquals(solutionA.getConstraint(i), solutionB.getConstraint(i));
			} else {
				// only check if constraints are feasible (== 0) or infeasible (!= 0)
				Assert.assertEquals(solutionA.getConstraint(i) != 0, solutionB.getConstraint(i) != 0);
			}
		}
	}

}
