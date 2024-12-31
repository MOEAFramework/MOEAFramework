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
package org.moeaframework.core.variable;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.TestThresholds;

@RunWith(CIRunner.class)
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
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testEmptySize() {
		new Permutation(0);
	}

	@Test
	public void testDefaultValue() {
		Assert.assertTrue(Permutation.isPermutation(permutation.toArray()));
	}
	
	@Test
	public void testName() {
		Assert.assertNull(new Permutation(5).getName());
		Assert.assertEquals("foo", new Permutation("foo", 5).getName());
	}

	@Test
	public void testFromArray() {
		int[] array = new int[] { 3, 1, 2, 0 };
		Permutation permutation = new Permutation(array.length);
		permutation.fromArray(array);

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
	public void testFromArrayEmpty() {
		Permutation permutation = new Permutation(1);
		permutation.fromArray(new int[] { });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFromArrayMissingElement() {
		Permutation permutation = new Permutation(3);
		permutation.fromArray(new int[] { 0, 3, 2 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFromArrayDuplicateElement() {
		Permutation permutation = new Permutation(3);
		permutation.fromArray(new int[] { 0, 1, 2, 1 });
	}

	@Test
	public void testSize() {
		Assert.assertEquals(5, permutation.size());
	}

	@Test
	public void testEquals() {
		Assert.assertFalse(permutation.equals(null));
		Assert.assertTrue(permutation.equals(permutation));
		Assert.assertTrue(permutation.equals(permutation.copy()));
		Assert.assertFalse(permutation.equals(new Permutation(6)));
		Assert.assertFalse(permutation.equals(new Permutation(4)));
		
		Permutation other = new Permutation(5);
		other.fromArray(new int[] { 0, 2, 1, 3, 4 });
		Assert.assertFalse(permutation.equals(other));
	}
	
	@Test
	public void testHashCode() {
		Assert.assertEquals(permutation.hashCode(), permutation.hashCode());
		Assert.assertEquals(permutation.hashCode(), permutation.copy().hashCode());
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
		Assert.assertNotSame(permutation, copy);
		Assert.assertEquals(permutation, copy);

		copy.swap(2, 3);
		Assert.assertNotEquals(permutation, copy);
	}

	@Test
	public void testSwap() {
		permutation.swap(2, 3);
		Assert.assertArrayEquals(new int[] { 0, 1, 3, 2, 4 }, permutation.toArray());

		permutation.swap(4, 4);
		Assert.assertArrayEquals(new int[] { 0, 1, 3, 2, 4 }, permutation.toArray());
	}

	@Test
	public void testInsert() {
		permutation.insert(4, 0);
		Assert.assertArrayEquals(new int[] { 4, 0, 1, 2, 3 }, permutation.toArray());

		permutation.insert(0, 4);
		Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, permutation.toArray());

		permutation.insert(2, 2);
		Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, permutation.toArray());

		permutation.insert(2, 3);
		Assert.assertArrayEquals(new int[] { 0, 1, 3, 2, 4 }, permutation.toArray());
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
	public void testToString() {
		permutation.fromArray(new int[] { 2, 0, 1, 3, 4 });
		Assert.assertEquals("[2,0,1,3,4]", permutation.toString());
	}

	@Test
	public void testEncodeDecode() {
		Permutation newVariable = new Permutation(5);
		newVariable.decode(permutation.encode());
		Assert.assertArrayEquals(permutation.toArray(), newVariable.toArray());
	}
	
	@Test
	public void testDecodeWithoutBrackets() {
		Permutation p = new Permutation(3);
		p.decode("2,0,1");
		Assert.assertArrayEquals(new int[] { 2, 0, 1 }, p.toArray());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDecodeInvalidLength() {
		Permutation p = new Permutation(5);
		p.decode("[2,0,1]");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDecodeInvalidValue() {
		Permutation p = new Permutation(5);
		p.decode("[2,0,1,5,3]");
	}
	
	@Test
	@Retryable
	public void testRandomize() {
		DescriptiveStatistics[] positionStats = new DescriptiveStatistics[permutation.size()];
		
		for (int j = 0; j < permutation.size(); j++) {
			positionStats[j] = new DescriptiveStatistics();
		}
		
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			permutation.randomize();
			
			Assert.assertTrue(Permutation.isPermutation(permutation.toArray()));
			
			for (int j = 0; j < permutation.size(); j++) {
				positionStats[j].addValue(permutation.get(j));
			}
		}
		
		for (int j = 0; j < permutation.size(); j++) {
			Assert.assertUniformDistribution(0, permutation.size()-1, positionStats[j]);
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidType() {
		Permutation.setPermutation(new BinaryVariable(3), new int[] { 0, 2, 1 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetInvalidType() {
		Permutation.getPermutation(new BinaryVariable(3));
	}
	
	@Test
	public void testDefinition() {
		Assert.assertEquals("Permutation(5)", new Permutation(5).getDefinition());
		Assert.assertEquals("Permutation(\"foo\",5)", new Permutation("foo", 5).getDefinition());
	}

}
