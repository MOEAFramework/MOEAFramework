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

public class DTLZ2Test extends ProblemTest {
	
	@Test
	public void testBounds() {
		Problem problem = new DTLZ2(10, 3);
		
		Assert.assertArrayEquals(new double[] { 3.0, 0.0, 0.0 }, 
				evaluateAtLowerBounds(problem).getObjectives(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 3.0 }, 
				evaluateAtUpperBounds(problem).getObjectives(),
				0.000001);
	}

	@Test
	public void testJMetal2D() {
		testAgainstJMetal("DTLZ2_2");
	}

	@Test
	public void testJMetal3D() {
		testAgainstJMetal("DTLZ2_3");
	}

	@Test
	public void testProvider() {
		assertProblemDefined("DTLZ2_2", 2);
		assertProblemDefined("DTLZ2_3", 3);
	}
	
	@Test
	public void testName() {
		Assert.assertEquals("DTLZ2_2", ProblemFactory.getInstance().getProblem("DTLZ2").getName());
	}
	
	@Test
	public void testEpsilons() {
		assertEpsilonsDefined("DTLZ2_2");
		assertEpsilonsDefined("DTLZ2_3");
	}
	
	@Test
	public void testGenerate() {
		testGenerate("DTLZ2_2", DTLZ2Test::assertParetoFrontSolution);
		testGenerate("DTLZ2_3", DTLZ2Test::assertParetoFrontSolution);
	}
	
	@Test
	public void testReferenceSet() {
		testReferenceSet("DTLZ2_2", DTLZ2Test::assertParetoFrontSolution);
		testReferenceSet("DTLZ2_3", DTLZ2Test::assertParetoFrontSolution);
	}
	
	protected static void assertParetoFrontSolution(Solution solution) {
		double sum = 0.0;
		
		for (int j = 0; j < solution.getNumberOfObjectives(); j++) {
			sum += Math.pow(solution.getObjective(j), 2.0);
		}

		Assert.assertEquals(1.0, Math.sqrt(sum), TestThresholds.LOW_PRECISION);
	}

}
