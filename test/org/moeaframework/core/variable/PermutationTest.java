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
package org.moeaframework.core.variable;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

	@Test
	public void testConstructor() {
		Assert.assertTrue(Permutation.isPermutation(permutation.toArray()));
	}

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
	
	@Test(expected = IllegalArgumentException.class)
	public void testArrayConstructorMissingElement() {
		new Permutation(new int[] { 0, 3, 2 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testArrayConstructorDuplicateElement() {
		new Permutation(new int[] { 0, 1, 2, 1 });
	}

	@Test
	public void testSize() {
		Assert.assertEquals(5, permutation.size());
	}

	@Test
	public void testEquals() {
		Assert.assertFalse(permutation.equals(null));
		Assert.assertTrue(permutation.equals(permutation));
		Assert.assertTrue(permutation.equals(new Permutation(5)));
		Assert.assertFalse(permutation.equals(new Permutation(6)));
		Assert.assertFalse(permutation.equals(new Permutation(4)));
		Assert.assertFalse(permutation.equals(new Permutation(new int[] { 0, 2, 1, 3, 4 })));
	}
	
	@Test
	public void testHashCode() {
		Assert.assertEquals(permutation.hashCode(), permutation.hashCode());
		Assert.assertEquals(permutation.hashCode(), new Permutation(5).hashCode());
	}

	@Test
	public void testGet() {
		for (int i = 0; i < permutation.size(); i++) {
			Assert.assertEquals(i, permutation.get(i));
		}
	}

	@Test
	public void testCopy() {
		Permutation copy = permutation.copy();
		Assert.assertTrue(copy.equals(permutation));

		copy.swap(2, 3);
		Assert.assertFalse(copy.equals(permutation));
		Assert.assertFalse(permutation.equals(copy));
	}

	@Test
	public void testSwap() {
		permutation.swap(2, 3);
		Assert.assertTrue(Permutation.isPermutation(permutation.toArray()));
		Assert.assertEquals(2, permutation.get(3));
		Assert.assertEquals(3, permutation.get(2));

		permutation.swap(4, 4);
		Assert.assertTrue(Permutation.isPermutation(permutation.toArray()));
	}

	@Test
	public void testInsert() {
		permutation.insert(4, 0);
		Assert.assertTrue(permutation.equals(new Permutation(new int[] { 4, 0, 1, 2, 3 })));

		permutation.insert(0, 4);
		Assert.assertTrue(permutation.equals(new Permutation(new int[] { 0, 1, 2, 3, 4 })));

		permutation.insert(2, 2);
		Assert.assertTrue(permutation.equals(new Permutation(new int[] { 0, 1, 2, 3, 4 })));

		permutation.insert(2, 3);
		Assert.assertTrue(permutation.equals(new Permutation(new int[] { 0, 1, 3, 2, 4 })));
	}

	@Test
	public void testIsPermutation() {
		Assert.assertTrue(Permutation.isPermutation(new int[0]));
		Assert.assertTrue(Permutation.isPermutation(new int[] { 0 }));
		Assert.assertFalse(Permutation.isPermutation(new int[] { 1 }));
		Assert.assertFalse(Permutation.isPermutation(new int[] { 0, 0 }));
		Assert.assertTrue(Permutation.isPermutation(new int[] { 2, 1, 0 }));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSwapOutOfBounds() {
		permutation.swap(7, 2);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testInsertOutOfBounds() {
		permutation.insert(2, 7);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetOutOfBounds() {
		permutation.get(7);
	}

	@Test
	public void testEncodeDecode() {
		Permutation newVariable = new Permutation(5);
		newVariable.decode(permutation.encode());
		Assert.assertArrayEquals(permutation.toArray(), newVariable.toArray());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDecodeInvalidPermutation1() throws IOException {
		Permutation p = new Permutation(5);
		p.decode("2,0,1");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDecodeInvalidPermutation2() throws IOException {
		Permutation p = new Permutation(5);
		p.decode("2,0,1,5,3");
	}

}
