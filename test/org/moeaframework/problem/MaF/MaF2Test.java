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
package org.moeaframework.problem.MaF;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class MaF2Test extends ProblemTest {
	
	@Test
	public void test2D() {
		Problem problem = new MaF2(2);
		
		Assert.assertArrayEquals(new double[] { 1.212591, 0.502272 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.502272, 1.212591 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.707106, 0.707106 }, 
				evaluateAt(problem, Vector.of(11, 0.5)).getObjectiveValues(),
				0.000001);
	}
	
	@Test
	public void test3D() {
		Problem problem = new MaF2(3);
		
		Assert.assertArrayEquals(new double[] { 1.013594, 0.419844, 0.478354 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.173905, 0.419844, 1.154849 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.5, 0.5, 0.707106 }, 
				evaluateAt(problem, Vector.of(12, 0.5)).getObjectiveValues(),
				0.000001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("MaF2_2", 2, false);
		assertProblemDefined("MaF2_3", 3, false);
	}
	
	@Test
	public void testAgainstJMetal3D() {
		assumeProblemDefined("MaF2_3-JMetal");
		testAgainstJMetal("MaF2_3");
	}

}
