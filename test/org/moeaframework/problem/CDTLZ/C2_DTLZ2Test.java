/* Copyright 2009-2016 David Hadka
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
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.Executor;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.DTLZ.DTLZ2;

/**
 * Tests the {@link C2_DTLZ2} class.
 */
public class C2_DTLZ2Test {
	
	/**
	 * Visual test of the Pareto front.  Copy the output and generate a plot,
	 * such as with R, and compare against the figures in Jain and Deb (2014):
	 * <pre>
	 *     library(rgl)
	 *     x = matrix(c(<paste text>), ncol=3, byrow=T)
	 *     plot3d(x)
	 * </pre>
	 */
	@Test
	@Ignore("skip visual tests")
	public void visualTest() {
		NondominatedPopulation result = new Executor()
				.withProblemClass(C2_DTLZ2.class, 3)
				.withAlgorithm("NSGAIII")
				.withMaxEvaluations(100000)
				.run();

		for (Solution solution : result) {
			if (!solution.violatesConstraints()) {
				System.out.format("%.4f, %.4f, %.4f,%n",
						solution.getObjective(0),
						solution.getObjective(1),
						solution.getObjective(2));
			}
		}
	}
	
	@Test
	public void test() {
		test(2, 0.4);
		test(3, 0.4);
		test(5, 0.5);
		test(8, 0.5);
		test(10, 0.5);
		test(15, 0.5);
	}
	
	/**
	 * Only a subset of optimal solutions from the DTLZ2 problem should be
	 * feasible.
	 * 
	 * @param numberOfObjectives the number of objectives
	 */
	public void test(int numberOfObjectives, double r) {
		C2_DTLZ2 problem = new C2_DTLZ2(numberOfObjectives);
		DTLZ2 originalProblem = new DTLZ2(numberOfObjectives);
		
		for (int i = 0; i <TestThresholds.SAMPLES; i++) {
			Solution originalSlution = originalProblem.generate();
			Solution solution = problem.newSolution();
			
			EncodingUtils.setReal(solution,
					EncodingUtils.getReal(originalSlution));
			
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
				distance += Math.pow(solution.getObjective(j) -
					        1 / Math.sqrt(numberOfObjectives), 2.0);
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
