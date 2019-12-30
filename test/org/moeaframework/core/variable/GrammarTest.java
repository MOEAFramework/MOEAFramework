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

/**
 * Tests the {@link Grammar} class.
 */
public class GrammarTest {

	/**
	 * The grammar instance used for testing.
	 */
	private Grammar grammar;

	/**
	 * Sets up the grammar instance used for testing.
	 */
	@Before
	public void setUp() {
		grammar = new Grammar(new int[] { 0, 1, 2, 3, 4 });
	}

	/**
	 * Destroys the grammar instance used for testing.
	 */
	@After
	public void tearDown() {
		grammar = null;
	}

	/**
	 * Tests the size constructor to ensure the grammar is initialized
	 * correctly.
	 */
	@Test
	public void testSizeConstructor() {
		Grammar grammar = new Grammar(5);
		Assert.assertEquals(5, grammar.size());
	}

	/**
	 * Tests the array constructor to ensure the grammar contains an independent
	 * copy of the array.
	 */
	@Test
	public void testArrayConstructor() {
		int[] array = new int[] { 0, 1, 2, 3 };
		Grammar grammar = new Grammar(array);

		Assert.assertEquals(4, grammar.size());

		for (int i = 0; i < grammar.size(); i++) {
			Assert.assertEquals(array[i], grammar.get(i));
		}

		// ensure stored array is independent from argument
		array[1] = 0;
		Assert.assertEquals(1, grammar.get(1));
	}

	/**
	 * Tests if the correct size is returned.
	 */
	@Test
	public void testSize() {
		Assert.assertEquals(5, grammar.size());
	}

	/**
	 * Tests if the {@link Grammar#equals} method works correctly.
	 */
	@Test
	public void testEquals() {
		Assert.assertFalse(grammar.equals(null));
		Assert.assertTrue(grammar.equals(grammar));
		Assert.assertTrue(grammar.equals(new Grammar(
				new int[] { 0, 1, 2, 3, 4 })));
		Assert.assertFalse(grammar.equals(new Grammar(0)));
		Assert.assertFalse(grammar
				.equals(new Grammar(new int[] { 0, 1, 2, 3 })));
		Assert.assertFalse(grammar.equals(new Grammar(new int[] { 0, 1, 2, 3,
				4, 5 })));
		Assert.assertFalse(grammar.equals(new Grammar(
				new int[] { 0, 2, 1, 3, 4 })));
	}
	
	/**
	 * Tests if the {@link Grammar#hashCode()} method works correctly.
	 */
	@Test
	public void testHashCode() {
		Assert.assertEquals(grammar.hashCode(), grammar.hashCode());
		Assert.assertEquals(grammar.hashCode(), new Grammar(
				new int[] { 0, 1, 2, 3, 4 }).hashCode());
	}

	/**
	 * Tests if the {@link Grammar#get} method returns the correct value.
	 */
	@Test
	public void testGet() {
		for (int i = 0; i < grammar.size(); i++) {
			Assert.assertEquals(i, grammar.get(i));
		}
	}

	/**
	 * Tests if the {@link Grammar#set} method works correctly.
	 */
	@Test
	public void testSet() {
		for (int i = 0; i < grammar.size(); i++) {
			grammar.set(i, 9);
			Assert.assertEquals(9, grammar.get(i));
		}
	}

	/**
	 * Tests if the {@link Grammar#set} method correctly throws an exception if
	 * {@code value >= getMaximumValue()}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidValue1() {
		grammar.set(1, grammar.getMaximumValue());
	}

	/**
	 * Tests if the {@link Grammar#set} method correctly throws an exception if
	 * {@code value < 0}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidValue2() {
		grammar.set(1, -1);
	}

	/**
	 * Tests if the {@link Grammar#copy} method produces a copy equal to but
	 * independent from the original.
	 */
	@Test
	public void testCopy() {
		Grammar copy = grammar.copy();
		Assert.assertTrue(copy.equals(grammar));

		copy.set(1, 0);
		Assert.assertFalse(copy.equals(grammar));
		Assert.assertFalse(grammar.equals(copy));
	}

	/**
	 * Ensures the {@link Grammar#toArray} method returns a correct and and
	 * independent copy of the integer codon representation.
	 */
	@Test
	public void testToArray() {
		int[] array = grammar.toArray();

		Assert.assertEquals(grammar.size(), array.length);
		for (int i = 0; i < grammar.size(); i++) {
			Assert.assertEquals(grammar.get(i), array[i]);
		}

		array[1] = 0;
		Assert.assertEquals(1, grammar.get(1));
	}

	/**
	 * Ensures the {@link Grammar#fromArray} method correctly sets the internal
	 * integer codon representation and the copy is independent from the array.
	 */
	@Test
	public void testFromArray() {
		int[] array = new int[] { 2, 1, 0 };
		grammar.fromArray(array);

		Assert.assertEquals(array.length, grammar.size());
		for (int i = 0; i < array.length; i++) {
			Assert.assertEquals(array[i], grammar.get(i));
		}

		array[1] = 0;
		Assert.assertEquals(1, grammar.get(1));
	}

	/**
	 * Tests if the {@link Grammar#fromArray} method correctly throws an
	 * exception when a value in the array is {@code >= getMaximumValue()}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFromArrayInvalidValue1() {
		grammar.fromArray(new int[] { 0, 1, grammar.getMaximumValue() });
	}

	/**
	 * Tests if the {@link Grammar#fromArray} method correctly throws an
	 * exception when a value in the array is {@code < 0}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFromArrayInvalidValue2() {
		grammar.fromArray(new int[] { 0, 1, -1 });
	}

	/**
	 * Tests the {@link Grammar#cut} method for the case when the entire array
	 * is cut. This should never occur in practice, but helps ensure the code
	 * works as expected.
	 */
	@Test
	public void testCut1() {
		int[] removed = grammar.cut(0, 4);

		Assert.assertArrayEquals(new int[] {}, grammar.toArray());
		Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, removed);
	}

	/**
	 * Tests the {@link Grammar#cut} method for the case when the tail is cut.
	 */
	@Test
	public void testCut2() {
		int[] removed = grammar.cut(2, 4);

		Assert.assertArrayEquals(new int[] { 0, 1 }, grammar.toArray());
		Assert.assertArrayEquals(new int[] { 2, 3, 4 }, removed);
	}

	/**
	 * Tests the {@link Grammar#cut} method for the case when the head is cut.
	 */
	@Test
	public void testCut3() {
		int[] removed = grammar.cut(0, 2);

		Assert.assertArrayEquals(new int[] { 3, 4 }, grammar.toArray());
		Assert.assertArrayEquals(new int[] { 0, 1, 2 }, removed);
	}

	/**
	 * Tests the {@link Grammar#insert} method for the case when the array is
	 * inserted at the head.
	 */
	@Test
	public void testInsert1() {
		grammar.insert(0, new int[] { 5, 6 });

		Assert.assertArrayEquals(new int[] { 5, 6, 0, 1, 2, 3, 4 }, grammar
				.toArray());
	}

	/**
	 * Tests the {@link Grammar#insert} method for the case when the array is
	 * inserted at the tail.
	 */
	@Test
	public void testInsert2() {
		grammar.insert(5, new int[] { 5, 6 });

		Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5, 6 }, grammar
				.toArray());
	}

	/**
	 * Tests the {@link Grammar#insert} method for the case when the array is
	 * inserted in the middle.
	 */
	@Test
	public void testInsert3() {
		grammar.insert(2, new int[] { 5, 6 });

		Assert.assertArrayEquals(new int[] { 0, 1, 5, 6, 2, 3, 4 }, grammar
				.toArray());
	}

	/**
	 * Tests the {@link Grammar#cut} method to ensure an exception is thrown
	 * when the end exceeds the array bounds.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testCutException1() {
		grammar.cut(3, 5);
	}

	/**
	 * Tests the {@link Grammar#cut} method to ensure an exception is thrown
	 * when the start exceeds the array bounds.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testCutException2() {
		grammar.cut(-1, 2);
	}

	/**
	 * Tests the {@link Grammar#cut} method to ensure an exception is thrown
	 * when the cut is invalid.
	 */
	@Test(expected = NegativeArraySizeException.class)
	public void testCutExceptione() {
		grammar.cut(0, 5);
	}

	/**
	 * Tests the {@link Grammar#cut} method to ensure an exception is thrown
	 * when {@code end <= start}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCutException3() {
		grammar.cut(4, 2);
	}

	/**
	 * Tests the {@link Grammar#insert} method to ensure an exception is thrown
	 * when the insert index is {@code > size()}.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInsertException1() {
		grammar.insert(6, new int[] { 0, 1 });
	}

	/**
	 * Tests the {@link Grammar#insert} method to ensure an exception is thrown
	 * when the insert index is {@code < 0}.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInsertException2() {
		grammar.insert(-1, new int[] { 0, 1 });
	}

}
