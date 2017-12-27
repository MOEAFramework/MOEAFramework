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

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.PRNG;

/**
 * Tests the {@link Subset} class.
 */
public class SubsetTest {
	
	@Test
	public void testFixedSizeSubsetConstructor() {
		// Test the constructor
		Subset subset = new Subset(5, 10);
		subset.validate();
		
		Assert.assertEquals(5, subset.getL());
		Assert.assertEquals(5, subset.getU());
		Assert.assertEquals(10, subset.getN());
		Assert.assertEquals(5, subset.size());
	}
	
	@Test
	public void testVariableSizeSubsetConstructor() {
		Subset subset = new Subset(0, 10, 10);
		subset.validate();
		
		Assert.assertEquals(0, subset.getL());
		Assert.assertEquals(10, subset.getU());
		Assert.assertEquals(10, subset.getN());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidConstructor1() {
		new Subset(11, 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidConstructor2() {
		new Subset(-1, 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidConstructor3() {
		new Subset(0, -1);
	}
	
	@Test
	public void testContains() {
		int[] content = new int[] {3, 5, 9};
		
		Subset subset = new Subset(0, 10, 10);
		subset.fromArray(content);
		
		for (int i = 0; i < subset.getN(); i++) {
			Assert.assertEquals(Arrays.binarySearch(content, i) >= 0, subset.contains(i));
		}
	}
	
	@Test
	public void testAdd() {
		Subset subset = new Subset(4, 6, 10);
		subset.fromArray(new int[] { 0, 1, 2, 3 });
		
		Subset subset2 = subset.copy();
		subset2.add(subset.randomNonmember());
		
		Assert.assertEquals(subset.size()+1, subset2.size());
		Assert.assertFalse(subset.equals(subset2));
		Assert.assertNotEquals(subset.hashCode(), subset2.hashCode());
	}
	
	@Test(expected = FrameworkException.class)
	public void testAddException() {
		Subset subset = new Subset(5, 10);
		subset.add(subset.randomNonmember());
		subset.validate();
	}
	
	@Test
	public void testReplace() {
		Subset subset = new Subset(5, 10);
		
		Subset subset2 = subset.copy();
		subset2.replace(subset.randomMember(), subset.randomNonmember());
		subset2.validate();
		
		Assert.assertEquals(subset.size(), subset2.size());
		Assert.assertFalse(subset.equals(subset2));
		Assert.assertNotEquals(subset.hashCode(), subset2.hashCode());
	}
	
	@Test
	public void testRemove() {
		Subset subset = new Subset(4, 6, 10);
		subset.fromArray(new int[] { 0, 1, 2, 3, 4 });
		
		Subset subset2 = subset.copy();
		
		for (int i = 1; i <= subset2.size(); i++) {
			subset2.remove(subset2.randomMember());
			subset2.remove(subset2.randomNonmember()); // Removing a non-member is a no-op
			
			Assert.assertEquals(subset.size()-i, subset2.size());
			Assert.assertFalse(subset.equals(subset2));
		}
	}
	
	@Test(expected = FrameworkException.class)
	public void testRemoveException() {
		Subset subset = new Subset(5, 10);
		subset.remove(subset.randomMember());
		subset.validate();
	}

	@Test
	public void testEquals() {
		Subset subset = new Subset(5, 10);
		Assert.assertFalse(subset.equals(null));
		Assert.assertTrue(subset.equals(subset));
		Assert.assertTrue(subset.equals(new Subset(5, 10)));
		Assert.assertFalse(subset.equals(new Subset(5, 15)));
		Assert.assertFalse(subset.equals(new Subset(7, 10)));
		
		Subset randomSet = subset.copy();
		randomSet.randomize();
		Assert.assertFalse(subset.equals(randomSet));
		
		Subset reorderedSet = subset.copy();
		int[] values = reorderedSet.toArray();
		PRNG.shuffle(values);
		reorderedSet.fromArray(values);
		Assert.assertTrue(subset.equals(reorderedSet));
	}
	
	@Test
	public void testHashCode() {
		Subset subset = new Subset(5, 10);
		Assert.assertEquals(subset.hashCode(), subset.hashCode());
		Assert.assertEquals(subset.hashCode(), new Subset(5, 10).hashCode());
		
		Subset reorderedSet = subset.copy();
		int[] values = reorderedSet.toArray();
		PRNG.shuffle(values);
		reorderedSet.fromArray(values);
		Assert.assertEquals(subset.hashCode(), reorderedSet.hashCode());
	}
	
	@Test
	public void testCopy() {
		Subset subset = new Subset(5, 10);
		Subset copy = subset.copy();
		Assert.assertTrue(copy.equals(subset));

		// Modify the copy and make sure the original is unchanged
		copy.replace(copy.randomMember(), copy.randomNonmember());
		Assert.assertFalse(copy.equals(subset));
		Assert.assertFalse(subset.equals(copy));
	}
	
	@Test
	public void testRandomize() {
		Subset subset = new Subset(5, 10);
		
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			subset.randomize();
			subset.validate();
		}
	}
	
	@Test
	public void testRandomNonmember() {
		Subset subset = new Subset(5, 10);
		
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			int nonmember = subset.randomNonmember();
			Assert.assertFalse(subset.getSet().contains(nonmember));
		}
	}
	
	@Test(expected = FrameworkException.class)
	public void testRandomNonmemberException() {
		Subset subset = new Subset(10, 10);
		subset.randomNonmember();
	}
	
	@Test
	public void testRandomMember() {
		Subset subset = new Subset(5, 10);
		
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			int member = subset.randomMember();
			Assert.assertTrue(subset.getSet().contains(member));
		}
	}
	
	@Test(expected = FrameworkException.class)
	public void testRandomMemberException() {
		Subset subset = new Subset(0, 10, 10);
		subset.fromArray(new int[0]);
		subset.randomMember();
	}
	
}
