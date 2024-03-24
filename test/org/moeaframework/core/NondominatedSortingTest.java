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
package org.moeaframework.core;

import static org.moeaframework.core.FastNondominatedSorting.CROWDING_ATTRIBUTE;
import static org.moeaframework.core.FastNondominatedSorting.RANK_ATTRIBUTE;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.mock.MockSolution;

public class NondominatedSortingTest {

	protected Population population;
	protected NondominatedSorting sorting;

	@Before
	public void setUp() {
		population = new Population();
		sorting = new NondominatedSorting();
	}

	@After
	public void tearDown() {
		population = null;
		sorting = null;
	}

	@Test
	public void testRankAssignment() {
		Solution solution1 = MockSolution.of().withObjectives(0.0, 0.0);
		Solution solution2 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution solution3 = MockSolution.of().withObjectives(1.0, 1.0);

		population.addAll(List.of(solution1, solution2, solution3));
		sorting.evaluate(population);
		
		assertHasAttributes(population);

		Assert.assertEquals(0, NondominatedSorting.getRank(solution1));
		Assert.assertEquals(1, NondominatedSorting.getRank(solution2));
		Assert.assertEquals(2, NondominatedSorting.getRank(solution3));
	}

	@Test
	public void testCrowdingAssignment() {
		Solution solution1 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution solution3 = MockSolution.of().withObjectives(1.0, 0.0);
		
		population.addAll(List.of(solution1, solution2, solution3));
		sorting.evaluate(population);
		
		assertHasAttributes(population);

		Assert.assertEquals(0, NondominatedSorting.getRank(solution1));
		Assert.assertEquals(0, NondominatedSorting.getRank(solution2));
		Assert.assertEquals(0, NondominatedSorting.getRank(solution3));
		Assert.assertEquals(Double.POSITIVE_INFINITY, NondominatedSorting.getCrowding(solution1), Settings.EPS);
		Assert.assertEquals(2.0, NondominatedSorting.getCrowding(solution2), Settings.EPS);
		Assert.assertEquals(Double.POSITIVE_INFINITY, NondominatedSorting.getCrowding(solution3), Settings.EPS);
	}

	@Test
	public void testIdenticalSolutions() {
		Solution solution1 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution solution3 = MockSolution.of().withObjectives(0.0, 1.0);

		population.addAll(List.of(solution1, solution2, solution3));
		sorting.evaluate(population);
		
		assertHasAttributes(population);

		Assert.assertEquals(0, NondominatedSorting.getRank(solution1));
		Assert.assertEquals(0, NondominatedSorting.getRank(solution2));
		Assert.assertEquals(0, NondominatedSorting.getRank(solution3));
		
		Assert.assertTrue(Double.isInfinite(NondominatedSorting.getCrowding(solution1)));
		Assert.assertEquals(0.0, NondominatedSorting.getCrowding(solution3), Settings.EPS);
	}
	
	@Test
	public void testSingularDimension() {
		Solution solution1 = MockSolution.of().withObjectives(0.0, 0.0, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(0.5, 0.0, 0.5);
		Solution solution3 = MockSolution.of().withObjectives(1.0, 0.0, 0.0);

		population.addAll(List.of(solution1, solution2, solution3));
		sorting.evaluate(population);
		
		assertHasAttributes(population);

		Assert.assertEquals(0, NondominatedSorting.getRank(solution1));
		Assert.assertEquals(0, NondominatedSorting.getRank(solution2));
		Assert.assertEquals(0, NondominatedSorting.getRank(solution3));
		
		Assert.assertTrue(Double.isInfinite(NondominatedSorting.getCrowding(solution1)));
		Assert.assertFalse(Double.isNaN(NondominatedSorting.getCrowding(solution2)));
		Assert.assertTrue(Double.isInfinite(NondominatedSorting.getCrowding(solution3)));
	}
	
	private void assertHasAttributes(Population population) {
		for (Solution solution : population) {
			Assert.assertTrue(solution.hasAttribute(RANK_ATTRIBUTE));
			Assert.assertTrue(solution.hasAttribute(CROWDING_ATTRIBUTE));
		}
	}

}
