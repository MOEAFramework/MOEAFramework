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

import org.apache.commons.math3.stat.StatUtils;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;

public class DTLZ1Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new DTLZ1(3);
		
		Assert.assertArrayEquals(new double[] { 0.0, 0.0, 63.0 }, 
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 63.0, 0.0, 0.0 }, 
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
	}

	@Test
	public void testJMetal2D() {
		testAgainstJMetal("DTLZ1_2");
	}
	
	@Test
	public void testJMetal3D() {
		testAgainstJMetal("DTLZ1_3");
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("DTLZ1_2", 2);
		assertProblemDefined("DTLZ1_3", 3);
	}
	
	@Test
	public void testName() {
		Assert.assertEquals("DTLZ1_2", ProblemFactory.getInstance().getProblem("DTLZ1").getName());
	}
	
	@Test
	public void testEpsilons() {
		assertEpsilonsDefined("DTLZ1_2");
		assertEpsilonsDefined("DTLZ1_3");
	}

	@Test
	public void testGenerate() {
		testGenerate("DTLZ1_2", DTLZ1Test::assertParetoFrontSolution);
		testGenerate("DTLZ1_3", DTLZ1Test::assertParetoFrontSolution);
	}
	
	@Test
	public void testReferenceSet() {
		testReferenceSet("DTLZ1_2", DTLZ1Test::assertParetoFrontSolution);
		testReferenceSet("DTLZ1_3", DTLZ1Test::assertParetoFrontSolution);
	}
	
	protected static void assertParetoFrontSolution(Solution solution) {
		double sum = StatUtils.sum(solution.getObjectiveValues());
		Assert.assertEquals(0.5, sum, TestThresholds.LOW_PRECISION);
	}

}
