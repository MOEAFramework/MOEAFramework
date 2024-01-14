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
package org.moeaframework.problem.ZCAT;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class ZCAT11Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT11(3);
		
		Assert.assertArrayEquals(new double[] { 1.840315, 6.845845, 15.193698 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.179539, 16.131285, 40.409218 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.501091, 17.560405, 34.978177 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.724464, 8.027485, 20.207547 }, 
				TestUtils.evaluateAt(problem, 0.006726, 0.042860, 0.126059, -1.860593, -1.694467, 2.490285, 2.394761, 0.819671, -1.716377, 0.629490, -3.192200, 0.386490, -6.329432, -0.158435, -4.634449, 0.903844, -2.366734, 2.881049, -1.510300, 2.654129, 3.192454, -2.727939, -3.115215, 5.620439, 0.508911, -9.552645, 8.543997, 5.788566, -5.705426, 11.332616).getObjectives(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		TestUtils.assertGeneratedSolutionsAreNondominated(new ZCAT11(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT11_2", 2, true);
		assertProblemDefined("ZCAT11_3", 3, false);
	}
	
}
