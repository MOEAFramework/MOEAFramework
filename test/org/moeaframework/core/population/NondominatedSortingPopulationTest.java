/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.core.population;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.NondominatedSortingComparator;
import org.moeaframework.mock.MockSolution;

public class NondominatedSortingPopulationTest {
	
	private static class TestNondominatedSortingPopulation extends NondominatedSortingPopulation {
		
		private int numberOfUpdates;

		public TestNondominatedSortingPopulation() {
			super();
		}

		public TestNondominatedSortingPopulation(Iterable<? extends Solution> iterable) {
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

	@Test
	public void testRankTruncation() {
		Solution solution1 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution solution2 = MockSolution.of().withObjectives(0.0, 0.0);
		Solution solution3 = MockSolution.of().withObjectives(1.0, 1.0);
		
		NondominatedSortingPopulation population = new NondominatedSortingPopulation();
		population.addAll(List.of(solution1, solution2, solution3));

		population.truncate(1);

		Assert.assertFalse(population.contains(solution1));
		Assert.assertTrue(population.contains(solution2));
		Assert.assertFalse(population.contains(solution3));
	}

	@Test
	public void testCrowdingTruncation() {
		Solution solution1 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution solution3 = MockSolution.of().withObjectives(1.0, 0.0);
		
		NondominatedSortingPopulation population = new NondominatedSortingPopulation();
		population.addAll(List.of(solution1, solution2, solution3));

		population.truncate(2);

		Assert.assertTrue(population.contains(solution1));
		Assert.assertFalse(population.contains(solution2));
		Assert.assertTrue(population.contains(solution3));
	}
	
	@Test
	public void testSingularObjective() {
		Solution solution1 = MockSolution.of().withObjectives(0.0, 0.0, 1.0);
		Solution solution2 = MockSolution.of().withObjectives(0.5, 0.0, 0.5);
		Solution solution3 = MockSolution.of().withObjectives(1.0, 0.0, 0.0);
		
		NondominatedSortingPopulation population = new NondominatedSortingPopulation();
		population.addAll(List.of(solution1, solution2, solution3));

		population.truncate(2);

		Assert.assertTrue(population.contains(solution1));
		Assert.assertFalse(population.contains(solution2));
		Assert.assertTrue(population.contains(solution3));
	}
	
	@Test
	public void testTruncate() {
		Solution solution1 = MockSolution.of().withObjectives(0.0, 0.0);
		Solution solution2 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution solution3 = MockSolution.of().withObjectives(0.45, 0.55);
		Solution solution4 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution solution5 = MockSolution.of().withObjectives(0.73, 0.27);
		Solution solution6 = MockSolution.of().withObjectives(0.74, 0.26);
		Solution solution7 = MockSolution.of().withObjectives(0.75, 0.25);
		Solution solution8 = MockSolution.of().withObjectives(1.0, 0.0);
		
		TestNondominatedSortingPopulation population = new TestNondominatedSortingPopulation();
		population.addAll(List.of(solution1, solution2, solution3, solution4, solution5, solution6, solution7, solution8));

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
		Solution solution1 = MockSolution.of().withObjectives(0.0, 0.0);
		Solution solution2 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution solution3 = MockSolution.of().withObjectives(0.45, 0.55);
		Solution solution4 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution solution5 = MockSolution.of().withObjectives(0.73, 0.27);
		Solution solution6 = MockSolution.of().withObjectives(0.74, 0.26);
		Solution solution7 = MockSolution.of().withObjectives(0.75, 0.25);
		Solution solution8 = MockSolution.of().withObjectives(1.0, 0.0);
		
		TestNondominatedSortingPopulation population = new TestNondominatedSortingPopulation();
		population.addAll(List.of(solution1, solution2, solution3, solution4, solution5, solution6, solution7, solution8));

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
		Solution solution1 = MockSolution.of().withObjectives(0.0, 0.0);
		Solution solution2 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution solution3 = MockSolution.of().withObjectives(0.45, 0.55);
		Solution solution4 = MockSolution.of().withObjectives(0.5, 0.5);
		Solution solution5 = MockSolution.of().withObjectives(0.73, 0.27);
		Solution solution6 = MockSolution.of().withObjectives(0.74, 0.26);
		Solution solution7 = MockSolution.of().withObjectives(0.75, 0.25);
		Solution solution8 = MockSolution.of().withObjectives(1.0, 0.0);
		
		TestNondominatedSortingPopulation population = new TestNondominatedSortingPopulation(
				List.of(solution1, solution2));

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
	
	@Test
	public void testCopy() {
		NondominatedSortingPopulation population = new NondominatedSortingPopulation();
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		
		NondominatedSortingPopulation copy = population.copy();
		
		Assert.assertNotSame(population, copy);
		Assert.assertSame(population.getComparator(), copy.getComparator());
		Assert.assertEquals(population, copy, true);
	}

}
