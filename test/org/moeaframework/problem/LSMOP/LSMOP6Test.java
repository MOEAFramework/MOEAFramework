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

public class LSMOP6Test extends ProblemTest {
	
	@Test
	public void test2() {
		Problem problem = new LSMOP6(2);
		
		Assert.assertArrayEquals(new double[] { 1.9167, 0.0 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 1.2001 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 26374.0, 1.0 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectives(),
				1.0);
	}
	
	@Test
	public void test3() {
		Problem problem = new LSMOP6(3);
		
		Assert.assertArrayEquals(new double[] { 1.9231, 0.0, 0.0 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 3115.6 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				1.0);
		
		Assert.assertArrayEquals(new double[] { 18642.0, 26458.0, 37417.0 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectives(),
				1.0);
	}
	
	@Test
	public void test4() {
		Problem problem = new LSMOP6(4);
		
		Assert.assertArrayEquals(new double[] { 1.9091, 0.0, 0.0, 0.0 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 0.0, 1.1094 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.0001);
		
		Assert.assertArrayEquals(new double[] { 12915.0, 15503.0, 21924.0, 1.0 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.5)).getObjectives(),
				1.0);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("LSMOP6_2", 2, true);
		assertProblemDefined("LSMOP6_3", 3, false);
	}

}
