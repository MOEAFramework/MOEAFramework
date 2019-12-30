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
package org.moeaframework.core.comparator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;

/**
 * Tests the {@link EpsilonBoxObjectiveComparator} class.
 */
public class EpsilonBoxObjectiveComparatorTest {

	/**
	 * The &epsilon;-box objective comparator used for testing.
	 */
	private EpsilonBoxObjectiveComparator comparator;

	/**
	 * Setup the comparator for use by all test methods.
	 */
	@Before
	public void setUp() {
		comparator = new EpsilonBoxObjectiveComparator(0.5);
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		comparator = null;
	}

	/**
	 * Tests if the comparator correctly detects dominance.
	 */
	@Test
	public void testDominance() {
		Solution solution1 = new Solution(new double[] { 0.0, 0.0 });
		Solution solution2 = new Solution(new double[] { 1.0, 1.0 });
		Solution solution3 = new Solution(new double[] { 1.0, 0.0 });

		Assert.assertTrue(comparator.compare(solution1, solution2) < 0);
		Assert.assertFalse(comparator.isSameBox());
		Assert.assertTrue(comparator.compare(solution2, solution1) > 0);
		Assert.assertFalse(comparator.isSameBox());
		Assert.assertTrue(comparator.compare(solution1, solution3) < 0);
		Assert.assertFalse(comparator.isSameBox());
		Assert.assertTrue(comparator.compare(solution3, solution1) > 0);
		Assert.assertFalse(comparator.isSameBox());
		Assert.assertFalse(comparator.compare(solution2, solution3) < 0);
		Assert.assertFalse(comparator.isSameBox());
		Assert.assertFalse(comparator.compare(solution3, solution2) > 0);
		Assert.assertFalse(comparator.isSameBox());
	}

	/**
	 * Tests if the comparator correctly detects non-dominance.
	 */
	@Test
	public void testNondominance() {
		Solution solution1 = new Solution(new double[] { 0.75, 0.25 });
		Solution solution2 = new Solution(new double[] { 0.25, 0.75 });

		Assert.assertTrue(comparator.compare(solution1, solution2) == 0);
		Assert.assertFalse(comparator.isSameBox());
		Assert.assertTrue(comparator.compare(solution2, solution1) == 0);
		Assert.assertFalse(comparator.isSameBox());
	}

	/**
	 * Test if an {@code EpsilonBoxObjectiveComparator} correctly detects
	 * dominance within the same &epsilon;-box (i.e., selects the solution 
	 * nearer to the optimal corner).
	 */
	@Test
	public void testDominanceInBox() {
		Solution solution1 = new Solution(new double[] { 0.15, 0.35 });
		Solution solution2 = new Solution(new double[] { 0.2, 0.2 });

		Assert.assertTrue(comparator.compare(solution1, solution2) > 0);
		Assert.assertTrue(comparator.isSameBox());
		Assert.assertTrue(comparator.compare(solution2, solution1) < 0);
		Assert.assertTrue(comparator.isSameBox());
	}

	/**
	 * Tests if an {@code EpsilonBoxObjectiveComparator} correctly extends the
	 * epsilon array.
	 */
	@Test
	public void testEpsilonExtension() {
		EpsilonBoxDominanceComparator comparator = 
				new EpsilonBoxDominanceComparator(new double[] { 0.1, 0.2 });

		Assert.assertEquals(0.1, comparator.getEpsilon(0), Settings.EPS);
		Assert.assertEquals(0.2, comparator.getEpsilon(1), Settings.EPS);
		Assert.assertEquals(0.2, comparator.getEpsilon(2), Settings.EPS);
		Assert.assertEquals(0.2, comparator.getEpsilon(Integer.MAX_VALUE),
				Settings.EPS);
		Assert.assertEquals(2, comparator.getNumberOfDefinedEpsilons());
	}

}
