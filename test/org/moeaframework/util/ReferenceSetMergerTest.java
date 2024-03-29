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
package org.moeaframework.util;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.analysis.tools.ReferenceSetMerger;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
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
		
		Assert.assertTrue(merger.getSources().containsAll(Arrays.asList("population1", "population2")));

		Assert.assertEquals(1, merger.getContributionFrom("population1").size());
		Assert.assertEquals(2, merger.getContributionFrom("population2").size());
		Assert.assertEquals(0, merger.getContributionFrom("population3").size());
		Assert.assertEquals(3, merger.getCombinedPopulation().size());
		
		Assert.assertTrue(merger.getContributionFrom("population1").contains(solution2));
		Assert.assertTrue(merger.getContributionFrom("population2").containsAll(
				new Solution[] { solution4, solution5 }));
		Assert.assertTrue(merger.getCombinedPopulation().containsAll(
				new Solution[] { solution2, solution4, solution5 }));
		
		TestUtils.assertEquals(merger.getPopulation("population1"), population1);
		TestUtils.assertEquals(merger.getPopulation("population2"), population2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDuplicateSourceKey() {
		NondominatedPopulation population1 = new NondominatedPopulation();
		NondominatedPopulation population2 = new NondominatedPopulation();
		
		ReferenceSetMerger merger = new ReferenceSetMerger();
		merger.add("population", population1);
		merger.add("population", population2);
	}
	
}
