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
import org.junit.Test;

/**
 * Tests the {@link WilcoxonSignedRanksTest} class.
 */
public class WilcoxonSignedRanksTestTest {

	/**
	 * Test from Sheskin (2004) in Chapter 6.
	 */
	@Test
	public void testExample1() {
		WilcoxonSignedRanksTest test = new WilcoxonSignedRanksTest(5.0);
		test.add(9);
		test.add(10);
		test.add(8);
		test.add(4);
		test.add(8);
		test.add(3);
		test.add(0);
		test.add(10);
		test.add(15);
		test.add(9);

		Assert.assertFalse(test.test(0.05));
	}

	/**
	 * Example from
	 * <a href="http://en.wikipedia.org/wiki/Wilcoxon_signed-rank_test">
	 * Wikipedia</a>.
	 */
	@Test
	public void testExample2() {
		WilcoxonSignedRanksTest test = new WilcoxonSignedRanksTest(0.0);
		test.add(15);
		test.add(-7);
		test.add(5);
		test.add(20);
		test.add(0);
		test.add(-9);
		test.add(17);
		test.add(-12);
		test.add(5);
		test.add(-10);

		Assert.assertFalse(test.test(0.05));
		Assert.assertEquals(18, test.lastT, 0.001);
	}

	/**
	 * Example from <a href="http://faculty.vassar.edu/lowry/ch12a.html">
	 * http://faculty.vassar.edu/lowry/ch12a.html</a>
	 */
	@Test
	public void testExample3() {
		WilcoxonSignedRanksTest test = new WilcoxonSignedRanksTest(0.0);
		test.add(0);
		test.add(0);
		test.add(2);
		test.add(-3);
		test.add(-4);
		test.add(-4);
		test.add(5);
		test.add(6);
		test.add(8);
		test.add(10);
		test.add(10);
		test.add(-14);
		test.add(16);
		test.add(20);
		test.add(32);
		test.add(40);

		Assert.assertTrue(test.test(0.05));
		Assert.assertEquals(19, test.lastT, 0.001);
	}
	
	/**
	 * At least six observations not equal to the median are required.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAllEqual() {
		WilcoxonSignedRanksTest test = new WilcoxonSignedRanksTest(10.0);
		test.add(10);
		test.add(10);
		test.add(10);
		test.add(10);
		test.add(10);
		test.add(10);
		test.add(10);
		test.add(10);
		test.add(10);
		test.add(10);

		Assert.assertFalse(test.test(0.05));
	}

}
