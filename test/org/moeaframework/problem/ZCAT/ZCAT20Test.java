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

public class ZCAT20Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT20(3);
		
		Assert.assertArrayEquals(new double[] { 1.840315, 6.845845, 15.193698 },
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.179539, 14.131285, 40.409218 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.501091, 19.560405, 34.978177 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.755148, 5.612683, 26.130553 },
				evaluateAt(problem, 0.282270, 0.816827, 0.340810, -0.903613, -1.235864, 2.196344, 1.042931, 2.790136, -3.329406, 4.530856, -1.162009, -5.322769, 3.415518, -0.445302, -0.921937, -3.035557, 5.749473, 3.760910, -1.231435, -9.856307, 7.135791, -1.939915, 7.305066, 4.073053, -0.949078, -12.501428, 0.693517, -3.202567, -12.044570, 1.963300).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		assertGeneratedSolutionsAreNondominated(new ZCAT20(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT20_2", 2, true);
		assertProblemDefined("ZCAT20_3", 3, false);
	}
	
}
