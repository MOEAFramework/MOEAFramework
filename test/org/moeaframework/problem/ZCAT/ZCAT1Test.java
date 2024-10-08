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
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class ZCAT1Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT1(3);
		
		Assert.assertArrayEquals(new double[] { 0.833701, 3.205340, 5.337009 }, 
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.500000, 10.000000, 31.500000 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.500056, 15.555805, 34.998878 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.136300, 4.213556, 18.908927 }, 
				evaluateAt(problem, -0.280338, -0.814084, 0.739272, -0.102162, -1.948444, 1.792249, -2.181722, -3.914709, 1.589035, -4.589177, -1.398357, 3.875549, 2.018275, -6.903109, 6.436933, -7.636705, -3.220683, -0.261082, -4.045127, -8.922555, -7.572965, 3.545114, -4.869828, -9.576003, 2.954402, -9.837334, -5.931108, 8.511191, 13.365776, -1.504652).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		assertGeneratedSolutionsAreNondominated(new ZCAT1(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT1_2", 2, true);
		assertProblemDefined("ZCAT1_3", 3, false);
	}
	
}
