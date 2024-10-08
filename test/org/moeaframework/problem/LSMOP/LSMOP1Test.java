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
package org.moeaframework.problem.LSMOP;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class LSMOP1Test extends ProblemTest {
	
	@Test
	public void test2D() {
		Problem problem = new LSMOP1(2);
		
		Assert.assertArrayEquals(new double[] { 0.0, 1.0 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 4.0449, 0.0 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 10.2848, 9.2195 }, 
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void test3D() {
		Problem problem = new LSMOP1(3);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 1.0 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 2.6732, 0.0, 0.0 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 5.1853, 4.7964, 8.7768 }, 
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void test4D() {
		Problem problem = new LSMOP1(4);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.0, 1.0 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 1.7111, 0.0, 0.0, 0.0 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 2.6143, 2.4878, 4.7030, 8.8538 }, 
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("LSMOP1_2", 2, true);
		assertProblemDefined("LSMOP1_3", 3, false);
	}

}
