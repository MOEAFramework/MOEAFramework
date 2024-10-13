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
import org.moeaframework.core.population.Population;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.core.variable.Variable;

public class ObjectValidatorTest {
	
	@Test
	public void testValidatorType() {
		Assert.assertInstanceOf(ObjectValidator.class, Validate.that("foo", new double[1]));
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
		Validate.that("foo", "bar").isNotEmpty();
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", null).isNotEmpty());
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", new double[0]).isNotEmpty());
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", List.of()).isNotEmpty());
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", new Population()).isNotEmpty());
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", "").isNotEmpty());
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", " \t\r\n").isNotEmpty());
	}
	
	@Test
	public void testIsA() {
		Variable variable = new RealVariable(0.0, 1.0);
		Assert.assertNotNull(Validate.that("foo", variable).isA(RealVariable.class));
		Assert.assertThrows(IllegalArgumentException.class, () -> Validate.that("foo", variable).isA(BinaryVariable.class));
	}
	
}
