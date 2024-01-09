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
import org.moeaframework.problem.ProblemTest;

/**
 * Tests the {@link C1_DTLZ3} class.
 */
public class C1_DTLZ3Test extends ProblemTest {
	
	@Test
	public void testProvider() {
		assertProblemDefined("C1_DTLZ3_2", 2, false);
		assertProblemDefined("C1_DTLZ3_3", 3, false);
	}
	
	@Test
	public void test() {
		Problem problem = new C1_DTLZ3(12, 3);
		
		Assert.assertArrayEquals(new double[] { 251.0, 0.0, 0.0 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.0 }, 
				TestUtils.evaluateAtLowerBounds(problem).getConstraints(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 9.41099264e-31, 1.53693173e-14, 2.51000000e+02 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.0 }, 
				TestUtils.evaluateAtUpperBounds(problem).getConstraints(),
				0.000001);
	}
	
	@Test
	public void testGenerate() {
		testGenerate(2);
		testGenerate(3);
		testGenerate(5);
		testGenerate(8);
		testGenerate(10);
		testGenerate(15);
	}
	
	/**
	 * All optimal solutions from the DTLZ3 problem should be feasible.
	 * 
	 * @param numberOfObjectives the number of objectives
	 */
	public void testGenerate(int numberOfObjectives) {
		try (C1_DTLZ3 problem = new C1_DTLZ3(numberOfObjectives)) {	
			for (int i = 0; i < TestThresholds.SAMPLES; i++) {
				Solution solution = problem.generate();
				problem.evaluate(solution);
				Assert.assertFalse(solution.violatesConstraints());
			}
		}
	}

}
