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

public class MaF6Test extends ProblemTest {
	
	@Test
	public void test2D() {
		Problem problem = new MaF6(2);
		
		Assert.assertArrayEquals(new double[] { 251.0, 0.0 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 251.0 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 0.70711, 0.70711 },
				evaluateAt(problem, Vector.of(11, 0.5)).getObjectiveValues(),
				0.00001);
	}
	
	@Test
	public void test3D() {
		Problem problem = new MaF6(3);
		
		Assert.assertArrayEquals(new double[] { 244.70691, 55.85275, 0.0 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 251.0 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 0.5, 0.5, 0.70711 },
				evaluateAt(problem, Vector.of(12, 0.5)).getObjectiveValues(),
				0.00001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("MaF6_2", 2, false);
		assertProblemDefined("MaF6_3", 3, false);
	}
	
	@Test
	public void testAgainstJMetal3D() {
		assumeProblemDefined("MaF6_3-JMetal");
		testAgainstJMetal("MaF6_3");
	}

}
