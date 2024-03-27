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
package org.moeaframework.problem.DTLZ;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.ProblemTest;

public class DTLZ6Test extends ProblemTest {
	
	@Test
	public void testBounds() {
		Problem problem = new DTLZ6(10, 3);
		
		Assert.assertArrayEquals(new double[] { 0.707106, 0.707106, 0.0 }, 
				evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 9.0 }, 
				evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
	}

	@Test
	public void testJMetal2D() {
		testAgainstJMetal("DTLZ6_2");
	}

	@Test
	public void testJMetal3D() {
		testAgainstJMetal("DTLZ6_3");
	}

	@Test
	public void testProvider() {
		assertProblemDefined("DTLZ6_2", 2);
		assertProblemDefined("DTLZ6_3", 3);
	}
	
	@Test
	public void testGenerate() {
		testGenerate("DTLZ6_2", DTLZ6Test::assertParetoFrontSolution);
		testGenerate("DTLZ6_3", DTLZ6Test::assertParetoFrontSolution);
	}
	
	@Test
	public void testReferenceSet() {
		testReferenceSet("DTLZ6_2", DTLZ6Test::assertParetoFrontSolution);
		testReferenceSet("DTLZ6_3", DTLZ6Test::assertParetoFrontSolution);
	}
	
	protected static void assertParetoFrontSolution(Solution solution) {
		double sum = 0.0;
		
		for (int j = 0; j < solution.getNumberOfObjectives(); j++) {
			sum += Math.pow(solution.getObjective(j), 2.0);
		}

		Assert.assertEquals(1.0, Math.sqrt(sum), TestThresholds.SOLUTION_EPS);
	}

}
