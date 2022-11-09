/* Copyright 2009-2022 David Hadka
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
package org.moeaframework.algorithm.single;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Solution;

public class MinMaxObjectiveComparatorTest {
	
	@Test
	public void testCalculateFitness() {
		Solution solution = TestUtils.newSolution(0.0, 1.0);
		double[] weights = new double[] { 1.0, 1.0 };
		Assert.assertEquals(MinMaxObjectiveComparator.calculateFitness(solution, weights), 1.0, TestThresholds.SOLUTION_EPS);
		
		solution = TestUtils.newSolution(1.0, 0.0);
		Assert.assertEquals(MinMaxObjectiveComparator.calculateFitness(solution, weights), 1.0, TestThresholds.SOLUTION_EPS);
		
		solution = TestUtils.newSolution(-1.0, 0.0);
		Assert.assertEquals(MinMaxObjectiveComparator.calculateFitness(solution, weights), 0.0, TestThresholds.SOLUTION_EPS);
		
		solution = TestUtils.newSolution(0.0, -1.0);
		Assert.assertEquals(MinMaxObjectiveComparator.calculateFitness(solution, weights), 0.0, TestThresholds.SOLUTION_EPS);
		
		solution = TestUtils.newSolution(1.0, 1.0);
		Assert.assertEquals(MinMaxObjectiveComparator.calculateFitness(solution, weights), 1.0, TestThresholds.SOLUTION_EPS);
		
		solution = TestUtils.newSolution(1.0, -1.0);
		Assert.assertEquals(MinMaxObjectiveComparator.calculateFitness(solution, weights), 1.0, TestThresholds.SOLUTION_EPS);
		
		solution = TestUtils.newSolution(0.0, 0.0);
		Assert.assertEquals(MinMaxObjectiveComparator.calculateFitness(solution, weights), 0.0, TestThresholds.SOLUTION_EPS);
	}
	
	@Test
	public void testNoWeights() {
		MinMaxObjectiveComparator comparator = new MinMaxObjectiveComparator();
		
		Solution solution1 = TestUtils.newSolution(0.0, 1.0);
		Solution solution2 = TestUtils.newSolution(1.0, 0.0);
		Assert.assertEquals(comparator.compare(solution1, solution2), 0);
		
		solution2 = TestUtils.newSolution(0.5, 0.5);
		Assert.assertEquals(comparator.compare(solution1, solution2), 1);
		
		solution2 = TestUtils.newSolution(1.5, 1.0);
		Assert.assertEquals(comparator.compare(solution1, solution2), -1);
		
		solution2 = TestUtils.newSolution(1.0, 1.0);
		Assert.assertEquals(comparator.compare(solution1, solution2), 0);
	}
	
	@Test
	public void testGivenWeights() {
		MinMaxObjectiveComparator comparator = new MinMaxObjectiveComparator(
				new double[] { 0.5, 0.25 });
		
		Solution solution1 = TestUtils.newSolution(0.0, 1.0);
		Solution solution2 = TestUtils.newSolution(1.0, 0.0);
		Assert.assertEquals(comparator.compare(solution1, solution2), -1);
		
		solution2 = TestUtils.newSolution(0.5, 0.0);
		Assert.assertEquals(comparator.compare(solution1, solution2), 0);
		
		solution2 = TestUtils.newSolution(0.25, 0.0);
		Assert.assertEquals(comparator.compare(solution1, solution2), 1);
	}

}
