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

import org.junit.Test;
import org.moeaframework.Assert;

public class DoubleValidatorTest {
	
	@Test
	public void testValidatorType() {
		Assert.assertInstanceOf(DoubleValidator.class, Validate.that("foo", 1.0));
	}

	@Test
	public void testGreaterThan() {
		Validate.that("foo", 2.0).isGreaterThan(1.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 1.0).isGreaterThan(1.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 0.0).isGreaterThan(1.0));
	}
	
	@Test
	public void testGreaterThanOrEqualTo() {
		Validate.that("foo", 1.0).isGreaterThanOrEqualTo(1.0);
		Validate.that("foo", 2.0).isGreaterThanOrEqualTo(1.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 0.0).isGreaterThanOrEqualTo(1.0));
	}
	
	@Test
	public void testLessThan() {
		Validate.that("foo", 0.0).isLessThan(1.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 1.0).isLessThan(1.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 2.0).isLessThan(1.0));
	}
	
	@Test
	public void testLessThanOrEqualTo() {
		Validate.that("foo", 0.0).isLessThanOrEqualTo(1.0);
		Validate.that("foo", 1.0).isLessThanOrEqualTo(1.0);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 2.0).isLessThanOrEqualTo(1.0));
	}
	
	@Test
	public void testBetween() {
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
	public void testFinite() {
		Validate.that("foo", 0.0).isFinite();
		Validate.that("foo", Double.MAX_VALUE).isFinite();
		Validate.that("foo", -Double.MAX_VALUE).isFinite();
		Validate.that("foo", Double.MIN_VALUE).isFinite();
		Validate.that("foo", -Double.MIN_VALUE).isFinite();
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", Double.POSITIVE_INFINITY).isFinite());
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", Double.NEGATIVE_INFINITY).isFinite());
	}
	
	@Test
	public void testNaNsRejected() {
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", Double.NaN).isGreaterThan(1.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", Double.NaN).isGreaterThanOrEqualTo(1.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", Double.NaN).isLessThan(1.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", Double.NaN).isLessThanOrEqualTo(1.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", Double.NaN).isBetween(0.0, 1.0));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", Double.NaN).isFinite());
	}
	
}
