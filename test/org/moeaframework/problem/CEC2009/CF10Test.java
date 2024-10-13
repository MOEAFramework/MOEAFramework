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
package org.moeaframework.problem.CEC2009;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;

public class CF10Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new CF10();
		
		Assert.assertArrayEquals(new double[] { 33.0, 32.0, 32.0 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { -2.6768 },
				evaluateAtLowerBounds(problem).getConstraintValues(),
				0.0001);
		
		Assert.assertFalse(evaluateAtLowerBounds(problem).isFeasible());
		
		Assert.assertArrayEquals(new double[] { 13.5889, 4.0141, 10.3382 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.001);
		
		Assert.assertArrayEquals(new double[] { -3.4419 },
				evaluateAtUpperBounds(problem).getConstraintValues(),
				0.001);
		
		Assert.assertFalse(evaluateAtUpperBounds(problem).isFeasible());
	}

	@Test
	public void testProvider() {
		assertProblemDefined("CF10", 3);
	}

}
