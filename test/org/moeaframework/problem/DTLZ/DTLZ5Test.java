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
package org.moeaframework.problem.DTLZ;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestEnvironment;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;

public class DTLZ5Test extends ProblemTest {
	
	@Test
	public void testBounds() {
		Problem problem = new DTLZ5(10, 3);
		
		Assert.assertArrayEquals(new double[] { 2.8977774, 0.776457, 0.0 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 3.0 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
	}

	@Test
	public void testJMetal2D() {
		testAgainstJMetal("DTLZ5_2");
	}

	@Test
	public void testJMetal3D() {
		testAgainstJMetal("DTLZ5_3");
	}

	@Test
	public void testProvider() {
		assertProblemDefined("DTLZ5_2", 2);
		assertProblemDefined("DTLZ5_3", 3);
	}
	
	@Test
	public void testName() {
		Assert.assertEquals("DTLZ5_2", ProblemFactory.getInstance().getProblem("DTLZ5").getName());
	}
	
	@Test
	public void testEpsilons() {
		assertEpsilonsDefined("DTLZ5_2");
		assertEpsilonsDefined("DTLZ5_3");
	}
	
	@Test
	public void testGenerate() {
		testGenerate("DTLZ5_2", DTLZ5Test::assertParetoFrontSolution);
		testGenerate("DTLZ5_3", DTLZ5Test::assertParetoFrontSolution);
	}
	
	@Test
	public void testReferenceSet() {
		testReferenceSet("DTLZ5_2", DTLZ5Test::assertParetoFrontSolution);
		testReferenceSet("DTLZ5_3", DTLZ5Test::assertParetoFrontSolution);
	}
	
	protected static void assertParetoFrontSolution(Solution solution) {
		double sum = 0.0;
		
		for (int j = 0; j < solution.getNumberOfObjectives(); j++) {
			sum += Math.pow(solution.getObjectiveValue(j), 2.0);
		}

		Assert.assertEquals(1.0, Math.sqrt(sum), TestEnvironment.LOW_PRECISION);
	}

}
