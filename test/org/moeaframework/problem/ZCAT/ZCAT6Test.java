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

public class ZCAT6Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT6(3);
		
		Assert.assertArrayEquals(new double[] { 0.833701, 3.205340, 7.200970 },
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.500000, 10.000000, 31.500000 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.500056, 19.555805, 34.998878 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.293693, 5.789108, 7.279147 },
				evaluateAt(problem, 0.439966, -0.741347, -0.247492, 0.691239, 0.416574, -0.383728, -2.397958, 2.983556, -2.304681, 4.803371, 4.922317, 5.588598, -0.392491, -3.895636, -3.426200, -7.768599, -7.891547, 6.796154, 7.868775, 2.273510, -5.590525, -8.305023, -2.538148, 5.850864, 3.314039, 7.592685, 0.903286, -1.270216, 8.759389, -7.843558).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		assertGeneratedSolutionsAreNondominated(new ZCAT6(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT6_2", 2, true);
		assertProblemDefined("ZCAT6_3", 3, false);
	}
	
}
