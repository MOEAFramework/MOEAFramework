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
import org.moeaframework.TestThresholds;
import org.moeaframework.core.FrameworkException;

/**
 * Tests the {@link Subset} class.
 */
public class SubsetTest {
	
	private Subset subset;

	@Before
	public void setUp() {
		subset = new Subset(5, 10);
	}

	@After
	public void tearDown() {
		subset = null;
	}
	
	@Test
	public void testConstructor1() {
		subset.validate();
	}
	
	@Test
	public void testConstructor2() {
		Subset subset = new Subset(10, 10);
		subset.validate();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidConstructor() {
		new Subset(11, 10);
	}
	
	@Test
	public void testGetK() {
		Assert.assertEquals(5, subset.getK());
	}
	
	@Test
	public void testGetN() {
		Assert.assertEquals(10, subset.getN());
	}

	@Test
	public void testEquals() {
		Assert.assertFalse(subset.equals(null));
		Assert.assertTrue(subset.equals(subset));
		Assert.assertTrue(subset.equals(new Subset(5, 10)));
		Assert.assertFalse(subset.equals(new Subset(5, 15)));
		Assert.assertFalse(subset.equals(new Subset(7, 10)));
		
		Subset randomSet = new Subset(5, 10);
		randomSet.randomize();
		Assert.assertFalse(subset.equals(randomSet));
	}
	
	@Test
	public void testHashCode() {
		Assert.assertEquals(subset.hashCode(), subset.hashCode());
		Assert.assertEquals(subset.hashCode(), new Subset(5, 10).hashCode());
	}
	
	@Test
	public void testGet() {
		for (int i = 0; i < subset.getK(); i++) {
			Assert.assertEquals(i, subset.get(i));
		}
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetException() {
		subset.get(5);
	}
	
	@Test
	public void testCopy() {
		Subset copy = subset.copy();
		Assert.assertTrue(copy.equals(subset));

		copy.set(2, copy.randomNonmember());
		Assert.assertFalse(copy.equals(subset));
		Assert.assertFalse(subset.equals(copy));
	}
	
	@Test
	public void testRandomize() {
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			subset.randomize();
			subset.validate();
		}
	}
	
	@Test
	public void testRandomNonmember() {
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
	
}
