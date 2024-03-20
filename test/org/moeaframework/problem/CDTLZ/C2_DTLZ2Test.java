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
package org.moeaframework.problem.CDTLZ;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.problem.DTLZ.DTLZ2;

public class C2_DTLZ2Test extends ProblemTest {
	
	@Test
	public void testProvider() {
		assertProblemDefined("C2_DTLZ2_2", 2, false);
		assertProblemDefined("C2_DTLZ2_3", 3, false);
	}
	
	@Test
	public void test() {
		Problem problem = new C2_DTLZ2(12, 3);
		
		Assert.assertArrayEquals(new double[] { 3.5, 0.0, 0.0 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 6.09 }, 
				TestUtils.evaluateAtLowerBounds(problem).getConstraints(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.31228981e-32, 2.14313190e-16, 3.5 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 6.09 }, 
				TestUtils.evaluateAtUpperBounds(problem).getConstraints(),
				0.000001);
	}
	
	@Test
	public void testGenerate() {
		testGenerate(2, 0.4);
		testGenerate(3, 0.4);
		testGenerate(5, 0.5);
		testGenerate(8, 0.5);
		testGenerate(10, 0.5);
		testGenerate(15, 0.5);
	}
	
	/**
	 * Only a subset of optimal solutions from the DTLZ2 problem should be feasible.
	 * 
	 * @param numberOfObjectives the number of objectives
	 */
	public void testGenerate(int numberOfObjectives, double r) {
		try (C2_DTLZ2 problem = new C2_DTLZ2(numberOfObjectives);
				DTLZ2 originalProblem = new DTLZ2(numberOfObjectives)) {
			for (int i = 0; i < TestThresholds.SAMPLES; i++) {
				Solution originalSolution = originalProblem.generate();
				Solution solution = problem.newSolution();
				
				EncodingUtils.setReal(solution, EncodingUtils.getReal(originalSolution));
				
				problem.evaluate(solution);
				
				// compute the minimum distance from the solution to either
				//    1) the M corner solutions, e.g. (1, 0, ..., 0)
				//    2) the center, e.g., (1/sqrt(M), ..., 1/sqrt(M))
				double minDistance = Double.POSITIVE_INFINITY;
				
				for (int j = 0; j < numberOfObjectives; j++) {
					double distance = Math.pow(solution.getObjective(j)-1.0, 2.0);
					
					for (int k = 0; k < numberOfObjectives; k++) {
						if (k != j) {
							distance += Math.pow(solution.getObjective(k), 2.0);
						}
					}
					
					minDistance = Math.min(minDistance, distance);
				}
				
				double distance = 0.0;
	
				for (int j = 0; j < numberOfObjectives; j++) {
					distance += Math.pow(solution.getObjective(j) - 1 / Math.sqrt(numberOfObjectives), 2.0);
				}
	
				minDistance = Math.min(minDistance, distance);
				
				if (minDistance < Math.pow(r, 2.0)) {
					Assert.assertFalse(solution.violatesConstraints());
				} else {
					Assert.assertTrue(solution.violatesConstraints());
				}
			}
		}
	}

}
