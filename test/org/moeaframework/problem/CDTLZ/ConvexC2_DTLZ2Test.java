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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;

public class ConvexC2_DTLZ2Test extends ProblemTest {
	
	@Test
	public void testProvider() {
		assertProblemDefined("CONVEX_C2_DTLZ2_2", 2, false);
		assertProblemDefined("CONVEX_C2_DTLZ2_3", 3, false);
	}
	
	@Test
	public void test() {
		Problem problem = new ConvexC2_DTLZ2(12, 3);
		
		Assert.assertArrayEquals(new double[] { 150.0625, 0.0, 0.0 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 15012.451979 }, 
				evaluateAtLowerBounds(problem).getConstraintValues(),
				0.000001);
		
		Assert.assertTrue(evaluateAtLowerBounds(problem).isFeasible());
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 12.25 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 99.991041 }, 
				evaluateAtUpperBounds(problem).getConstraintValues(),
				0.000001);
		
		Assert.assertTrue(evaluateAtUpperBounds(problem).isFeasible());
	}
	
}
