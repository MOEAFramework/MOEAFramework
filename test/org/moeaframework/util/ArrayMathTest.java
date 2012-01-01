/* Copyright 2009-2012 David Hadka
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
package org.moeaframework.util;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Settings;

/**
 * Tests the {@link ArrayMath} class.
 */
public class ArrayMathTest {

	/**
	 * Tests if the {@code sum} method correctly computes the sum of arrays.
	 */
	@Test
	public void testIntSum() {
		Assert.assertEquals(0, ArrayMath.sum(new int[] {}));
		Assert.assertEquals(1, ArrayMath.sum(new int[] { 1 }));
		Assert.assertEquals(5, ArrayMath.sum(new int[] { 2, 0, 3, 2, 0, -2 }));
	}

	/**
	 * Tests if the {@code sum} method throws an exception if {@code null} is
	 * passed as the argument.
	 */
	@Test(expected = NullPointerException.class)
	public void testIntSumNullArray() {
		ArrayMath.sum((int[])null);
	}

	/**
	 * Tests if the {@code sum} method correctly detects integer overflow.
	 */
	@Test(expected = ArithmeticException.class)
	public void testIntSumOverflow() {
		ArrayMath.sum(new int[] { Integer.MAX_VALUE, 1 });
	}

	/**
	 * Tests if the {@code sum} method correctly computes the sum of arrays.
	 */
	@Test
	public void testDoubleSum() {
		Assert.assertEquals(0.0, ArrayMath.sum(new double[] {}), Settings.EPS);
		Assert.assertEquals(1.0, ArrayMath.sum(new double[] { 1.0 }),
				Settings.EPS);
		Assert.assertEquals(5.0, ArrayMath.sum(new double[] { 2.0, 0, 3.0, 2.0,
				0, -2.0 }), Settings.EPS);
	}

	/**
	 * Tests if the {@code sum} method throws an exception if {@code null} is
	 * passed as the argument.
	 */
	@Test(expected = NullPointerException.class)
	public void testDoubleSumNullArray() {
		ArrayMath.sum((double[])null);
	}

	/**
	 * Tests if the {@code min} method correctly returns the minimum element in
	 * an array.
	 */
	@Test
	public void testDoubleMin() {
		Assert.assertEquals(-5.0, ArrayMath.min(new double[] { -5.0 }),
				Settings.EPS);
		Assert.assertEquals(-5.0, ArrayMath.min(new double[] { -2.0, 0.0, 5.0,
				-5.0, 3.0 }), Settings.EPS);
	}

	/**
	 * Tests if the {@code min} method throws an exception if an empty array is
	 * passed as the argument.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDoubleMinEmpty() {
		ArrayMath.min(new double[] {});
	}

	/**
	 * Tests if the {@code min} method throws an exception if {@code null} is
	 * passed as the argument.
	 */
	@Test(expected = NullPointerException.class)
	public void testDoubleMinNull() {
		ArrayMath.min(null);
	}

	/**
	 * Tests if the {@code max} method correctly returns the maximum element in
	 * an array.
	 */
	@Test
	public void testDoubleMax() {
		Assert.assertEquals(-5.0, ArrayMath.max(new double[] { -5.0 }),
				Settings.EPS);
		Assert.assertEquals(5.0, ArrayMath.max(new double[] { -2.0, 0.0, 5.0,
				-5.0, 3.0 }), Settings.EPS);
	}

	/**
	 * Tests if the {@code mixn} method throws an exception if an empty array is
	 * passed as the argument.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDoubleMaxEmpty() {
		ArrayMath.max(new double[] {});
	}

	/**
	 * Tests if the {@code max} method throws an exception if {@code null} is
	 * passed as the argument.
	 */
	@Test(expected = NullPointerException.class)
	public void testDoubleMaxNull() {
		ArrayMath.max(null);
	}

}
