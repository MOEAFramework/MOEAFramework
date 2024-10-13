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
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class LSMOP2Test extends ProblemTest {
	
	@Test
	public void test2D() {
		Problem problem = new LSMOP2(2);
		
		Assert.assertArrayEquals(new double[] { 0.0, 1.0 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 1.0635, 0.0 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 0.5441, 0.57265 }, 
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void test3D() {
		Problem problem = new LSMOP2(3);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 1.0 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 1.0526, 0.0, 0.0 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 0.2705, 0.2846, 0.5315 }, 
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void test4D() {
		Problem problem = new LSMOP2(4);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.0, 1.0 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 1.0462, 0.0, 0.0, 0.0 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 0.1370, 0.1452, 0.2678, 0.5708 }, 
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("LSMOP2_2", 2, true);
		assertProblemDefined("LSMOP2_3", 3, false);
	}

}
