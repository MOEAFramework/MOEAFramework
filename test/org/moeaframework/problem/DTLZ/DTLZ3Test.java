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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.ProblemTest;

public class DTLZ3Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new DTLZ3(10, 3);
		
		Assert.assertArrayEquals(new double[] { 201.0, 0.0, 0.0 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 201.0 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
	}

	@Test
	public void testJMetal2D() {
		testAgainstJMetal("DTLZ3_2");
	}

	@Test
	public void testJMetal3D() {
		testAgainstJMetal("DTLZ3_3");
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("DTLZ3_2", 2);
		assertProblemDefined("DTLZ3_3", 3);
	}
	
	@Test
	public void testName() {
		Assert.assertEquals("DTLZ3_2", ProblemFactory.getInstance().getProblem("DTLZ3").getName());
	}
	
	@Test
	public void testEpsilons() {
		assertEpsilonsDefined("DTLZ3_2");
		assertEpsilonsDefined("DTLZ3_3");
	}
	
	@Test
	public void testGenerate() {
		testGenerate("DTLZ3_2", DTLZ3Test::assertParetoFrontSolution);
		testGenerate("DTLZ3_3", DTLZ3Test::assertParetoFrontSolution);
	}
	
	@Test
	public void testReferenceSet() {
		testReferenceSet("DTLZ3_2", DTLZ3Test::assertParetoFrontSolution);
		testReferenceSet("DTLZ3_3", DTLZ3Test::assertParetoFrontSolution);
	}
	
	protected static void assertParetoFrontSolution(Solution solution) {
		double sum = 0.0;
		
		for (int j = 0; j < solution.getNumberOfObjectives(); j++) {
			sum += Math.pow(solution.getObjectiveValue(j), 2.0);
		}

		Assert.assertEquals(1.0, Math.sqrt(sum), TestThresholds.LOW_PRECISION);
	}

}
