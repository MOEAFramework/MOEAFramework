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

public class ZCAT17Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT17(3);
		
		Assert.assertArrayEquals(new double[] { 1.769356, 6.913971, 11.411876 },
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.627477, 15.346272, 43.196120 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 5.169556, 18.086915, 31.608882 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.108118, 6.830303, 18.736357 },
				evaluateAt(problem, 0.381744, 0.192930, 1.018495, -1.452971, -0.971630, -0.921319, -0.817430, -2.590116, 1.340213, -0.114376, 2.687711, 5.123537, 5.051055, 5.277635, 4.373355, 0.175044, -6.607048, 6.106702, 8.399712, -1.592255, -6.735093, -3.489696, -0.935984, -8.406567, 4.803897, -11.030065, 5.831859, 11.660102, 12.901155, 6.516655).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		assertGeneratedSolutionsAreNondominated(new ZCAT17(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT17_2", 2, true);
		assertProblemDefined("ZCAT17_3", 3, false);
	}
	
}
