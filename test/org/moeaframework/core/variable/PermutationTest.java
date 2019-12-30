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
 * Tests the {@link Permutation} class.
 */
public class PermutationTest {

	private Permutation permutation;

	@Before
	public void setUp() {
		permutation = new Permutation(5);
	}

	@After
	public void tearDown() {
		permutation = null;
	}

	/**
	 * Tests the constructor to ensure a valid permutation is created.
	 */
	@Test
	public void testConstructor() {
		Assert.assertTrue(Permutation.isPermutation(permutation.toArray()));
	}

	/**
	 * Tests the array constructor to ensure the array forms a valid permutation
	 * and is an independent copy of the array.
	 */
	@Test
	public void testArrayConstructor() {
		int[] array = new int[] { 3, 1, 2, 0 };
		Permutation permutation = new Permutation(array);

		Assert.assertTrue(Permutation.isPermutation(permutation.toArray()));
		Assert.assertEquals(4, permutation.size());

		for (int i = 0; i < permutation.size(); i++) {
			Assert.assertEquals(array[i], permutation.get(i));
		}

		// ensure stored array is independent from argument
		array[0] = 1;
		Assert.assertEquals(3, permutation.get(0));
	}
	
	/**
	 * Tests the array constructor, and consequently the {@code fromArray}
	 * method, for detecting when the permutation is missing an element.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testArrayConstructorInvalidPermutation1() {
		new Permutation(new int[] { 0, 3, 2 });
	}
	
	/**
	 * Tests if the array constructor, and consequently the {@code fromArray}
	 * method, for detecting when the permutation contains duplicate elements.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testArrayConstructorInvalidPermutation2() {
		new Permutation(new int[] { 0, 1, 2, 1 });
	}

	/**
	 * Tests if the correct size is returned.
	 */
	@Test
	public void testSize() {
		Assert.assertEquals(5, permutation.size());
	}

	/**
	 * Tests if the {@link Permutation#equals} method works correctly.
	 */
	@Test
	public void testEquals() {
		Assert.assertFalse(permutation.equals(null));
		Assert.assertTrue(permutation.equals(permutation));
		Assert.assertTrue(permutation.equals(new Permutation(5)));
		Assert.assertFalse(permutation.equals(new Permutation(6)));
		Assert.assertFalse(permutation.equals(new Permutation(4)));
		Assert.assertFalse(permutation.equals(new Permutation(new int[] { 0, 2,
				1, 3, 4 })));
	}
	
	/**
	 * Tests to ensure the {@link Permutation#hashCode()} general contract is
	 * upheld.
	 */
	@Test
	public void testHashCode() {
		Assert.assertEquals(permutation.hashCode(), permutation.hashCode());
		Assert.assertEquals(permutation.hashCode(),
				new Permutation(5).hashCode());
	}

	/**
	 * Tests if the {@link Permutation#get} method returns the correct value.
	 */
	@Test
	public void testGet() {
		for (int i = 0; i < permutation.size(); i++) {
			Assert.assertEquals(i, permutation.get(i));
		}
	}

	/**
	 * Tests if the {@link Permutation#copy} method produces a copy equal to but
	 * independent from the original.
	 */
	@Test
	public void testCopy() {
		Permutation copy = permutation.copy();
		Assert.assertTrue(copy.equals(permutation));

		copy.swap(2, 3);
		Assert.assertFalse(copy.equals(permutation));
		Assert.assertFalse(permutation.equals(copy));
	}

	/**
	 * Tests if the {@link Permutation#swap} method correctly swaps the two
	 * indices.
	 */
	@Test
	public void testSwap() {
		permutation.swap(2, 3);
		Assert.assertTrue(Permutation.isPermutation(permutation.toArray()));
		Assert.assertEquals(2, permutation.get(3));
		Assert.assertEquals(3, permutation.get(2));

		permutation.swap(4, 4);
		Assert.assertTrue(Permutation.isPermutation(permutation.toArray()));
	}

	/**
	 * Tests if the {@link Permutation#insert} method correctly inserts the
	 * value into the permutation.
	 */
	@Test
	public void testInsert() {
		permutation.insert(4, 0);
		Assert.assertTrue(permutation.equals(new Permutation(new int[] { 4, 0,
				1, 2, 3 })));

		permutation.insert(0, 4);
		Assert.assertTrue(permutation.equals(new Permutation(new int[] { 0, 1,
				2, 3, 4 })));

		permutation.insert(2, 2);
		Assert.assertTrue(permutation.equals(new Permutation(new int[] { 0, 1,
				2, 3, 4 })));

		permutation.insert(2, 3);
		Assert.assertTrue(permutation.equals(new Permutation(new int[] { 0, 1,
				3, 2, 4 })));
	}

	/**
	 * Tests if the {@link Permutation#isPermutation} method correctly detects
	 * valid and invalid permutations.
	 */
	@Test
	public void testIsPermutation() {
		Assert.assertTrue(Permutation.isPermutation(new int[0]));
		Assert.assertTrue(Permutation.isPermutation(new int[] { 0 }));
		Assert.assertFalse(Permutation.isPermutation(new int[] { 1 }));
		Assert.assertFalse(Permutation.isPermutation(new int[] { 0, 0 }));
		Assert.assertTrue(Permutation.isPermutation(new int[] { 2, 1, 0 }));
	}

	/**
	 * Tests if the {@link Permutation#swap(int, int)} method correctly throws
	 * an {@code IndexOutOfBoundsException}.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSwapException() {
		permutation.swap(7, 2);
	}

	/**
	 * Tests if the {@link Permutation#insert(int, int)} method correctly throws
	 * an {@code IndexOutOfBoundsException}.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInsertException() {
		permutation.insert(2, 7);
	}

	/**
	 * Tests if the {@link Permutation#get(int)} method correctly throws an
	 * {@code IndexOutOfBoundsException}.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetException() {
		permutation.get(7);
	}


}
