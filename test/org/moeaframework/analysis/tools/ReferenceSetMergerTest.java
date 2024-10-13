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
package org.moeaframework.analysis.tools;

import java.util.List;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.mock.MockSolution;

public class ReferenceSetMergerTest {

	@Test
	public void test() {
		Solution solution1 = MockSolution.of().withObjectives(1.0, 2.0, 3.0);
		Solution solution2 = MockSolution.of().withObjectives(1.0, 3.0, 2.0);
		Solution solution3 = MockSolution.of().withObjectives(2.0, 1.0, 3.0);
		Solution solution4 = MockSolution.of().withObjectives(1.0, 1.0, 3.0);
		Solution solution5 = MockSolution.of().withObjectives(3.0, 1.0, 1.0);
		
		NondominatedPopulation population1 = new NondominatedPopulation(List.of(solution1, solution2, solution3));
		NondominatedPopulation population2 = new NondominatedPopulation(List.of(solution4, solution5));
		
		ReferenceSetMerger merger = new ReferenceSetMerger();
		merger.add("population1", population1);
		merger.add("population2", population2);
		
		Assert.assertContains(merger.getSources(), "population1");
		Assert.assertContains(merger.getSources(), "population2");

		Assert.assertSize(1, merger.getContributionFrom("population1"));
		Assert.assertSize(2, merger.getContributionFrom("population2"));
		Assert.assertSize(3, merger.getCombinedPopulation());
		
		Assert.assertContains(merger.getContributionFrom("population1"), solution2);
		Assert.assertContains(merger.getContributionFrom("population2"), solution4);
		Assert.assertContains(merger.getContributionFrom("population2"), solution5);
		Assert.assertContains(merger.getCombinedPopulation(), solution2);
		Assert.assertContains(merger.getCombinedPopulation(), solution4);
		Assert.assertContains(merger.getCombinedPopulation(), solution5);
		
		Assert.assertEquals(merger.getPopulation("population1"), population1);
		Assert.assertEquals(merger.getPopulation("population2"), population2);
	}
	
	@Test
	public void testDuplicateSolution() {
		Solution solution1 = MockSolution.of().withObjectives(1.0, 2.0, 3.0);
		Solution solution2 = MockSolution.of().withObjectives(1.0, 2.0, 3.0);
		
		NondominatedPopulation population1 = new NondominatedPopulation(List.of(solution1));
		NondominatedPopulation population2 = new NondominatedPopulation(List.of(solution2));
		
		ReferenceSetMerger merger = new ReferenceSetMerger();
		merger.add("population1", population1);
		merger.add("population2", population2);
		
		Assert.assertContains(merger.getSources(), "population1");
		Assert.assertContains(merger.getSources(), "population2");

		Assert.assertSize(1, merger.getContributionFrom("population1"));
		Assert.assertSize(1, merger.getContributionFrom("population2"));
		Assert.assertSize(1, merger.getCombinedPopulation());
		
		Assert.assertContains(merger.getContributionFrom("population1"), solution1);
		Assert.assertContains(merger.getContributionFrom("population2"), solution2);
		Assert.assertContains(merger.getCombinedPopulation(), solution1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDuplicateSourceKey() {
		NondominatedPopulation population1 = new NondominatedPopulation();
		NondominatedPopulation population2 = new NondominatedPopulation();
		
		ReferenceSetMerger merger = new ReferenceSetMerger();
		merger.add("population", population1);
		merger.add("population", population2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMissingSourceKey() {
		NondominatedPopulation population1 = new NondominatedPopulation();
		
		ReferenceSetMerger merger = new ReferenceSetMerger();
		merger.add("population1", population1);
		merger.getContributionFrom("population2");
	}
	
}
