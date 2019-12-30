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

import java.util.BitSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BinaryVariableTest {

	private BinaryVariable value;

	@Before
	public void setUp() {
		value = new BinaryVariable(2);
		value.set(1, true);
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		value = null;
	}
	
	@Test
	public void testToString() {
		Assert.assertEquals("01", value.toString());
	}

	@Test
	public void testGetValue() {
		Assert.assertEquals(2, value.getNumberOfBits());
		Assert.assertFalse(value.get(0));
		Assert.assertTrue(value.get(1));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetValueBoundsCheckLower() {
		value.get(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetValueBoundsCheckUpper() {
		value.get(3);
	}

	@Test
	public void testSetValue() {
		value.set(0, true);
		value.set(1, false);

		Assert.assertTrue(value.get(0));
		Assert.assertFalse(value.get(1));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetValueBoundsCheckLower() {
		value.set(-1, false);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetValueBoundsCheckUpper() {
		value.set(3, false);
	}

	@Test
	public void testEquals() {
		Assert.assertFalse(value.equals(null));
		Assert.assertTrue(value.equals(value));

		BinaryVariable trueCase = new BinaryVariable(2);
		trueCase.set(1, true);
		Assert.assertTrue(value.equals(trueCase));

		BinaryVariable falseCase1 = new BinaryVariable(2);
		falseCase1.set(0, true);
		Assert.assertFalse(value.equals(falseCase1));

		BinaryVariable falseCase2 = new BinaryVariable(3);
		falseCase2.set(1, true);
		Assert.assertFalse(value.equals(falseCase2));
	}
	
	@Test
	public void testHashCode() {
		Assert.assertEquals(value.hashCode(), value.hashCode());
		
		BinaryVariable bv = new BinaryVariable(2);
		bv.set(1, true);
		Assert.assertEquals(value.hashCode(), bv.hashCode());
	}

	@Test
	public void testCopy() {
		BinaryVariable copy = value.copy();
		Assert.assertTrue(copy.equals(value));

		copy.set(1, false);
		Assert.assertTrue(value.get(1));
		Assert.assertFalse(copy.equals(value));
	}

	@Test
	public void testClear() {
		value.clear();
		Assert.assertEquals(2, value.getNumberOfBits());
		Assert.assertEquals(0, value.cardinality());
	}

	@Test
	public void testIsEmpty() {
		Assert.assertFalse(value.isEmpty());

		value.clear();
		Assert.assertTrue(value.isEmpty());
	}

	@Test
	public void testCardinality() {
		Assert.assertEquals(1, value.cardinality());

		value.clear();
		Assert.assertEquals(0, value.cardinality());
	}

	@Test
	public void testGetBitSet() {
		BitSet bitSet = value.getBitSet();

		Assert.assertEquals(2, bitSet.length());
		Assert.assertFalse(bitSet.get(0));
		Assert.assertTrue(bitSet.get(1));

		// ensure the returned BitSet is independent of the BinaryVariable
		bitSet.set(0);
		Assert.assertFalse(value.get(0));
	}
	
	@Test
	public void testHammingDistance() {
		BinaryVariable b1 = new BinaryVariable(5);
		BinaryVariable b2 = new BinaryVariable(5);
		
		b1.set(2, true);
		b1.set(4, true);
		b2.set(2, true);
		b2.set(3, true);
		
		Assert.assertEquals(2, b1.hammingDistance(b2));
		Assert.assertEquals(0, b1.hammingDistance(b1));
		Assert.assertEquals(0, b2.hammingDistance(b2));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testHammingDistanceLength() {
		BinaryVariable b1 = new BinaryVariable(5);
		BinaryVariable b2 = new BinaryVariable(6);
		
		b1.hammingDistance(b2);
	}

}
