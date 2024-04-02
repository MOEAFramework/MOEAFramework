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

import java.util.Arrays;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.FrameworkException;

@RunWith(CIRunner.class)
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
		
		Subset otherSubset = subset.copy();
		subset.fromArray(new int[] { 0, 1, 2, 3, 4 });
		otherSubset.fromArray(new int[] { 0, 1, 2, 3, 4 });
		Assert.assertTrue(subset.equals(otherSubset));
		
		otherSubset.fromArray(new int[] { 4, 0, 1, 2, 3 });
		Assert.assertTrue(subset.equals(otherSubset));
		
		otherSubset.fromArray(new int[] { 0, 1, 2, 3, 5 });
		Assert.assertFalse(subset.equals(otherSubset));
	}
	
	@Test
	public void testHashCode() {
		Subset subset = new Subset(5, 10);
		Assert.assertEquals(subset.hashCode(), subset.hashCode());
		Assert.assertEquals(subset.hashCode(), new Subset(5, 10).hashCode());
		
		Subset otherSubset = subset.copy();
		subset.fromArray(new int[] { 0, 1, 2, 3, 4 });
		otherSubset.fromArray(new int[] { 0, 1, 2, 3, 4 });
		Assert.assertEquals(subset.hashCode(), otherSubset.hashCode());
		
		otherSubset.fromArray(new int[] { 4, 0, 1, 2, 3 });
		Assert.assertEquals(subset.hashCode(), otherSubset.hashCode());
		
		otherSubset.fromArray(new int[] { 0, 1, 2, 3, 5 });
		Assert.assertNotEquals(subset.hashCode(), otherSubset.hashCode());
	}
	
	@Test
	public void testCopy() {
		Subset subset = new Subset(5, 10);
		Subset copy = subset.copy();
		Assert.assertNotSame(subset, copy);
		Assert.assertEquals(subset, copy);

		// Modify the copy and make sure the original is unchanged
		copy.replace(copy.randomMember(), copy.randomNonmember());
		Assert.assertNotEquals(subset, copy);
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
			Assert.assertContains(subset.getSet(), member);
		}
	}
	
	@Test(expected = FrameworkException.class)
	public void testRandomMemberException() {
		Subset subset = new Subset(0, 10, 10);
		subset.fromArray(new int[0]);
		subset.randomMember();
	}
	
	@Test
	public void testToString() {
		Subset subset = new Subset(5, 10);
		subset.fromArray(new int[] { 9, 3, 5, 7, 1 });
		Assert.assertEquals("1,3,5,7,9", subset.toString());
	}
	
	@Test
	public void testEncodeDecode() {
		int[] content = new int[] {3, 5, 9};
		
		Subset subset = new Subset(0, 10, 10);
		subset.fromArray(content);
		
		Subset newSubset = new Subset(0, 10, 10);
		newSubset.decode(subset.encode());
		Assert.assertArrayEquals(subset.toArray(), newSubset.toArray());
	}
	
	@Test
	@Retryable
	public void testRandomize() {
		Subset subset = new Subset(2, 10);
		DescriptiveStatistics[] valueStats = new DescriptiveStatistics[subset.getN()];
		
		for (int j = 0; j < subset.getN(); j++) {
			valueStats[j] = new DescriptiveStatistics();
		}
		
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			subset.randomize();
			subset.validate();
						
			for (int j = 0; j < subset.getN(); j++) {
				valueStats[j].addValue(subset.contains(j) ? 1 : 0);
			}
		}
		
		for (int j = 0; j < subset.getL(); j++) {
			Assert.assertEquals(0.2, valueStats[j].getMean(), TestThresholds.STATISTICS_EPS);
		}
	}
	
}
