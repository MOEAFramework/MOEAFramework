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
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class LSMOP7Test extends ProblemTest {
	
	@Test
	public void test2() {
		Problem problem = new LSMOP7(2);
		
		Assert.assertArrayEquals(new double[] { 1.9655, 0.0 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 120761.0 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				1.0);
		
		Assert.assertArrayEquals(new double[] { 34197.0, 34197.0 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectives(),
				1.0);
	}
	
	@Test
	public void test3() {
		Problem problem = new LSMOP7(3);
		
		Assert.assertArrayEquals(new double[] { 1.9677, 0.0, 0.0 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 1.4864 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 21967.0, 21967.0, 1.0 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectives(),
				1.0);
	}
	
	@Test
	public void test4() {
		Problem problem = new LSMOP7(4);
		
		Assert.assertArrayEquals(new double[] { 1.9630, 0.0, 0.0, 0.0 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.0, 9086.0 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				1.0);
		
		Assert.assertArrayEquals(new double[] { 14394.0, 14394.0, 26546.0, 37541.0 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectives(),
				1.0);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("LSMOP7_2", 2, true);
		assertProblemDefined("LSMOP7_3", 3, false);
	}

}
