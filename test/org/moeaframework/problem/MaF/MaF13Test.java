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
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class MaF13Test extends ProblemTest {
	
	@Test
	public void test3D() {
		Problem problem = new MaF13(3);
		
		Assert.assertArrayEquals(new double[] { 8.0, 8.0, 9.0 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 2.359368, 8.0, 0.019164 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 1.0 }, 
				evaluateAt(problem, Vector.of(5, 0.0)).getObjectiveValues(),
				0.00001);
	}
	
	@Test
	public void test4D() {
		Problem problem = new MaF13(4);
		
		Assert.assertArrayEquals(new double[] { 8.0, 8.0, 9.0, 4560526297.0 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 2.359368, 8.0, 0.019164, 1073741834.246294 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.00001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 1.0, 1.0 }, 
				evaluateAt(problem, Vector.of(5, 0.0)).getObjectiveValues(),
				0.00001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("MaF13_3", 3, false);
	}
	
	@Test
	public void testAgainstJMetal3D() {
		assumeProblemDefined("MaF13_3-JMetal");
		testAgainstJMetal("MaF13_3");
	}

}
