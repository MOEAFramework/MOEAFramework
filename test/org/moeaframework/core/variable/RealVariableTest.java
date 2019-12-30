/* Copyright 2009-2019 David Hadka
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
import org.moeaframework.core.Settings;

public class RealVariableTest {

	private RealVariable value;

	@Before
	public void setUp() {
		value = new RealVariable(0.5, 0.0, 1.0);
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		value = null;
	}
	
	@Test
	public void testEmptyConstructor() {
		Assert.assertTrue(Double.isNaN(new RealVariable(0.0, 1.0).getValue()));
	}

	@Test
	public void testGetValue() {
		Assert.assertEquals(0.5, value.getValue(), Settings.EPS);
		Assert.assertEquals(0.0, value.getLowerBound(), Settings.EPS);
		Assert.assertEquals(1.0, value.getUpperBound(), Settings.EPS);
	}

	@Test
	public void testSetValue() {
		value.setValue(0.75);
		Assert.assertEquals(0.75, value.getValue(), Settings.EPS);
	}

	@Test
	public void testEquals() {
		Assert.assertFalse(value.equals(null));
		Assert.assertTrue(value.equals(value));
		Assert.assertTrue(value.equals(new RealVariable(0.5, 0.0, 1.0)));
		Assert.assertFalse(value.equals(new RealVariable(0.75, 0.0, 1.0)));
		Assert.assertFalse(value.equals(new RealVariable(0.5, 0.25, 1.0)));
		Assert.assertFalse(value.equals(new RealVariable(0.5, 0.0, 0.75)));
	}

	@Test
	public void testHashCode() {
		Assert.assertEquals(value.hashCode(), value.hashCode());
		Assert.assertEquals(value.hashCode(), new RealVariable(0.5, 0.0, 1.0)
				.hashCode());
	}

	@Test
	public void testCopy() {
		RealVariable copy = value.copy();
		Assert.assertTrue(copy.equals(value));

		copy.setValue(0.75);
		Assert.assertEquals(0.5, value.getValue(), Settings.EPS);
		Assert.assertFalse(copy.equals(value));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorBoundsCheckLower() {
		new RealVariable(0.0 - Settings.EPS, 0.0, 1.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorBoundsCheckUpper() {
		new RealVariable(1.0 + Settings.EPS, 0.0, 1.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetValueBoundsCheckLower() {
		value.setValue(value.getLowerBound() - Settings.EPS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetValueBoundsCheckUpper() {
		value.setValue(value.getUpperBound() + Settings.EPS);
	}

}
