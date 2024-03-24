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

import org.junit.Assert;
import org.junit.Test;

public class WilcoxonSignedRanksTestTest {

	/**
	 * Test from Sheskin (2004) in Chapter 6.
	 */
	@Test
	public void testExample1() {
		WilcoxonSignedRanksTest test = new WilcoxonSignedRanksTest(5.0);
		test.addAll(new double[] { 9, 10, 8, 4, 8, 3, 0, 10, 15, 9 });
		
		Assert.assertFalse(test.test(0.05));
	}

	/**
	 * Example from <a href="http://en.wikipedia.org/wiki/Wilcoxon_signed-rank_test">Wikipedia</a>.
	 */
	@Test
	public void testExample2() {
		WilcoxonSignedRanksTest test = new WilcoxonSignedRanksTest(0.0);
		test.addAll(new double[] { 15, -7, 5, 20, 0, -9, 17, -12, 5, -10 });

		Assert.assertFalse(test.test(0.05));
		Assert.assertEquals(18, test.lastT, 0.001);
	}

	/**
	 * Example from <a href="http://faculty.vassar.edu/lowry/ch12a.html">http://faculty.vassar.edu/lowry/ch12a.html</a>
	 */
	@Test
	public void testExample3() {
		WilcoxonSignedRanksTest test = new WilcoxonSignedRanksTest(0.0);
		test.addAll(new double[] { 0, 0, 2, -3, -4, -4, 5, 6, 8, 10, 10, -14, 16, 20, 32, 40 });

		Assert.assertTrue(test.test(0.05));
		Assert.assertEquals(19, test.lastT, 0.001);
	}
	
	/**
	 * At least six observations not equal to the median are required.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAllEqual() {
		WilcoxonSignedRanksTest test = new WilcoxonSignedRanksTest(10.0);
		test.addAll(new double[] { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 });

		Assert.assertFalse(test.test(0.05));
	}

}
