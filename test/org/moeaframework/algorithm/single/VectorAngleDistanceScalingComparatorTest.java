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
