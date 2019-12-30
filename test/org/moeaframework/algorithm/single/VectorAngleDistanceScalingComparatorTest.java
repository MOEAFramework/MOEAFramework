package org.moeaframework.algorithm.single;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Solution;
import org.moeaframework.util.Vector;

public class VectorAngleDistanceScalingComparatorTest {
	
	@Test
	public void testCalculateFitness() {
		Solution solution1 = TestUtils.newSolution(1.0, 1.0);
		Solution solution2 = TestUtils.newSolution(1.0, 1.0);
		double[] weights = Vector.normalize(new double[] { 1.0, 1.0 });
		
		Assert.assertTrue(VectorAngleDistanceScalingComparator.calculateFitness(solution1, weights, 100.0) ==
				VectorAngleDistanceScalingComparator.calculateFitness(solution2, weights, 100.0));
		
		solution2 = TestUtils.newSolution(0.5, 0.5);
		Assert.assertTrue(VectorAngleDistanceScalingComparator.calculateFitness(solution1, weights, 100.0) >
				VectorAngleDistanceScalingComparator.calculateFitness(solution2, weights, 100.0));
		
		solution2 = TestUtils.newSolution(1.5, 1.5);
		Assert.assertTrue(VectorAngleDistanceScalingComparator.calculateFitness(solution1, weights, 100.0) <
				VectorAngleDistanceScalingComparator.calculateFitness(solution2, weights, 100.0));
		
		solution2 = TestUtils.newSolution(1.0, 0.0);
		Assert.assertTrue(VectorAngleDistanceScalingComparator.calculateFitness(solution1, weights, 100.0) <
				VectorAngleDistanceScalingComparator.calculateFitness(solution2, weights, 100.0));
		
		solution1 = TestUtils.newSolution(0.5, 0.0);
		solution2 = TestUtils.newSolution(1.0, 0.0);
		Assert.assertTrue(VectorAngleDistanceScalingComparator.calculateFitness(solution1, weights, 100.0) <
				VectorAngleDistanceScalingComparator.calculateFitness(solution2, weights, 100.0));
		
		solution1 = TestUtils.newSolution(0.75, 0.35);
		solution2 = TestUtils.newSolution(0.75, 0.25);
		Assert.assertTrue(VectorAngleDistanceScalingComparator.calculateFitness(solution1, weights, 100.0) <
				VectorAngleDistanceScalingComparator.calculateFitness(solution2, weights, 100.0));
	}

}
