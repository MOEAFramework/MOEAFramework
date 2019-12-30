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
