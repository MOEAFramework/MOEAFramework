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
package org.moeaframework.problem;

import org.apache.commons.math3.stat.StatUtils;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;
import org.moeaframework.algorithm.jmetal.JMetalUtils;
import org.moeaframework.algorithm.jmetal.ProblemAdapter;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Utilities for testing problems against their implementations in JMetal.
 */
public abstract class ProblemTest {

	/**
	 * Tests if a problem implementation in the MOEA Framework matches the
	 * corresponding implementation in JMetal.
	 * 
	 * @param problemA the JMetal problem
	 * @param problemB the MOEA Framework problem
	 * @throws Exception if an error occurred in JMetal
	 */
	protected <T extends org.uma.jmetal.solution.Solution<?>> void test(org.uma.jmetal.problem.Problem<T> problemA, Problem problemB)
			throws Exception {
		ProblemAdapter adapter = JMetalUtils.createProblemAdapter(problemB);
		
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			T solutionA = problemA.createSolution();
			Solution solutionB = adapter.convert(solutionA);

			problemA.evaluate(solutionA);
			problemB.evaluate(solutionB);

			compare(solutionA, solutionB);
		}
	}

	/**
	 * Compares a JMetal solution against an MOEA Framework solution to ensure
	 * their objectives and constraint violations match.
	 * 
	 * @param solutionA the JMetal solution
	 * @param solutionB the MOEA Framework solution
	 */
	protected <T extends org.uma.jmetal.solution.Solution<?>> void compare(T solutionA, Solution solutionB) {
		for (int i = 0; i < solutionA.getNumberOfObjectives(); i++) {
			TestUtils.assertEquals(
					solutionA.getObjective(i), 
					solutionB.getObjective(i));
		}
		
		if (solutionB.getNumberOfConstraints() > 0) {
			TestUtils.assertEquals(
					Math.abs(JMetalUtils.getOverallConstraintViolation(solutionA)), 
					Math.abs(StatUtils.sum(solutionB.getConstraints())));
		}
	}

}
