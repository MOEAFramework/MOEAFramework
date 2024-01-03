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
package org.moeaframework.problem.DTLZ;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AnalyticalProblem;
import org.moeaframework.problem.ProblemTest;

/**
 * Tests the {@link DTLZ3} class.
 */
public class DTLZ3Test extends ProblemTest {

	/**
	 * Tests the 2D case.
	 */
	@Test
	public void testDTLZ3_2D() {
		test(2);
		testReferenceSet(2);
	}

	/**
	 * Tests the 3D case.
	 */
	@Test
	public void testDTLZ3_3D() {
		test(3);
		testReferenceSet(3);
	}

	/**
	 * Asserts that the {@link DTLZ3#evaluate} method works correctly.
	 * 
	 * @param M the number of objectives
	 */
	protected void test(int M) {
		test("DTLZ3_" + M);
	}

	/**
	 * Tests if the {@link DTLZ3#generate} method works correctly.
	 * 
	 * @param M the number of objectives
	 */
	protected void testReferenceSet(int M) {
		try (AnalyticalProblem problem = new DTLZ3(M)) {
			for (int i = 0; i < TestThresholds.SAMPLES; i++) {
				Solution solution = problem.generate();
				double sum = 0.0;
	
				for (int j = 0; j < solution.getNumberOfObjectives(); j++) {
					sum += Math.pow(solution.getObjective(j), 2.0);
				}
	
				Assert.assertEquals(1.0, Math.sqrt(sum), TestThresholds.SOLUTION_EPS);
			}
		}
	}

}
