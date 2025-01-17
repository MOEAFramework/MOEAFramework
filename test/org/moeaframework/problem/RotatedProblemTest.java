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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.CallCounter;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.mock.MockProblem;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.util.RotationMatrixBuilder;

public class RotatedProblemTest {
	
	@Test
	public void testIdentity() {
		MockSolution solution = MockSolution.of()
				.withVariables(new RealVariable(0.0, 1.0), new RealVariable(0.0, 1.0))
				.withObjectives(2.0, 3.0, 4.0)
				.withConstraints(2.0, 3.0);
		
		MockProblem problem = new MockProblem(solution) {

			@Override
			public void evaluate(Solution solution) {
				Assert.assertEquals(2, solution.getNumberOfVariables());
				Assert.assertEquals(3, solution.getNumberOfObjectives());
				Assert.assertEquals(2, solution.getNumberOfConstraints());
				Assert.assertArrayEquals(new double[] { 0.25, 0.75 }, RealVariable.getReal(solution), TestThresholds.HIGH_PRECISION);
			}
			
		};
		
		CallCounter<Problem> callCounter = CallCounter.of(problem);
		
		RotationMatrixBuilder builder = new RotationMatrixBuilder(2);
		RotatedProblem rotatedProblem = new RotatedProblem(callCounter.getProxy(), builder.create());
		MockSolution rotatedSolution = MockSolution.of(rotatedProblem).at(0.25, 0.75);
		
		rotatedProblem.evaluate(rotatedSolution);
		
		Assert.assertEquals(1, callCounter.getTotalCallCount("evaluate"));
	}
	
	@Test
	public void testRotation() {
		MockSolution solution = MockSolution.of()
				.withVariables(new RealVariable(0.0, 1.0), new RealVariable(0.0, 1.0))
				.withObjectives(2.0, 3.0, 4.0)
				.withConstraints(2.0, 3.0);
		
		MockProblem problem = new MockProblem(solution) {

			@Override
			public void evaluate(Solution solution) {
				double[] expected = new double[] {
					(0.25 - 0.5) * Math.sqrt(2.0) / 2.0 - (0.75 - 0.5) * Math.sqrt(2.0) / 2.0 + 0.5,
					(0.25 - 0.5) * Math.sqrt(2.0) / 2.0 + (0.75 - 0.5) * Math.sqrt(2.0) / 2.0 + 0.5 };
				
				Assert.assertEquals(2, solution.getNumberOfVariables());
				Assert.assertEquals(3, solution.getNumberOfObjectives());
				Assert.assertEquals(2, solution.getNumberOfConstraints());
				Assert.assertArrayEquals(expected, RealVariable.getReal(solution), TestThresholds.HIGH_PRECISION);
			}
			
		};
		
		CallCounter<Problem> callCounter = CallCounter.of(problem);
		
		RotationMatrixBuilder builder = new RotationMatrixBuilder(2).rotateAll().withThetas(Math.toRadians(45));
		RotatedProblem rotatedProblem = new RotatedProblem(callCounter.getProxy(), builder.create());
		MockSolution rotatedSolution = MockSolution.of(rotatedProblem).at(0.25, 0.75);
		
		rotatedProblem.evaluate(rotatedSolution);
		
		Assert.assertEquals(1, callCounter.getTotalCallCount("evaluate"));
	}

}
