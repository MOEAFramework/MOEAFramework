/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.core.variable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BinaryIntegerVariableTest {

	private BinaryIntegerVariable value;

	@Before
	public void setUp() {
		value = new BinaryIntegerVariable(7, 5, 10);
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		value = null;
	}

	@Test
	public void testGetValue() {
		Assert.assertEquals(7, value.getValue());
		Assert.assertEquals(5, value.getLowerBound());
		Assert.assertEquals(10, value.getUpperBound());
	}

	@Test
	public void testSetValue() {
		for (int i = value.getLowerBound(); i <= value.getUpperBound(); i++) {
			value.setValue(i);
			Assert.assertEquals(i, value.getValue());
		}
	}

	@Test
	public void testEquals() {
		Assert.assertFalse(value.equals(null));
		Assert.assertTrue(value.equals(value));
		Assert.assertTrue(value.equals(new BinaryIntegerVariable(7, 5, 10)));
		Assert.assertFalse(value.equals(new BinaryIntegerVariable(9, 5, 10)));
		Assert.assertFalse(value.equals(new BinaryIntegerVariable(7, 2, 10)));
		Assert.assertFalse(value.equals(new BinaryIntegerVariable(7, 5, 9)));
	}

	@Test
	public void testHashCode() {
		Assert.assertEquals(value.hashCode(), value.hashCode());
		Assert.assertEquals(value.hashCode(),
				new BinaryIntegerVariable(7, 5, 10).hashCode());
	}

	@Test
	public void testCopy() {
		BinaryIntegerVariable copy = value.copy();
		Assert.assertTrue(copy.equals(value));
		Assert.assertEquals(value.getBitSet(), copy.getBitSet());

		copy.setValue(9);
		Assert.assertEquals(7, value.getValue());
		Assert.assertFalse(copy.equals(value));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorBoundsCheckLower() {
		new BinaryIntegerVariable(-1, 0, 10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorBoundsCheckUpper() {
		new BinaryIntegerVariable(11, 0, 10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetValueBoundsCheckLower() {
		value.setValue(4);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetValueBoundsCheckUpper() {
		value.setValue(11);
	}

}
