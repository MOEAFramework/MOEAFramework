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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class ZCAT10Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT10(3);
		
		Assert.assertArrayEquals(new double[] { 0.786754, 3.088933, 11.352262 }, 
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.566786, 2.519048, 13.911830 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.165194, 9.178640, 11.696120 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.738749, 3.582365, 13.877770 }, 
				evaluateAt(problem, 0.280879, 0.192992, 1.325509, -1.232875, -2.294169, 1.627938, 0.141870, 1.359980, 0.239838, -0.751369, 3.117022, -1.938914, -0.413250, 0.051893, -5.016322, -3.155558, 8.340104, 8.577536, 3.968839, -0.836990, -8.263584, 1.646909, 4.060898, 4.824122, 4.345153, 4.958540, 6.573738, -0.179425, -3.900610, -11.592669).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		assertGeneratedSolutionsAreNondominated(new ZCAT10(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT10_2", 2, true);
		assertProblemDefined("ZCAT10_3", 3, false);
	}
	
}
