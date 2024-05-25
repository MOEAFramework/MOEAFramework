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
import org.moeaframework.problem.ProblemTest;

public class MaF8Test extends ProblemTest {
	
	@Test
	public void test2D() {
//		Problem problem = new MaF8(2);
//		
//		Assert.assertArrayEquals(new double[] { 14.0, 0.0 }, 
//				evaluateAtLowerBounds(problem).getObjectives(),
//				0.00001);
//		
//		Assert.assertArrayEquals(new double[] { 0.0, 7.0 }, 
//				evaluateAtUpperBounds(problem).getObjectives(),
//				0.00001);
//		
//		Assert.assertArrayEquals(new double[] { 4.0, 0.0 }, 
//				evaluateAt(problem, Vector.of(2, 0.5)).getObjectives(),
//				0.00001);
	}
	
	@Test
	public void test3D() {
//		Problem problem = new MaF8(3);
//		
//		Assert.assertArrayEquals(new double[] { 28.0, 0.0, 0.0 }, 
//				evaluateAtLowerBounds(problem).getObjectives(),
//				0.00001);
//		
//		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 7.0 }, 
//				evaluateAtUpperBounds(problem).getObjectives(),
//				0.00001);
//		
//		Assert.assertArrayEquals(new double[] { 8.0, 0.0, 0.0 }, 
//				evaluateAt(problem, Vector.of(2, 0.5)).getObjectives(),
//				0.00001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("MaF8_2", 2, false);
		assertProblemDefined("MaF8_3", 3, false);
	}
	
	@Test
	public void testAgainstJMetal3D() {
		testAgainstJMetal("MaF8_3");
	}

}
