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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class LSMOP3Test extends ProblemTest {
	
	@Test
	public void test2D() {
		Problem problem = new LSMOP3(2);
		
		Assert.assertArrayEquals(new double[] { 0.0, 1.9655 }, 
				evaluateAtLowerBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 14.3374, 0.0 }, 
				evaluateAtUpperBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 20.0, 22628.0 }, 
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectives(),
				1.0);
	}
	
	@Test
	public void test3D() {
		Problem problem = new LSMOP3(3);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 1.0 }, 
				evaluateAtLowerBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 12.3276, 0.0, 0.0 }, 
				evaluateAtUpperBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 10.0, 12218.0, 9.0 }, 
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectives(),
				1.0);
	}
	
	@Test
	public void test4D() {
		Problem problem = new LSMOP3(4);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.0, 1.9655 }, 
				evaluateAtLowerBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 11.71900, 0.0, 0.0, 0.0 }, 
				evaluateAtUpperBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 5.0, 6515.0, 7.0, 20907.0 }, 
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectives(),
				1.0);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("LSMOP3_2", 2, true);
		assertProblemDefined("LSMOP3_3", 3, false);
	}

}
