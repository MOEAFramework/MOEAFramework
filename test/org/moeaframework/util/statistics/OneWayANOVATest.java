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
package org.moeaframework.util.statistics;

import org.apache.commons.math3.stat.inference.TestUtils;
import org.junit.Test;
import org.moeaframework.Assert;

public class OneWayANOVATest {
	
	@Test(expected = IllegalArgumentException.class)
	public void testOneGroup() {
		new OneWayANOVA(1);
	}

	/**
	 * Test from Sheskin (2004) in Chapter 21.
	 */
	@Test
	public void testExample() {
		OneWayANOVA test = new OneWayANOVA(3);
		test.addAll(new double[] { 8, 10, 9, 10, 9 }, 0);
		test.addAll(new double[] { 7, 8, 5, 8, 5 }, 1);
		test.addAll(new double[] { 4, 8, 7, 5, 7 }, 2);

		Assert.assertEquals(6.98, TestUtils.oneWayAnovaFValue(test.categorize()), 0.01);
		Assert.assertTrue(test.test(0.05));
		Assert.assertTrue(test.test(0.01));
	}
	
	@Test
	public void testAllEquals() {
		OneWayANOVA test = new OneWayANOVA(3);
		test.addAll(new double[] { 10, 10, 10, 10, 10 }, 0);
		test.addAll(new double[] { 10, 10, 10, 10, 10 }, 1);
		test.addAll(new double[] { 10, 10, 10, 10, 10 }, 2);
		
		Assert.assertFalse(test.test(0.05));
		Assert.assertFalse(test.test(0.01));
	}

}
