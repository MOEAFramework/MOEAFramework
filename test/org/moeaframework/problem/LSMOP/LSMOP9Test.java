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
package org.moeaframework.problem.LSMOP;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class LSMOP9Test extends ProblemTest {
	
	@Test
	public void test2D() {
		Problem problem = new LSMOP9(2);
		
		Assert.assertArrayEquals(new double[] { 0.0, 4.0 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 1.0, 189.54 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.01);
		
		Assert.assertArrayEquals(new double[] { 0.5, 37.1903 },
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void test3D() {
		Problem problem = new LSMOP9(3);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 6.0 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 1.0, 1.0, 311.42 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.01);
		
		Assert.assertArrayEquals(new double[] { 0.5, 0.5, 113.3573 },
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void test4D() {
		Problem problem = new LSMOP9(4);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.0, 8.0 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 1.0, 1.0, 1.0, 565.51 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.01);
		
		Assert.assertArrayEquals(new double[] { 0.5, 0.5, 0.5, 145.6829 },
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("LSMOP9_2", 2, true);
		assertProblemDefined("LSMOP9_3", 3, false);
	}

}
