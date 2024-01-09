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

import org.junit.Assert;
import org.junit.Assume;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;

/**
 * Utilities for testing problems against multiple implementations.
 */
public abstract class ProblemTest {
	
	/**
	 * Call from any test to skip if JMetal does not exist.
	 */
	public void assumeJMetalExists() {
		Assume.assumeTrue("JMetal-Plugin required to run test",
				ProblemFactory.getInstance().hasProvider("org.moeaframework.problem.jmetal.JMetalProblems"));
	}
	
	/**
	 * Asserts that the problem is defined and has the given properties.
	 * 
	 * @param problemName the problem name
	 * @param expectedNumberOfObjectives the expected number of objectives
	 */
	public void assertProblemDefined(String problemName, int expectedNumberOfObjectives) {
		assertProblemDefined(problemName, expectedNumberOfObjectives, true);
	}
	
	/**
	 * Asserts that the problem is defined and has the given properties.
	 * 
	 * @param problemName the problem name
	 * @param expectedNumberOfObjectives the expected number of objectives
	 * @param expectReferenceSet {@code true} if it should have a reference set
	 */
	public void assertProblemDefined(String problemName, int expectedNumberOfObjectives, boolean expectReferenceSet) {
		Problem problem = ProblemFactory.getInstance().getProblem(problemName);
		Assert.assertNotNull(problem);
		Assert.assertEquals(expectedNumberOfObjectives, problem.getNumberOfObjectives());
		
		if (expectReferenceSet) {
			NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet(problemName);
			Assert.assertNotNull(referenceSet);
			Assert.assertTrue(referenceSet.size() > 0);
			Assert.assertEquals(expectedNumberOfObjectives, referenceSet.get(0).getNumberOfObjectives());
		}
	}
	
	/**
	 * Tests the MOEA Framework implementation against the JMetal implementation.
	 * 
	 * @param problemName the problem name
	 */
	public void testAgainstJMetal(String problemName) {
		testAgainstJMetal(problemName, true);
	}

	/**
	 * Tests the MOEA Framework implementation against the JMetal implementation.
	 * 
	 * @param problemName the problem name
	 * @param exactConstraints if {@code true}, require identical constraint values
	 */
	public void testAgainstJMetal(String problemName, boolean exactConstraints) {
		assumeJMetalExists();
		
		Problem problemA = ProblemFactory.getInstance().getProblem(problemName);
		Problem problemB = ProblemFactory.getInstance().getProblem(problemName + "-JMetal");

		testAgainstJMetal(problemA, problemB, exactConstraints);
	}
	
	/**
	 * Tests if two problems produce identical results.
	 * 
	 * @param problemA the first problem
	 * @param problemB the second problem
	 * @param exactConstraints if {@code true}, require identical constraint values
	 */
	protected void testAgainstJMetal(Problem problemA, Problem problemB, boolean exactConstraints) {
		RandomInitialization initialization = new RandomInitialization(problemA);
		
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			Solution solutionA = initialization.initialize(1)[0];
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
