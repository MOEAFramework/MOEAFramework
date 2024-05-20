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
package org.moeaframework.core.comparator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Solution;
import org.moeaframework.core.attribute.CrowdingDistance;
import org.moeaframework.core.attribute.Rank;

public class NondominatedSortingComparatorTest {

	private NondominatedSortingComparator comparator;

	@Before
	public void setUp() {
		comparator = new NondominatedSortingComparator();
	}

	@After
	public void tearDown() {
		comparator = null;
	}

	@Test
	public void testDominance() {
		Solution solution1 = new Solution(0, 0);
		Solution solution2 = new Solution(0, 0);
		Solution solution3 = new Solution(0, 0);

		Rank.setAttribute(solution1, 0);
		CrowdingDistance.setAttribute(solution1, Double.POSITIVE_INFINITY);
		Rank.setAttribute(solution2, 1);
		CrowdingDistance.setAttribute(solution2, Double.POSITIVE_INFINITY);
		Rank.setAttribute(solution3, 1);
		CrowdingDistance.setAttribute(solution3, 1.0);

		// ranks used correctly
		Assert.assertTrue(comparator.compare(solution1, solution2) < 0);
		Assert.assertTrue(comparator.compare(solution1, solution3) < 0);
		Assert.assertTrue(comparator.compare(solution2, solution1) > 0);
		Assert.assertTrue(comparator.compare(solution3, solution1) > 0);

		// crowding distance used correctly
		Assert.assertTrue(comparator.compare(solution2, solution3) < 0);
		Assert.assertTrue(comparator.compare(solution3, solution2) > 0);
	}

	@Test
	public void testNondominance() {
		Solution solution1 = new Solution(0, 0);
		Solution solution2 = new Solution(0, 0);

		Rank.setAttribute(solution1, 0);
		Rank.setAttribute(solution2, 0);

		// test infinite crowding distance
		CrowdingDistance.setAttribute(solution1, Double.POSITIVE_INFINITY);
		CrowdingDistance.setAttribute(solution2, Double.POSITIVE_INFINITY);

		Assert.assertTrue(comparator.compare(solution1, solution2) == 0);
		Assert.assertTrue(comparator.compare(solution2, solution1) == 0);

		// test finite crowding distance
		CrowdingDistance.setAttribute(solution1, 1.0);
		CrowdingDistance.setAttribute(solution2, 1.0);

		Assert.assertTrue(comparator.compare(solution1, solution2) == 0);
		Assert.assertTrue(comparator.compare(solution2, solution1) == 0);
	}

	@Test(expected = NullPointerException.class)
	public void testMissingRankAttribute() {
		Solution solution1 = new Solution(0, 0);
		Solution solution2 = new Solution(0, 0);

		comparator.compare(solution1, solution2);
	}

	@Test(expected = NullPointerException.class)
	public void testMissingCrowdingAttribute() {
		Solution solution1 = new Solution(0, 0);
		Solution solution2 = new Solution(0, 0);

		Rank.setAttribute(solution1, 0);
		Rank.setAttribute(solution2, 0);

		comparator.compare(solution1, solution2);
	}

}
