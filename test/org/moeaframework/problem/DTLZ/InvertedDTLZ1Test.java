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

import org.apache.commons.math3.stat.StatUtils;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestEnvironment;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.ProblemTest;
import org.moeaframework.util.Vector;

public class InvertedDTLZ1Test extends ProblemTest {
	
	@Test
	public void test() {
		Problem problem = new InvertedDTLZ1(3);
		
		Assert.assertArrayEquals(new double[] { 63.0, 63.0, 0.0 },
				evaluateAtLowerBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.0, 63.0, 63.0 },
				evaluateAtUpperBounds(problem).getObjectiveValues(),
				0.000001);
		
		Assert.assertArrayEquals(new double[] { 0.375, 0.375, 0.25 },
				evaluateAt(problem, Vector.of(7, 0.5)).getObjectiveValues(),
				0.000001);
	}
	
	@Test
	public void testProvider() {
		assertProblemDefined("Inverted_DTLZ1_2", 2, false);
		assertProblemDefined("Inverted_DTLZ1_3", 3, false);
	}

	@Test
	public void testGenerate() {
		testGenerate("Inverted_DTLZ1_2", InvertedDTLZ1Test::assertParetoFrontSolution);
		testGenerate("Inverted_DTLZ1_3", InvertedDTLZ1Test::assertParetoFrontSolution);
	}
	
	protected static void assertParetoFrontSolution(Solution solution) {
		double sum = StatUtils.sum(solution.getObjectiveValues());
		Assert.assertEquals(0.5 * (solution.getNumberOfObjectives() - 1), sum, TestEnvironment.LOW_PRECISION);
	}

}
