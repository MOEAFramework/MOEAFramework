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
package org.moeaframework.util.validate;

import java.util.List;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Population;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.mock.MockConstraintProblem;
import org.moeaframework.mock.MockMixedBinaryProblem;
import org.moeaframework.mock.MockMultiTypeProblem;
import org.moeaframework.mock.MockRealProblem;

public class ValidateTest {

	@Test
	public void testGreaterThan() {
		Validate.that("foo", 2).isGreaterThan(1);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 1).isGreaterThan(1));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 0).isGreaterThan(1));
		
		Validate.that("foo", 2.0).isGreaterThan(1.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 1.0).isGreaterThan(1.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 0.0).isGreaterThan(1.0));
	}
	
	@Test
	public void testGreaterThanOrEqualTo() {
		Validate.that("foo", 1).isGreaterThanOrEqualTo(1);
		Validate.that("foo", 2).isGreaterThanOrEqualTo(1);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 0).isGreaterThanOrEqualTo(1));
		
		Validate.that("foo", 1.0).isGreaterThanOrEqualTo(1.0);
		Validate.that("foo", 2.0).isGreaterThanOrEqualTo(1.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 0.0).isGreaterThanOrEqualTo(1.0));
	}
	
	@Test
	public void testLessThan() {
		Validate.that("foo", 0).isLessThan(1);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 1).isLessThan(1));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 2).isLessThan(1));
		
		Validate.that("foo", 0.0).isLessThan(1.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 1.0).isLessThan(1.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 2.0).isLessThan(1.0));
	}
	
	@Test
	public void testLessThanOrEqualTo() {
		Validate.that("foo", 0).isLessThanOrEqualTo(1);
		Validate.that("foo", 1).isLessThanOrEqualTo(1);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 2).isLessThanOrEqualTo(1));
		
		Validate.that("foo", 0.0).isLessThanOrEqualTo(1.0);
		Validate.that("foo", 1.0).isLessThanOrEqualTo(1.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 2.0).isLessThanOrEqualTo(1.0));
	}
	
	@Test
	public void testBetween() {
		Validate.that("foo", 5).isBetween(5, 10);
		Validate.that("foo", 7).isBetween(5, 10);
		Validate.that("foo", 10).isBetween(5, 10);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 4).isBetween(5, 10));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 11).isBetween(5, 10));
		
		Validate.that("foo", 5.0).isBetween(5.0, 10.0);
		Validate.that("foo", 7.0).isBetween(5.0, 10.0);
		Validate.that("foo", 10.0).isBetween(5.0, 10.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 4.0).isBetween(5.0, 10.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 11.0).isBetween(5.0, 10.0));
	}
	
	@Test
	public void testStrictlyBetween() {
		Validate.that("foo", 7.0).isStrictlyBetween(5.0, 10.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 5.0).isStrictlyBetween(5.0, 10.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 10.0).isStrictlyBetween(5.0, 10.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 4.0).isStrictlyBetween(5.0, 10.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 11.0).isStrictlyBetween(5.0, 10.0));
	}
	
	@Test
	public void testProbability() {
		Validate.that("foo", 0.0).isProbability();
		Validate.that("foo", 0.5).isProbability();
		Validate.that("foo", 1.0).isProbability();
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", -0.01).isProbability());
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 1.01).isProbability());
	}
	
	@Test
	public void testNotNull() {
		Validate.that("foo", new double[0]).isNotNull();
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", null).isNotNull());
	}
	
	@Test
	public void testNotEmpty() {
		Validate.that("foo", new double[1]).isNotEmpty();
		Validate.that("foo", List.of(5)).isNotEmpty();
		Validate.that("foo", new Object()).isNotEmpty();
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", null).isNotEmpty());
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", new double[0]).isNotEmpty());
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", List.of()).isNotEmpty());
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", new Population()).isNotEmpty());
	}
	
	@Test
	public void testUnconstrained() {
		Validate.that("problem", new MockRealProblem()).isUnconstrained();
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("problem", new MockConstraintProblem()).isUnconstrained());
	}
	
	@Test
	public void testProblemType() {
		Validate.that("problem", new MockRealProblem()).isType(RealVariable.class);
		Validate.that("problem", new MockMixedBinaryProblem()).isType(BinaryVariable.class);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("problem", new MockRealProblem()).isType(BinaryVariable.class));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("problem", new MockMultiTypeProblem()).isType(BinaryVariable.class));
	}
	
}
