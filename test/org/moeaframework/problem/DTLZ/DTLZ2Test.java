/* Copyright 2009-2018 David Hadka
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
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AnalyticalProblem;
import org.moeaframework.problem.ProblemTest;

/**
 * Tests the {@link DTLZ2} class.
 */
public class DTLZ2Test extends ProblemTest {

	/**
	 * Tests the 2D case.
	 * 
	 * @throws Exception if a JMetal error occurred
	 */
	@Test
	public void testDTLZ2_2D() throws Exception {
		test(2);
		testReferenceSet(2);
	}

	/**
	 * Tests the 3D case.
	 * 
	 * @throws Exception if a JMetal error occurred
	 */
	@Test
	public void testDTLZ2_3D() throws Exception {
		test(3);
		testReferenceSet(3);
	}

	/**
	 * Tests the 4D case.
	 * 
	 * @throws Exception if a JMetal error occurred
	 */
	@Test
	public void testDTLZ2_4D() throws Exception {
		test(4);
		testReferenceSet(4);
	}

	/**
	 * Tests the 5D case.
	 * 
	 * @throws Exception if a JMetal error occurred
	 */
	@Test
	public void testDTLZ2_5D() throws Exception {
		test(5);
		testReferenceSet(5);
	}

	/**
	 * Tests the 6D case.
	 * 
	 * @throws Exception if a JMetal error occurred
	 */
	@Test
	public void testDTLZ2_6D() throws Exception {
		test(6);
		testReferenceSet(6);
	}

	/**
	 * Tests the 7D case.
	 * 
	 * @throws Exception if a JMetal error occurred
	 */
	@Test
	public void testDTLZ2_7D() throws Exception {
		test(7);
		testReferenceSet(7);
	}

	/**
	 * Tests the 8D case.
	 * 
	 * @throws Exception if a JMetal error occurred
	 */
	@Test
	public void testDTLZ2_8D() throws Exception {
		test(8);
		testReferenceSet(8);
	}

	/**
	 * Asserts that the {@link DTLZ2#evaluate} method works correctly.
	 * 
	 * @param M the number of objectives
	 * @throws Exception if a JMetal error occurred
	 */
	protected void test(int M) throws Exception {
		jmetal.core.Problem problemA = new jmetal.problems.DTLZ.DTLZ2("Real",
				M + 9, M);
		Problem problemB = new org.moeaframework.problem.DTLZ.DTLZ2(M);

		test(problemA, problemB);
	}

	/**
	 * Tests if the {@link DTLZ2#generate} method works correctly.
	 * 
	 * @param M the number of objectives
	 */
	protected void testReferenceSet(int M) {
		AnalyticalProblem problem = new org.moeaframework.problem.DTLZ.DTLZ2(M);

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			Solution solution = problem.generate();
			double sum = 0.0;

			for (int j = 0; j < solution.getNumberOfObjectives(); j++) {
				sum += Math.pow(solution.getObjective(j), 2.0);
			}

			Assert.assertEquals(1.0, Math.sqrt(sum),
					TestThresholds.SOLUTION_EPS);
		}
	}

}
