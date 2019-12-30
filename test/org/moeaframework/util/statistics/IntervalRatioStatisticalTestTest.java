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

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link IntervalRatioStatisticalTest} class.
 */
public class IntervalRatioStatisticalTestTest {

	/**
	 * The shared interval ratio statistical test.
	 */
	private IntervalRatioStatisticalTest test = null;

	/**
	 * Constructs the shared interval ratio statistical test.
	 */
	@Before
	public void setUp() {
		test = new IntervalRatioStatisticalTest(2) {

			public boolean test(double alpha) {
				throw new UnsupportedOperationException();
			}

		};
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		test = null;
	}

	/**
	 * Tests if the {@link IntervalRatioStatisticalTest#categorize} procedure
	 * correctly organized categories/groups.
	 */
	@Test
	public void testCategorize() {
		test.add(2.0, 0);
		test.add(-1.0, 1);
		test.add(-2.0, 1);
		test.add(3.0, 0);
		test.add(5.0, 1);
		test.add(-1.0, 0);
		test.add(-3.0, 0);

		List<double[]> result = test.categorize();

		Assert.assertEquals(2, result.size());
		testArraysMatch(new double[] { 2.0, 3.0, -1.0, -3.0 }, result.get(0));
		testArraysMatch(new double[] { -1.0, -2.0, 5.0 }, result.get(1));
	}

	/**
	 * Asserts that the two arrays contain the same elements, but not
	 * necessarily in the same order.
	 * 
	 * @param a1 the first array
	 * @param a2 the second array
	 */
	public void testArraysMatch(double[] a1, double[] a2) {
		for (int i = 0; i < a1.length; i++) {
			boolean found = false;

			for (int j = 0; j < a2.length; j++) {
				if (a1[i] == a2[j]) {
					found = true;
					a2[j] = Double.NaN;
				}
			}

			Assert.assertTrue(found);
		}
	}

	/**
	 * Tests if the {@link IntervalRatioStatisticalTest#add} method correctly
	 * throws an exception for invalid groups.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGroupException1() {
		test.add(0.0, -1);
	}

	/**
	 * Tests if the {@link IntervalRatioStatisticalTest#add} method correctly
	 * throws an exception for invalid groups.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGroupException2() {
		test.add(0.0, 2);
	}

}
