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
package org.moeaframework.mock;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.core.variable.Subset;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;

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
		Assert.assertArrayEquals(new double[] { 0.0, 1.0 }, mockSolution.getObjectiveValues(), TestThresholds.HIGH_PRECISION);
		Assert.assertArrayEquals(new double[] { 1.0 }, mockSolution.getConstraintValues(), TestThresholds.HIGH_PRECISION);
		
		Assert.assertEquals(mockSolution, mockSolution.build());
		Assert.assertEquals(mockSolution, mockSolution.copy());
		Assert.assertEquals(mockSolution, mockSolution.deepCopy());
	}
	
	@Test
	public void testSetters() {
		MockSolution mockSolution = MockSolution.of()
				.withVariables(new RealVariable(0.0, 1.0))
				.withObjectives(0.0, 0.0)
				.withConstraints(0.0);
		
		EncodingUtils.setReal(mockSolution, new double[] { 0.5 });
		mockSolution.setObjectiveValue(0, 1.0);
		mockSolution.setConstraintValue(0, 1.0);
		
		Assert.assertEquals(0.5, EncodingUtils.getReal(mockSolution.getVariable(0)), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(1.0, mockSolution.getObjectiveValue(0), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(1.0, mockSolution.getConstraintValue(0), TestThresholds.HIGH_PRECISION);
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

		MockSolution mockSolution = MockSolution.of(problem).at(0.0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5);
		problem.evaluate(mockSolution);
		
		Assert.assertEquals(11, mockSolution.getNumberOfVariables());
		Assert.assertEquals(2, mockSolution.getNumberOfObjectives());
		Assert.assertEquals(0, mockSolution.getNumberOfConstraints());
		
		Assert.assertArrayEquals(new double[] { 1.0, 0.0 }, mockSolution.getObjectiveValues(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test(expected = AssertionError.class)
	public void testReadOnly() {
		Problem problem = new DTLZ2(2);

		MockSolution mockSolution = MockSolution.of(problem).randomize().readOnly();
		problem.evaluate(mockSolution);
	}
	
	@Test
	public void testAtLowerBound() {
		Assert.assertEquals(-1.0,
				EncodingUtils.getReal(MockSolution.of().withVariables(new RealVariable(-1.0, 1.0)).atLowerBounds().getVariables()[0]),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertEquals(5,
				EncodingUtils.getInt(MockSolution.of().withVariables(new BinaryIntegerVariable(5, 10)).atLowerBounds().getVariables()[0]));
		
		Assert.assertEquals(0,
				EncodingUtils.getBitSet(MockSolution.of().withVariables(new BinaryVariable(5)).atLowerBounds().getVariables()[0]).cardinality());
		
		Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 4 },
				EncodingUtils.getPermutation(MockSolution.of().withVariables(new Permutation(5)).atLowerBounds().getVariables()[0]));
		
		Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 4 },
				EncodingUtils.getSubset(MockSolution.of().withVariables(new Subset(5, 10)).atLowerBounds().getVariables()[0]));
	}
	
	@Test
	public void testAtUpperBound() {
		Assert.assertEquals(1.0,
				EncodingUtils.getReal(MockSolution.of().withVariables(new RealVariable(-1.0, 1.0)).atUpperBounds().getVariables()[0]),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertEquals(10,
				EncodingUtils.getInt(MockSolution.of().withVariables(new BinaryIntegerVariable(5, 10)).atUpperBounds().getVariables()[0]));
		
		Assert.assertEquals(5,
				EncodingUtils.getBitSet(MockSolution.of().withVariables(new BinaryVariable(5)).atUpperBounds().getVariables()[0]).cardinality());
		
		Assert.assertArrayEquals(new int[] { 4, 3, 2, 1, 0 },
				EncodingUtils.getPermutation(MockSolution.of().withVariables(new Permutation(5)).atUpperBounds().getVariables()[0]));
		
		Assert.assertArrayEquals(new int[] { 5, 6, 7, 8, 9 },
				EncodingUtils.getSubset(MockSolution.of().withVariables(new Subset(5, 10)).atUpperBounds().getVariables()[0]));
	}

}
