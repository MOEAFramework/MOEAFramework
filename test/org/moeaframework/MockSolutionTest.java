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
package org.moeaframework;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.DTLZ.DTLZ2;

@SuppressWarnings("resource")
public class MockSolutionTest {
	
	@Test
	public void testBuilder() {
		MockSolution mockSolution = MockSolution.of()
				.withObjectives(0.0, 1.0)
				.withConstraints(1.0);
		
		Assert.assertEquals(0, mockSolution.getNumberOfVariables());
		Assert.assertEquals(2, mockSolution.getNumberOfObjectives());
		Assert.assertEquals(1, mockSolution.getNumberOfConstraints());
		Assert.assertArrayEquals(new double[] { 0.0, 1.0 }, mockSolution.getObjectives(), Settings.EPS);
		Assert.assertArrayEquals(new double[] { 1.0 }, mockSolution.getConstraints(), Settings.EPS);
		
		TestUtils.assertEquals(mockSolution, mockSolution.build());
		TestUtils.assertEquals(mockSolution, mockSolution.copy());
		TestUtils.assertEquals(mockSolution, mockSolution.deepCopy());
	}
	
	@Test
	public void testSetters() {
		MockSolution mockSolution = MockSolution.of()
				.withVariables(new RealVariable(0.0, 1.0))
				.withObjectives(0.0, 0.0)
				.withConstraints(0.0);
		
		EncodingUtils.setReal(mockSolution, new double[] { 0.5 });
		mockSolution.setObjective(0, 1.0);
		mockSolution.setConstraint(0, 1.0);
		
		Assert.assertEquals(0.5, EncodingUtils.getReal(mockSolution.getVariable(0)), Settings.EPS);
		Assert.assertEquals(1.0, mockSolution.getObjective(0), Settings.EPS);
		Assert.assertEquals(1.0, mockSolution.getConstraint(0), Settings.EPS);
	}
	
	@Test
	public void testCompare() {
		Problem problem = new DTLZ2(2);
		
		Solution realSolution = problem.newSolution();
		problem.evaluate(realSolution);
		
		MockSolution mockSolution = MockSolution.of(realSolution);
		
		mockSolution.assertEquals(realSolution);
	}
	
	@Test
	public void testEvaluate() {
		Problem problem = new DTLZ2(2);

		MockSolution mockSolution = MockSolution.of(problem).withReals(0.0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5);
		problem.evaluate(mockSolution);
		
		Assert.assertEquals(11, mockSolution.getNumberOfVariables());
		Assert.assertEquals(2, mockSolution.getNumberOfObjectives());
		Assert.assertEquals(0, mockSolution.getNumberOfConstraints());
		
		Assert.assertArrayEquals(new double[] { 1.0, 0.0 }, mockSolution.getObjectives(), Settings.EPS);
	}
	
	@Test(expected = AssertionError.class)
	public void testReadOnly() {
		Problem problem = new DTLZ2(2);

		MockSolution mockSolution = MockSolution.of(problem).randomize().readOnly();
		problem.evaluate(mockSolution);
	}

}
