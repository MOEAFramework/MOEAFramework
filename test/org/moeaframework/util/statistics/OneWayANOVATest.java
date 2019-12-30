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
package org.moeaframework.util.statistics;

import org.junit.Assert;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.junit.Test;

/**
 * Tests the {@link OneWayANOVA} class.
 */
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
		test.add(8, 0);
		test.add(10, 0);
		test.add(9, 0);
		test.add(10, 0);
		test.add(9, 0);
		test.add(7, 1);
		test.add(8, 1);
		test.add(5, 1);
		test.add(8, 1);
		test.add(5, 1);
		test.add(4, 2);
		test.add(8, 2);
		test.add(7, 2);
		test.add(5, 2);
		test.add(7, 2);

		Assert.assertEquals(6.98, TestUtils
				.oneWayAnovaFValue(test.categorize()), 0.01);
		Assert.assertTrue(test.test(0.05));
		Assert.assertTrue(test.test(0.01));
	}
	
	@Test
	public void testAllEquals() {
		OneWayANOVA test = new OneWayANOVA(3);
		test.add(10, 0);
		test.add(10, 0);
		test.add(10, 0);
		test.add(10, 0);
		test.add(10, 0);
		test.add(10, 1);
		test.add(10, 1);
		test.add(10, 1);
		test.add(10, 1);
		test.add(10, 1);
		test.add(10, 2);
		test.add(10, 2);
		test.add(10, 2);
		test.add(10, 2);
		test.add(10, 2);
		
		Assert.assertFalse(test.test(0.05));
		Assert.assertFalse(test.test(0.01));
	}

}
