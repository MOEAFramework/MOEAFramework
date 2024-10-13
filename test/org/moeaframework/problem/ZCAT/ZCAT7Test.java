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

public class ZCAT7Test extends ProblemTest {

	@Test
	public void test() {
		Problem problem = new ZCAT7(3);
		
		Assert.assertArrayEquals(new double[] { 1.313224, 4.976575, 11.545471 }, 
				evaluateAt(problem, Vector.of(problem.getNumberOfVariables(), 0.0)).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 3.068842, 13.617866, 39.546382 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 4.557605, 16.335284, 28.544560 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 2.100773, 10.342234, 12.020951 }, 
				evaluateAt(problem, 0.348970, 0.705492, 0.864410, 1.259723, -0.837834, -2.204263, 0.955083, -3.224799, -2.436147, 2.681704, 5.404753, 5.229724, 1.976725, -1.334688, 6.895635, -3.258287, 4.507806, 8.316556, 3.042343, 1.550775, -2.777561, -3.161572, 4.803347, -2.142699, -6.152846, -4.195338, 9.409657, 6.114285, 8.966450, 1.007867).getObjectiveValues(),
				0.0001);
	}
	
	@Test
	public void testGenerate() {
		assertGeneratedSolutionsAreNondominated(new ZCAT7(3), 1000);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("ZCAT7_2", 2, true);
		assertProblemDefined("ZCAT7_3", 3, false);
	}
	
}
