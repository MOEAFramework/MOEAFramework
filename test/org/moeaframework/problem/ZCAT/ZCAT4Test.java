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

public class ZCAT4Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT4(3);
		
		Assert.assertArrayEquals(new double[] { 1.136169, 4.778021, 10.680481 }, 
				TestUtils.evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.233394, 5.481752, 21.333909 }, 
				TestUtils.evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.316305, 14.294690, 23.157424 }, 
				TestUtils.evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 1.497537, 6.600155, 19.733788 }, 
				TestUtils.evaluateAt(problem, -0.370835, -0.143374, 0.687622, 0.289698, -0.079764, -0.462331, -0.229383, 0.841106, 0.251233, 2.714333, -3.207807, -1.930301, 1.646572, 5.571272, 3.239873, 3.108082, -1.867829, -0.875798, -0.397578, -9.882711, -5.359110, 4.900919, -8.517916, 7.897280, 6.598649, -1.945588, -4.479490, -5.661023, -2.486237, 1.087901).getObjectives(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		TestUtils.assertGeneratedSolutionsAreNondominated(new ZCAT4(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT4_2", 2, true);
		assertProblemDefined("ZCAT4_3", 3, false);
	}
	
}
