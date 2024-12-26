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
package org.moeaframework.util.validate;

import org.junit.Test;
import org.moeaframework.Assert;

public class ValidatorTest {
	
	@Test(expected = IllegalArgumentException.class)
	public void testFails() {
		Validate.that("foo", 1).fails("This always fails");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFailUnrecognizedOption() {
		try {
			Validate.that("foo", "bar").failUnsupportedOption();
		} catch (IllegalArgumentException e) {
			Assert.assertStringNotContains(e.getMessage(), "valid options are:");
			throw e;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFailUnrecognizedOptionWithSupportedValues() {
		try {
		Validate.that("foo", "bar").failUnsupportedOption("baz");
		} catch (IllegalArgumentException e) {
			Assert.assertStringContains(e.getMessage(), "valid options are: baz");
			throw e;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFailUnsupportedOptionWithEnum() {
		try {
			Validate.that("foo", TestEnum.Foo).failUnsupportedOption();
		} catch (IllegalArgumentException e) {
			Assert.assertStringContains(e.getMessage(), "valid options are: Foo, Bar");
			throw e;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFailUnsupportedOptionWithEnumAsString() {
		try {
			Validate.that("foo", "baz").failUnsupportedOption(TestEnum.class);
		} catch (IllegalArgumentException e) {
			Assert.assertStringContains(e.getMessage(), "valid options are: Foo, Bar");
			throw e;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFailUnsupportedOptionWithNull() {
		try {
			Validate.that("foo", null).failUnsupportedOption();
		} catch (IllegalArgumentException e) {
			Assert.assertStringNotContains(e.getMessage(), "valid options are:");
			throw e;
		}
	}
	
	private enum TestEnum {
		Foo,
		Bar
	}
	
}
