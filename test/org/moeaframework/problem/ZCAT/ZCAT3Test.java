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

public class ZCAT3Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT3(3);
		
		Assert.assertArrayEquals(new double[] { 0.576915, 2.317877, 5.203125 },
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.500000, 12.000000, 31.500000 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.500056, 17.555805, 34.998878 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.285791, 3.644319, 7.052878 },
				evaluateAt(problem, 0.199976, 0.864537, 0.277067, 1.123872, 2.026562, 1.418068, 1.409816, -1.805995, 3.701700, 3.439141, 1.370076, -4.062809, -2.375099, -1.875144, 0.882099, 4.580727, -0.231853, -8.675746, -0.664547, -3.070162, -2.617749, -1.865204, -1.463649, 0.509115, 9.378694, -5.804617, -4.640183, 3.301225, 13.004847, 2.888292).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		assertGeneratedSolutionsAreNondominated(new ZCAT3(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT3_2", 2, true);
		assertProblemDefined("ZCAT3_3", 3, false);
	}
	
}
