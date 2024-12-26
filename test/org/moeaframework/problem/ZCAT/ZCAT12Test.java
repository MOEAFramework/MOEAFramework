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
package org.moeaframework.problem.ZCAT;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class ZCAT12Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT12(3);
		
		Assert.assertArrayEquals(new double[] { 1.479003, 6.240014, 11.794938 },
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.231115, 17.230625, 38.794938 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.226891, 17.249402, 29.794938 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.731858, 7.113652, 15.453434 },
				evaluateAt(problem, 0.009590, -0.101814, 0.785681, 1.185700, -0.803837, -1.051418, 1.822063, -2.512954, -4.353886, 3.661911, -0.085392, -5.295348, 4.885863, 5.771219, 4.500573, -6.335023, -4.885735, 2.058493, -3.675596, 3.084957, -6.064603, 4.178104, 1.701042, 4.046147, -11.800635, 0.519961, -1.976740, 12.116186, 10.934561, -14.943509).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		assertGeneratedSolutionsAreNondominated(new ZCAT12(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT12_2", 2, true);
		assertProblemDefined("ZCAT12_3", 3, false);
	}
	
}
