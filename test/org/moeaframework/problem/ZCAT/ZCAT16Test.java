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

public class ZCAT16Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT16(3);
		
		Assert.assertArrayEquals(new double[] { 1.462439, 5.019840, 12.053325 },
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.257218, 13.019840, 39.034466 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.253447, 17.019840, 30.072183 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.477041, 4.185910, 11.590992 },
				evaluateAt(problem, 0.279341, 0.171896, 0.377137, 1.416472, 2.052186, 1.695658, -1.579087, -2.432268, -2.418886, -3.673868, -2.690783, 3.284320, -5.996409, -3.726970, -3.924829, -3.164205, -4.356797, 0.382675, 0.302581, 5.823157, 9.908309, 0.465543, -1.807873, 9.883464, 3.964032, -9.848410, -10.900923, -8.289450, -13.954855, -5.211829).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		assertGeneratedSolutionsAreNondominated(new ZCAT16(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT16_2", 2, true);
		assertProblemDefined("ZCAT16_3", 3, false);
	}
	
}
