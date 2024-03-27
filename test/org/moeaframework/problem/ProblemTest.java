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

import java.util.function.Consumer;

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
import org.moeaframework.mock.MockSolution;

/**
 * Utilities for testing problems.  While these are not strict requirements, these tests should cover:
 * <ol>
 *   <li>Testing that the problem and reference set (if applicable) can be constructed through the
 *       {@link ProblemFactory} (use {@link #assertProblemDefined(String, int, boolean)})
 *   <li>Test a handful of inputs to verify the {@link Problem#evaluate(Solution)} method is producing
 *       correct values for the objectives and constraints (if applicable)
 *   <li>If possible, test the problem against the JMetal implementation using {@link #testAgainstJMetal(String)}
 *   <li>If an {@link AnalyticalProblem}, test that the generated optimal solutions are in fact optimal and
 *       non-dominated
 *   <li>Any other relevant tests to validate the problem
 * </ol>
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
	 * Assume that the given problem exists, and if not skip the test.
	 * 
	 * @param problemName the problem name
	 */
	public void assumeProblemDefined(String problemName) {
		try {
			ProblemFactory.getInstance().getProblem(problemName);
		} catch (ProviderNotFoundException e) {
			Assume.assumeNoException("problem " + problemName + " not found, skipping test", e);
		}
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
	 * Tests the reference set to verify it is non-empty and all solutions match the given predicate.
	 * 
	 * @param problemName the problem name
	 * @param assertion the assertion to check if a solution lies on the Pareto front
	 */
	public void testReferenceSet(String problemName, Consumer<Solution> assertion) {
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet(problemName);
		
		Assert.assertNotNull(referenceSet);
		Assert.assertTrue(referenceSet.size() > 0);
		
		for (Solution solution : referenceSet) {
			assertion.accept(solution);
		}
	}
	
	/**
	 * Returns the solution resulting from evaluating the problem with the specified decision variables.
	 * 
	 * @param problem the problem
	 * @param variables the decision variable values
	 * @return the solution resulting from evaluating the problem with the specified decision variables
	 */
	public static Solution evaluateAt(Problem problem, double... variables) {
		return MockSolution.of(problem).at(variables).evaluate();
	}
	
	/**
	 * Returns the solution resulting from evaluating the problem at its lower bounds.
	 * 
	 * @param problem the problem
	 * @return the solution evaluated at the lower bounds
	 */
	public static Solution evaluateAtLowerBounds(Problem problem) {
		return MockSolution.of(problem).atLowerBounds().evaluate();
	}
	
	/**
	 * Returns the solution resulting from evaluating the problem at its upper bounds.
	 * 
	 * @param problem the problem
	 * @return the solution evaluated at the upper bounds
	 */
	public static Solution evaluateAtUpperBounds(Problem problem) {
		return MockSolution.of(problem).atUpperBounds().evaluate();
	}
	
	/**
	 * Asserts that all solutions returned by the `generate()` method are feasible and non-dominated.
	 * 
	 * @param problem the analytical problem
	 * @param count the number of solutions to generate
	 */
	public static void assertGeneratedSolutionsAreNondominated(AnalyticalProblem problem, int count) {
		NondominatedPopulation population = new NondominatedPopulation();
		
		for (int i = 0; i < count; i++) {
			Solution solution = problem.generate();
			problem.evaluate(solution);
			
			Assert.assertFalse(solution.violatesConstraints());
			Assert.assertTrue(population.add(solution));
		}
	}
	
	/**
	 * Tests analytical problems to verify generated solutions lie on the Pareto front.
	 * 
	 * @param problemName the problem name
	 * @param assertion the assertion to check if a solution lies on the Pareto front
	 */
	public void testGenerate(String problemName, Consumer<Solution> assertion) {
		try (AnalyticalProblem problem = (AnalyticalProblem)ProblemFactory.getInstance().getProblem(problemName)) {
			for (int i = 0; i < TestThresholds.SAMPLES; i++) {
				assertion.accept(problem.generate());
			}
		}
	}
	
	/**
	 * Asserts that the solution is feasible and has no constraint violations.
	 * 
	 * @param solution the solution to test
	 */
	public static void assertFeasible(Solution solution) {
		Assert.assertFalse(solution.violatesConstraints());
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
			
			// JMetal only recognizes negative values as violating constraints, therefore fix the sign
			// before performing exact comparisons.
			if (exactConstraints && problemA.getNumberOfConstraints() > 0) {
				double[] constraints = solutionA.getConstraints();
				
				for (int j = 0; j < constraints.length; j++) {
					if (constraints[j] > 0.0) {
						constraints[j] = -constraints[j];
					}
				}
				
				solutionA.setConstraints(constraints);
			}
			
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
