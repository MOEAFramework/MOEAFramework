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
package org.moeaframework.problem.misc;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;

public class TanakaTest extends ProblemTest {

	@Test
	public void testJMetal() {
		testAgainstJMetal("Tanaka", false);
	}
	
	@Test
	public void test() {
		Problem problem = new Tanaka();
		
		Assert.assertArrayEquals(new double[] { 0.1, 0.1 }, 
				evaluateAt(problem, 0.1, 0.1).getObjectiveValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { 1.08, -0.18 }, 
				evaluateAt(problem, 0.1, 0.1).getConstraintValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertFalse(evaluateAt(problem, 0.1, 0.1).isFeasible());
		
		Assert.assertArrayEquals(new double[] { Math.PI, Math.PI }, 
				evaluateAt(problem, Math.PI, Math.PI).getObjectiveValues(),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertArrayEquals(new double[] { -18.639, 13.456 }, 
				evaluateAt(problem, Math.PI, Math.PI).getConstraintValues(),
				0.001);
		
		Assert.assertFalse(evaluateAt(problem, Math.PI, Math.PI).isFeasible());
	}

}
