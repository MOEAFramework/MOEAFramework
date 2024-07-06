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
package org.moeaframework.util.clustering;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.mock.MockSolution;

public class DistanceMeasureTest {
	
	@Test
	public void testEuclideanDistance() {
		ClusterableSolution first = ClusterableSolution.withObjectives(MockSolution.of().withObjectives(0.0, 1.0));
		ClusterableSolution second = ClusterableSolution.withObjectives(MockSolution.of().withObjectives(1.0, 0.0));
		
		DistanceMeasure<ClusterableSolution> measure = DistanceMeasure.euclideanDistance();
		
		Assert.assertEquals(0.0, measure.compute(first, first));
		Assert.assertEquals(Math.sqrt(2.0), measure.compute(first, second));
		Assert.assertEquals(Math.sqrt(2.0), measure.compute(second, first));
	}
	
	@Test
	public void testManhattanDistance() {
		ClusterableSolution first = ClusterableSolution.withObjectives(MockSolution.of().withObjectives(0.0, 1.0));
		ClusterableSolution second = ClusterableSolution.withObjectives(MockSolution.of().withObjectives(1.0, 0.0));
		
		DistanceMeasure<ClusterableSolution> measure = DistanceMeasure.manhattanDistance();
		
		Assert.assertEquals(0.0, measure.compute(first, first));
		Assert.assertEquals(2.0, measure.compute(first, second));
		Assert.assertEquals(2.0, measure.compute(second, first));
	}
	
	@Test
	public void testChebyshevDistance() {
		ClusterableSolution first = ClusterableSolution.withObjectives(MockSolution.of().withObjectives(0.0, 1.0));
		ClusterableSolution second = ClusterableSolution.withObjectives(MockSolution.of().withObjectives(1.0, 0.0));
		
		DistanceMeasure<ClusterableSolution> measure = DistanceMeasure.chebyshevDistance();
		
		Assert.assertEquals(0.0, measure.compute(first, first));
		Assert.assertEquals(1.0, measure.compute(first, second));
		Assert.assertEquals(1.0, measure.compute(second, first));
	}
	
}
