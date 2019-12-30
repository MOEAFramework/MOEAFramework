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
package org.moeaframework.core;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.comparator.NondominatedSortingComparator;

/**
 * Tests the {@link NondominatedSortingPopulation} class.
 */
public class NondominatedSortingPopulationTest {
	
	private static class TestNondominatedSortingPopulation 
	extends NondominatedSortingPopulation {
		
		private int numberOfUpdates;

		public TestNondominatedSortingPopulation() {
			super();
		}

		public TestNondominatedSortingPopulation(
				Iterable<? extends Solution> iterable) {
			super(iterable);
		}

		@Override
		public void update() {
			numberOfUpdates++;
			super.update();
		}

		public int getNumberOfUpdates() {
			return numberOfUpdates;
		}
		
	}

	/**
	 * Tests if the {@code truncate} method works correctly with solutions of
	 * differing ranks.
	 */
	@Test
	public void testRankTruncation() {
		NondominatedSortingPopulation population = 
				new NondominatedSortingPopulation();

		Solution solution1 = new Solution(new double[] { 0.5, 0.5 });
		Solution solution2 = new Solution(new double[] { 0.0, 0.0 });
		Solution solution3 = new Solution(new double[] { 1.0, 1.0 });

		population.add(solution1);
		population.add(solution2);
		population.add(solution3);

		population.truncate(1);

		Assert.assertFalse(population.contains(solution1));
		Assert.assertTrue(population.contains(solution2));
		Assert.assertFalse(population.contains(solution3));
	}

	/**
	 * Tests if {@code truncate} method works correctly with equally-ranked
	 * solutions.
	 */
	@Test
	public void testCrowdingTruncation() {
		NondominatedSortingPopulation population = 
				new NondominatedSortingPopulation();

		Solution solution1 = new Solution(new double[] { 0.0, 1.0 });
		Solution solution2 = new Solution(new double[] { 0.5, 0.5 });
		Solution solution3 = new Solution(new double[] { 1.0, 0.0 });

		population.add(solution1);
		population.add(solution2);
		population.add(solution3);

		population.truncate(2);

		Assert.assertTrue(population.contains(solution1));
		Assert.assertFalse(population.contains(solution2));
		Assert.assertTrue(population.contains(solution3));
	}
	
	@Test
	public void testSingularObjective() {
		NondominatedSortingPopulation population = 
				new NondominatedSortingPopulation();

		Solution solution1 = new Solution(new double[] { 0.0, 0.0, 1.0 });
		Solution solution2 = new Solution(new double[] { 0.5, 0.0, 0.5 });
		Solution solution3 = new Solution(new double[] { 1.0, 0.0, 0.0 });

		population.add(solution1);
		population.add(solution2);
		population.add(solution3);

		population.truncate(2);

		Assert.assertTrue(population.contains(solution1));
		Assert.assertFalse(population.contains(solution2));
		Assert.assertTrue(population.contains(solution3));
	}
	
	@Test
	public void testTruncate() {
		TestNondominatedSortingPopulation population = 
				new TestNondominatedSortingPopulation();

		Solution solution1 = new Solution(new double[] { 0.0, 0.0 });
		Solution solution2 = new Solution(new double[] { 0.0, 1.0 });
		Solution solution3 = new Solution(new double[] { 0.45, 0.55 });
		Solution solution4 = new Solution(new double[] { 0.5, 0.5 });
		Solution solution5 = new Solution(new double[] { 0.73, 0.27 });
		Solution solution6 = new Solution(new double[] { 0.74, 0.26 });
		Solution solution7 = new Solution(new double[] { 0.75, 0.25 });
		Solution solution8 = new Solution(new double[] { 1.0, 0.0 });

		population.add(solution1);
		population.add(solution2);
		population.add(solution3);
		population.add(solution4);
		population.add(solution5);
		population.add(solution6);
		population.add(solution7);
		population.add(solution8);

		population.truncate(5);

		Assert.assertTrue(population.contains(solution1));
		Assert.assertTrue(population.contains(solution2));
		Assert.assertTrue(population.contains(solution3));
		Assert.assertTrue(population.contains(solution4));
		Assert.assertFalse(population.contains(solution5));
		Assert.assertFalse(population.contains(solution6));
		Assert.assertFalse(population.contains(solution7));
		Assert.assertTrue(population.contains(solution8));
		
		Assert.assertEquals(1, population.getNumberOfUpdates());
	}
	
	@Test
	public void testPruning() {
		TestNondominatedSortingPopulation population = 
				new TestNondominatedSortingPopulation();

		Solution solution1 = new Solution(new double[] { 0.0, 0.0 });
		Solution solution2 = new Solution(new double[] { 0.0, 1.0 });
		Solution solution3 = new Solution(new double[] { 0.45, 0.55 });
		Solution solution4 = new Solution(new double[] { 0.5, 0.5 });
		Solution solution5 = new Solution(new double[] { 0.73, 0.27 });
		Solution solution6 = new Solution(new double[] { 0.74, 0.26 });
		Solution solution7 = new Solution(new double[] { 0.75, 0.25 });
		Solution solution8 = new Solution(new double[] { 1.0, 0.0 });

		population.add(solution1);
		population.add(solution2);
		population.add(solution3);
		population.add(solution4);
		population.add(solution5);
		population.add(solution6);
		population.add(solution7);
		population.add(solution8);

		population.prune(5);

		Assert.assertTrue(population.contains(solution1));
		Assert.assertTrue(population.contains(solution2));
		Assert.assertTrue(population.contains(solution3));
		Assert.assertFalse(population.contains(solution4));
		Assert.assertFalse(population.contains(solution5));
		Assert.assertFalse(population.contains(solution6));
		Assert.assertTrue(population.contains(solution7));
		Assert.assertTrue(population.contains(solution8));
		
		Assert.assertEquals(1, population.getNumberOfUpdates());
	}
	
	@Test
	public void testUpdate() {
		Solution solution1 = new Solution(new double[] { 0.0, 0.0 });
		Solution solution2 = new Solution(new double[] { 0.0, 1.0 });
		Solution solution3 = new Solution(new double[] { 0.45, 0.55 });
		Solution solution4 = new Solution(new double[] { 0.5, 0.5 });
		Solution solution5 = new Solution(new double[] { 0.73, 0.27 });
		Solution solution6 = new Solution(new double[] { 0.74, 0.26 });
		Solution solution7 = new Solution(new double[] { 0.75, 0.25 });
		Solution solution8 = new Solution(new double[] { 1.0, 0.0 });
		
		TestNondominatedSortingPopulation population = 
			new TestNondominatedSortingPopulation(
					Arrays.asList(solution1, solution2));

		population.get(0); //update
		population.add(solution3);
		population.add(solution4);
		population.get(0); //update
		population.get(1);
		population.remove(0);
		population.get(0); //update
		population.remove(solution3);
		population.truncate(1); //update
		population.addAll(Arrays.asList(solution5, solution6));
		population.prune(2); //update
		population.addAll(new Solution[] { solution7, solution8 });
		
		Iterator<Solution> iterator = population.iterator();
		iterator.hasNext();
		iterator.next(); //update
		iterator.remove();
		iterator.next(); //update
		iterator.next();
		
		population.clear();
		population.sort(new NondominatedSortingComparator()); //update
		
		Assert.assertEquals(8, population.getNumberOfUpdates());
	}

}
