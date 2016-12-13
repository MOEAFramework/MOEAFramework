package org.moeaframework.algorithm.single;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Solution;

public class LinearObjectiveComparatorTest {
	
	@Test
	public void testCalculateFitness() {
		Solution solution = TestUtils.newSolution(0.0, 1.0);
		double[] weights = new double[] { 1.0, 1.0 };
		Assert.assertEquals(LinearObjectiveComparator.calculateFitness(solution, weights), 1.0, TestThresholds.SOLUTION_EPS);
		
		solution = TestUtils.newSolution(1.0, 0.0);
		Assert.assertEquals(LinearObjectiveComparator.calculateFitness(solution, weights), 1.0, TestThresholds.SOLUTION_EPS);
		
		solution = TestUtils.newSolution(-1.0, 0.0);
		Assert.assertEquals(LinearObjectiveComparator.calculateFitness(solution, weights), -1.0, TestThresholds.SOLUTION_EPS);
		
		solution = TestUtils.newSolution(0.0, -1.0);
		Assert.assertEquals(LinearObjectiveComparator.calculateFitness(solution, weights), -1.0, TestThresholds.SOLUTION_EPS);
		
		solution = TestUtils.newSolution(1.0, 1.0);
		Assert.assertEquals(LinearObjectiveComparator.calculateFitness(solution, weights), 2.0, TestThresholds.SOLUTION_EPS);
		
		solution = TestUtils.newSolution(1.0, -1.0);
		Assert.assertEquals(LinearObjectiveComparator.calculateFitness(solution, weights), 0.0, TestThresholds.SOLUTION_EPS);
		
		solution = TestUtils.newSolution(0.0, 0.0);
		Assert.assertEquals(LinearObjectiveComparator.calculateFitness(solution, weights), 0.0, TestThresholds.SOLUTION_EPS);
	}
	
	@Test
	public void testNoWeights() {
		LinearObjectiveComparator comparator = new LinearObjectiveComparator();
		
		Solution solution1 = TestUtils.newSolution(0.0, 1.0);
		Solution solution2 = TestUtils.newSolution(1.0, 0.0);
		Assert.assertEquals(comparator.compare(solution1, solution2), 0);
		
		solution2 = TestUtils.newSolution(0.5, 0.5);
		Assert.assertEquals(comparator.compare(solution1, solution2), 0);
		
		solution2 = TestUtils.newSolution(0.5, 0.0);
		Assert.assertEquals(comparator.compare(solution1, solution2), 1);
		
		solution2 = TestUtils.newSolution(0.5, 1.0);
		Assert.assertEquals(comparator.compare(solution1, solution2), -1);
	}
	
	@Test
	public void testGivenWeights() {
		LinearObjectiveComparator comparator = new LinearObjectiveComparator(
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
