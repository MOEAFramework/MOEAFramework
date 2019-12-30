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

import org.apache.commons.math3.stat.inference.TestUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link TwoSampleTTest} class.
 */
public class TwoSampleTTestTest {

	/**
	 * Test from Sheskin (2004) in Chapter 11.
	 */
	@Test
	public void testIndependentExample() {
		TwoSampleTTest test = new TwoSampleTTest(true);
		test.add(11, 0);
		test.add(1, 0);
		test.add(0, 0);
		test.add(2, 0);
		test.add(0, 0);
		test.add(11, 1);
		test.add(11, 1);
		test.add(5, 1);
		test.add(8, 1);
		test.add(4, 1);

		Assert.assertEquals(-1.96, TestUtils.t(test.categorize().get(0), test
				.categorize().get(1)), 0.01);
		Assert.assertFalse(test.test(0.05));
	}

	/**
	 * Test from Sheskin (2004) in Chapter 17.
	 */
	@Test
	public void testDependentExample() {
		TwoSampleTTest test = new TwoSampleTTest(false);
		test.add(9, 0);
		test.add(2, 0);
		test.add(1, 0);
		test.add(4, 0);
		test.add(6, 0);
		test.add(4, 0);
		test.add(7, 0);
		test.add(8, 0);
		test.add(5, 0);
		test.add(1, 0);
		test.add(8, 1);
		test.add(2, 1);
		test.add(3, 1);
		test.add(2, 1);
		test.add(3, 1);
		test.add(0, 1);
		test.add(4, 1);
		test.add(5, 1);
		test.add(4, 1);
		test.add(0, 1);

		Assert.assertEquals(2.86, TestUtils.pairedT(test.categorize().get(0),
				test.categorize().get(1)), 0.02);
		Assert.assertTrue(test.test(0.05));
		Assert.assertFalse(test.test(0.01));
	}
	
	@Test
	public void testAllEqual() {
		TwoSampleTTest test = new TwoSampleTTest(true);
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

		Assert.assertFalse(test.test(0.05));
		Assert.assertFalse(test.test(0.01));
	}

}
