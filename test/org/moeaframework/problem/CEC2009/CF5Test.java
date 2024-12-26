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
package org.moeaframework.problem.CEC2009;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;

public class CF5Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new CF5();
		
		Assert.assertArrayEquals(new double[] { 32.0, 35.0 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { -1.75 },
				evaluateAtLowerBounds(problem).getConstraintValues(),
				0.0001);
		
		Assert.assertFalse(evaluateAtLowerBounds(problem).isFeasible());
		
		Assert.assertArrayEquals(new double[] { 43.258, 23.279 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { 1.2798 },
				evaluateAtUpperBounds(problem).getConstraintValues(),
				0.0001);
		
		Assert.assertTrue(evaluateAtUpperBounds(problem).isFeasible());
	}

	@Test
	public void testProvider() {
		assertProblemDefined("CF5", 2);
	}

}
