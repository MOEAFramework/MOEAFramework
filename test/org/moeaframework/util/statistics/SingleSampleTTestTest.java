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
package org.moeaframework.util.statistics;

import org.apache.commons.math3.stat.inference.TestUtils;
import org.junit.Assert;
import org.junit.Test;

public class SingleSampleTTestTest {

	/**
	 * Test from Sheskin (2004) in Chapter 2.
	 */
	@Test
	public void testExample() {
		SingleSampleTTest test = new SingleSampleTTest(5.0);
		test.addAll(new double[] { 9, 10, 8, 4, 8, 3, 0, 10, 15, 9 });

		Assert.assertEquals(1.94, TestUtils.t(test.getMean(), test.categorize().get(0)), 0.01);
		Assert.assertFalse(test.test(0.05));
	}
	
	@Test
	public void testAllEqual() {
		SingleSampleTTest test = new SingleSampleTTest(10.0);
		test.addAll(new double[] { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 });

		Assert.assertFalse(test.test(0.05));
		Assert.assertFalse(test.test(0.01));
	}

}
