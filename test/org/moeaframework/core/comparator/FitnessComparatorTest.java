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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Solution;

public class FitnessComparatorTest {

	private FitnessComparator comparator;

	@Before
	public void setUp() {
		comparator = new FitnessComparator(false);
	}

	@After
	public void tearDown() {
		comparator = null;
	}

	@Test
	public void testDominance() {
		Solution solution1 = new Solution(0, 0);
		Solution solution2 = new Solution(0, 0);

		FitnessEvaluator.setFitness(solution1, 0.0);
		FitnessEvaluator.setFitness(solution2, 1.0);

		Assert.assertTrue(comparator.compare(solution1, solution2) < 0);
		Assert.assertTrue(comparator.compare(solution2, solution1) > 0);
	}

	@Test
	public void testNondominance() {
		Solution solution1 = new Solution(0, 0);
		Solution solution2 = new Solution(0, 0);

		FitnessEvaluator.setFitness(solution1, 1.0);
		FitnessEvaluator.setFitness(solution2, 1.0);

		Assert.assertTrue(comparator.compare(solution1, solution2) == 0);
		Assert.assertTrue(comparator.compare(solution2, solution1) == 0);
	}

	@Test(expected = NullPointerException.class)
	public void testMissingCrowdingAttribute() {
		Solution solution1 = new Solution(0, 0);
		Solution solution2 = new Solution(0, 0);

		comparator.compare(solution1, solution2);
	}

}
