/* Copyright 2009-2025 David Hadka
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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;

public class C3_DTLZ1Test extends ProblemTest {
	
	@Test
	public void testProvider() {
		assertProblemDefined("C3_DTLZ1_2", 2, false);
		assertProblemDefined("C3_DTLZ1_3", 3, false);
	}
	
	@Test
	public void test() {
		Problem problem = new C3_DTLZ1(12, 3);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 125.5 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 250.0, 250.0, 124.5 },
				evaluateAtLowerBounds(problem).getConstraintValues(),
				0.000001);
		
		Assert.assertTrue(evaluateAtLowerBounds(problem).isFeasible());
		
		Assert.assertArrayEquals(new double[] { 125.5, 0.0, 0.0 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 124.5, 250.0, 250.0 },
				evaluateAtUpperBounds(problem).getConstraintValues(),
				0.000001);
		
		Assert.assertTrue(evaluateAtUpperBounds(problem).isFeasible());
	}

}
