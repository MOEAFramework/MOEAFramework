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
package org.moeaframework.core.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.mock.MockConstraintProblem;
import org.moeaframework.problem.mock.MockMixedBinaryProblem;
import org.moeaframework.problem.mock.MockMultiTypeProblem;
import org.moeaframework.problem.mock.MockRealProblem;

public class ValidateTest {
	
	@Test
	public void testGreaterThanZero() {
		Validate.greaterThanZero("foo", 1);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.greaterThanZero("foo", 0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.greaterThanZero("foo", -1));
		
		Validate.greaterThanZero("foo", 1.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.greaterThanZero("foo", 0.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.greaterThanZero("foo", -1.0));
	}
		
	@Test
	public void testGreaterThanOrEqualToZero() {
		Validate.greaterThanOrEqualToZero("foo", 0);
		Validate.greaterThanOrEqualToZero("foo", 1);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.greaterThanOrEqualToZero("foo", -1));
		
		Validate.greaterThanOrEqualToZero("foo", 0.0);
		Validate.greaterThanOrEqualToZero("foo", 1.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.greaterThanOrEqualToZero("foo", -1.0));
	}
	
	@Test
	public void testGreaterThan() {
		Validate.greaterThan("foo", 1, 2);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.greaterThan("foo", 1, 1));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.greaterThan("foo", 1, 0));
		
		Validate.greaterThan("foo", 1.0, 2.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.greaterThan("foo", 1.0, 1.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.greaterThan("foo", 1.0, 0.0));
	}
	
	@Test
	public void testGreaterThanOrEqual() {
		Validate.greaterThanOrEqual("foo", 1, 1);
		Validate.greaterThanOrEqual("foo", 1, 2);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.greaterThanOrEqual("foo", 1, 0));
		
		Validate.greaterThanOrEqual("foo", 1.0, 1.0);
		Validate.greaterThanOrEqual("foo", 1.0, 2.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.greaterThanOrEqual("foo", 1.0, 0.0));
	}
	
	@Test
	public void testInclusiveBetween() {
		Validate.inclusiveBetween("foo", 5, 10, 5);
		Validate.inclusiveBetween("foo", 5, 10, 7);
		Validate.inclusiveBetween("foo", 5, 10, 10);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.inclusiveBetween("foo", 5, 10, 4));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.inclusiveBetween("foo", 5, 10, 11));
		
		Validate.inclusiveBetween("foo", 5.0, 10.0, 5.0);
		Validate.inclusiveBetween("foo", 5.0, 10.0, 7.0);
		Validate.inclusiveBetween("foo", 5.0, 10.0, 10.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.inclusiveBetween("foo", 5.0, 10.0, 4.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.inclusiveBetween("foo", 5.0, 10.0, 11.0));
	}
	
	@Test
	public void testProbability() {
		Validate.probability("foo", 0.0);
		Validate.probability("foo", 0.5);
		Validate.probability("foo", 1.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.probability("foo", -0.01));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.probability("foo", 1.01));
	}
	
	@Test
	public void testNotNull() {
		Validate.notNull("foo", new double[0]);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.notNull("foo", null));
	}
	
	@Test
	public void testProblemHasNoConstraints() {
		Validate.problemHasNoConstraints(new MockRealProblem());
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.problemHasNoConstraints(new MockConstraintProblem()));
	}
	
	@Test
	public void testProblemType() {
		Validate.problemType(new MockRealProblem(), RealVariable.class);
		Validate.problemType(new MockMixedBinaryProblem(), BinaryVariable.class);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.problemType(new MockRealProblem(), BinaryVariable.class));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.problemType(new MockMultiTypeProblem(), BinaryVariable.class));
	}
	
}
