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
package org.moeaframework.problem;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestResources;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.mock.MockProblem;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.DTLZ.DTLZ2;

@SuppressWarnings("resource")
public class ScaledProblemTest {
	
	@Test
	public void testReferenceSetStaysNondominated() throws IOException {
		File file = TestResources.asFile("pf/DTLZ2.2D.pf");
		
		NondominatedPopulation original = NondominatedPopulation.load(file);
		
		ScaledProblem scaledProblem = new ScaledProblem(new DTLZ2(2), 2.0);
		NondominatedPopulation scaled = scaledProblem.loadScaledReferenceSet(file);
		
		Assert.assertEquals(original.size(), scaled.size());
	}
	
	@Test
	public void testScaledValues() {
		MockSolution solution = MockSolution.of()
				.withVariables(new RealVariable(0.0, 1.0))
				.withObjectives(2.0, 3.0, 4.0)
				.withConstraints(2.0, 3.0);
		
		ScaledProblem problem = new ScaledProblem(MockProblem.of(solution), 2.0);
		problem.evaluate(solution);
		
		Assert.assertArrayEquals(new double[] { 2.0, 6.0, 16.0 }, solution.getObjectiveValues(), TestThresholds.HIGH_PRECISION);
		Assert.assertArrayEquals(new double[] { 2.0, 3.0 }, solution.getConstraintValues(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testBase1LeavesSolutionUnchanged() {
		MockSolution solution = MockSolution.of()
				.withVariables(new RealVariable(0.0, 1.0))
				.withObjectives(2.0, 3.0, 4.0)
				.withConstraints(2.0, 3.0);
		
		ScaledProblem problem = new ScaledProblem(MockProblem.of(solution), 1.0);
		problem.evaluate(solution);
		
		Assert.assertArrayEquals(new double[] { 2.0, 3.0, 4.0 }, solution.getObjectiveValues(), TestThresholds.HIGH_PRECISION);
		Assert.assertArrayEquals(new double[] { 2.0, 3.0 }, solution.getConstraintValues(), TestThresholds.HIGH_PRECISION);
	}

}
