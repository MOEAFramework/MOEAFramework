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
package org.moeaframework.problem.MaF;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class MaF4Test extends ProblemTest {
	
	@Test
	public void test2D() {
		Problem problem = new MaF4(2);
		
		Assert.assertArrayEquals(new double[] { 0.0, 1004.0 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 502.0, 0.0 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 0.58579, 1.17157 },
				evaluateAt(problem, Vector.of(11, 0.5)).getObjectiveValues(),
				0.00001);
	}
	
	@Test
	public void test3D() {
		Problem problem = new MaF4(3);
		
		Assert.assertArrayEquals(new double[] { 0.0, 1004.0, 2008.0 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 502.0, 1004.0, 0.0 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 1.0,2.0, 2.34315 },
				evaluateAt(problem, Vector.of(12, 0.5)).getObjectiveValues(),
				0.00001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("MaF4_2", 2, false);
		assertProblemDefined("MaF4_3", 3, false);
	}
	
	@Test
	public void testAgainstJMetal3D() {
		assumeProblemDefined("MaF4_3-JMetal");
		testAgainstJMetal("MaF4_3");
	}

}
