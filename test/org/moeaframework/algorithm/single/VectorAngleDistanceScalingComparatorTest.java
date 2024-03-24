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
package org.moeaframework.algorithm.single;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Solution;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.util.Vector;

public class VectorAngleDistanceScalingComparatorTest {
	
	private double calculate(Solution solution, double[] weights) {
		return VectorAngleDistanceScalingComparator.calculateFitness(solution, weights, 100.0);
	}
	
	@Test
	public void testCalculateFitness() {
		double[] weights = Vector.normalize(new double[] { 1.0, 1.0 });
		
		Assert.assertTrue(calculate(MockSolution.of().withObjectives(1.0, 1.0), weights) ==
				calculate(MockSolution.of().withObjectives(1.0, 1.0), weights));
		Assert.assertTrue(calculate(MockSolution.of().withObjectives(1.0, 1.0), weights) >
				calculate(MockSolution.of().withObjectives(0.5, 0.5), weights));
		Assert.assertTrue(calculate(MockSolution.of().withObjectives(1.0, 1.0), weights) <
				calculate(MockSolution.of().withObjectives(1.5, 1.5), weights));
		Assert.assertTrue(calculate(MockSolution.of().withObjectives(1.0, 1.0), weights) <
				calculate(MockSolution.of().withObjectives(1.0, 0.0), weights));
		Assert.assertTrue(calculate(MockSolution.of().withObjectives(0.5, 0.0), weights) <
				calculate(MockSolution.of().withObjectives(1.0, 0.0), weights));
		Assert.assertTrue(calculate(MockSolution.of().withObjectives(0.75, 0.35), weights) <
				calculate(MockSolution.of().withObjectives(0.75, 0.25), weights));
	}

}
