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

public class IntegerValidatorTest {
	
	@Test
	public void testValidatorType() {
		Assert.assertInstanceOf(IntegerValidator.class, Validate.that("foo", 1));
	}

	@Test
	public void testGreaterThan() {
		Validate.that("foo", 2).isGreaterThan(1);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 1).isGreaterThan(1));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 0).isGreaterThan(1));
	}
	
	@Test
	public void testGreaterThanOrEqualTo() {
		Validate.that("foo", 1).isGreaterThanOrEqualTo(1);
		Validate.that("foo", 2).isGreaterThanOrEqualTo(1);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 0).isGreaterThanOrEqualTo(1));
	}
	
	@Test
	public void testLessThan() {
		Validate.that("foo", 0).isLessThan(1);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 1).isLessThan(1));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 2).isLessThan(1));
	}
	
	@Test
	public void testLessThanOrEqualTo() {
		Validate.that("foo", 0).isLessThanOrEqualTo(1);
		Validate.that("foo", 1).isLessThanOrEqualTo(1);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 2).isLessThanOrEqualTo(1));
	}
	
	@Test
	public void testBetween() {
		Validate.that("foo", 5).isBetween(5, 10);
		Validate.that("foo", 7).isBetween(5, 10);
		Validate.that("foo", 10).isBetween(5, 10);
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 4).isBetween(5, 10));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", 11).isBetween(5, 10));
	}
	
}
