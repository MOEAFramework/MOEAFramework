/* Copyright 2009-2016 David Hadka
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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link NondominatedSorting} class.
 */
public class NondominatedSortingTest {

	/**
	 * The population undergoing fast non-dominated sorting.
	 */
	protected Population population;

	/**
	 * The non-dominated sorting instance.
	 */
	protected NondominatedSorting sorting;

	/**
	 * Prepare an empty population and a sorting object using a dummy problem.
	 */
	@Before
	public void setUp() {
		population = new Population();
		sorting = new NondominatedSorting();
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		population = null;
		sorting = null;
	}

	/**
	 * Returns the rank of the specified solution.
	 * 
	 * @param solution the solution
	 * @return the rank of the specified solution
	 */
	private int getRank(Solution solution) {
		return (Integer)solution.getAttribute(RANK_ATTRIBUTE);
	}
	
	/**
	 * Returns the crowding distance of the specified solution.
	 * 
	 * @param solution the solution
	 * @return the crowding distance of the specified solution
	 */
	private double getCrowding(Solution solution) {
		return (Double)solution.getAttribute(CROWDING_ATTRIBUTE);
	}

	/**
	 * Tests if the rank attribute is correctly computed.
	 */
	@Test
	public void testRankAssignment() {
		Solution solution1 = new Solution(new double[] { 0.0, 0.0 });
		Solution solution2 = new Solution(new double[] { 0.5, 0.5 });
		Solution solution3 = new Solution(new double[] { 1.0, 1.0 });

		population.add(solution1);
		population.add(solution2);
		population.add(solution3);

		sorting.evaluate(population);
		
		assertHasAttributes(population);

		Assert.assertEquals(0, getRank(solution1));
		Assert.assertEquals(1, getRank(solution2));
		Assert.assertEquals(2, getRank(solution3));
	}

	/**
	 * Tests if the crowding distance is correctly computed.
	 */
	@Test
	public void testCrowdingAssignment() {
		Solution solution1 = new Solution(new double[] { 0.0, 1.0 });
		Solution solution2 = new Solution(new double[] { 0.5, 0.5 });
		Solution solution3 = new Solution(new double[] { 1.0, 0.0 });

		population.add(solution1);
		population.add(solution2);
		population.add(solution3);

		sorting.evaluate(population);
		
		assertHasAttributes(population);

		Assert.assertEquals(0, getRank(solution1));
		Assert.assertEquals(0, getRank(solution2));
		Assert.assertEquals(0, getRank(solution3));
		Assert.assertEquals(Double.POSITIVE_INFINITY, getCrowding(solution1),
				Settings.EPS);
		Assert.assertEquals(2.0, getCrowding(solution2), Settings.EPS);
		Assert.assertEquals(Double.POSITIVE_INFINITY, getCrowding(solution3),
				Settings.EPS);
	}
	
	/**
	 * Tests if two identical solutions are ranked correctly.
	 */
	@Test
	public void testIdenticalSolutions() {
		Solution solution1 = new Solution(new double[] { 0.0, 1.0 });
		Solution solution2 = new Solution(new double[] { 0.5, 0.5 });
		Solution solution3 = new Solution(new double[] { 0.0, 1.0 });

		population.add(solution1);
		population.add(solution2);
		population.add(solution3);

		sorting.evaluate(population);
		
		assertHasAttributes(population);

		Assert.assertEquals(0, getRank(solution1));
		Assert.assertEquals(0, getRank(solution2));
		Assert.assertEquals(0, getRank(solution3));
		
		Assert.assertTrue(Double.isInfinite(getCrowding(solution1)));
		Assert.assertEquals(0.0, getCrowding(solution3), Settings.EPS);
	}
	
	private void assertHasAttributes(Population population) {
		for (Solution solution : population) {
			Assert.assertTrue(solution.hasAttribute(RANK_ATTRIBUTE));
			Assert.assertTrue(solution.hasAttribute(CROWDING_ATTRIBUTE));
		}
	}

}
