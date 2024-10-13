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

public class ZCAT13Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT13(3);
		
		Assert.assertArrayEquals(new double[] { 1.562250, 6.085544, 15.750000 }, 
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.627477, 17.346272, 34.196120 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.169556, 16.086915, 40.608882 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.700372, 10.631518, 22.234614 }, 
				evaluateAt(problem, -0.308044, -0.167561, 0.794227, -0.259670, -1.158484, -1.594798, -0.383843, -1.656152, -3.597608, 0.386064, -4.530505, 0.943464, 4.829387, 1.206823, -5.838941, 4.246819, -4.630271, -8.301096, 4.691068, 3.304094, 4.479630, 7.271796, 3.767044, 4.819503, -4.450979, 4.831946, 3.926103, 7.897202, -12.317753, 8.057822).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		assertGeneratedSolutionsAreNondominated(new ZCAT13(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT13_2", 2, true);
		assertProblemDefined("ZCAT13_3", 3, false);
	}
	
}
