/* Copyright 2009-2022 David Hadka
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
package org.moeaframework.algorithm.jmetal;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.MockConstraintProblem;
import org.uma.jmetal.solution.DoubleSolution;

/**
 * Tests the {@link ProblemAdapter} class.
 */
public class ProblemAdapterTest {
	
	private class TestProblemAdapter<T extends org.uma.jmetal.solution.Solution<?>> extends ProblemAdapter<T> {
		
		private static final long serialVersionUID = -4544075640184829372L;

		public TestProblemAdapter(Problem problem) {
			super(problem);
		}

		@Override
		public T createSolution() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Solution convert(T solution) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	@Test
	public void testDefaultMethods() {
		MockConstraintProblem problem = new MockConstraintProblem();
		TestProblemAdapter<DoubleSolution> adapter = new TestProblemAdapter<DoubleSolution>(problem);
		
		Assert.assertEquals(problem.getName(), adapter.getName());
		Assert.assertEquals(problem.getNumberOfVariables(), adapter.getNumberOfVariables());
		Assert.assertEquals(problem.getNumberOfObjectives(), adapter.getNumberOfObjectives());
		Assert.assertEquals(problem.getNumberOfConstraints(), adapter.getNumberOfConstraints());
		Assert.assertEquals(problem.getNumberOfVariables(), adapter.getNumberOfMutationIndices());
	}
	
	@Test
	public void testEvaluate() {
		MockConstraintProblem problem = new MockConstraintProblem();
		ProblemAdapter<DoubleSolution> adapter = new DoubleProblemAdapter(problem);
		
		DoubleSolution solution = adapter.createSolution();
		adapter.evaluate(solution);
		
		Assert.assertEquals(problem.getNumberOfVariables(), solution.getNumberOfVariables());
		Assert.assertEquals(problem.getNumberOfObjectives(), solution.getNumberOfObjectives());
		Assert.assertEquals(5.0, solution.getObjective(0), Settings.EPS);
		Assert.assertEquals(-35.0, JMetalUtils.getOverallConstraintViolation(solution), Settings.EPS);
		Assert.assertEquals(2, JMetalUtils.getNumberOfViolatedConstraints(solution));
	}

}
