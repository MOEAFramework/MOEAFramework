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

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.mock.MockSolution;

public class CachedDistanceMeasureTest {
	
	@Test
	public void testSymmetric() {
		ClusterableSolution first = ClusterableSolution.withObjectives(MockSolution.of().withObjectives(0.0, 1.0));
		ClusterableSolution second = ClusterableSolution.withObjectives(MockSolution.of().withObjectives(1.0, 0.0));
		
		CountingDistanceMeasure<ClusterableSolution> counter = new CountingDistanceMeasure<>();
		DistanceMeasure<ClusterableSolution> measure = new CachedDistanceMeasure<>(counter, true);
		
		Assert.assertEquals(0.0, measure.compute(first, first));
		Assert.assertEquals(Math.sqrt(2.0) + 1.0, measure.compute(first, second));
		Assert.assertEquals(Math.sqrt(2.0) + 1.0, measure.compute(second, first));
		Assert.assertEquals(2, counter.getCallCount());
	}
	
	@Test
	public void testNonSymmetric() {
		ClusterableSolution first = ClusterableSolution.withObjectives(MockSolution.of().withObjectives(0.0, 1.0));
		ClusterableSolution second = ClusterableSolution.withObjectives(MockSolution.of().withObjectives(1.0, 0.0));
		
		CountingDistanceMeasure<ClusterableSolution> counter = new CountingDistanceMeasure<>();
		DistanceMeasure<ClusterableSolution> measure = new CachedDistanceMeasure<>(counter, false);
		
		Assert.assertEquals(0.0, measure.compute(first, first));
		Assert.assertEquals(Math.sqrt(2.0) + 1.0, measure.compute(first, second));
		Assert.assertEquals(Math.sqrt(2.0) + 2.0, measure.compute(second, first));
		Assert.assertEquals(3, counter.getCallCount());
	}
	
	private static class CountingDistanceMeasure<T extends Clusterable> implements DistanceMeasure<T> {
		
		private final DistanceMeasure<T> measure;
		
		private int calls;
		
		public CountingDistanceMeasure() {
			super();
			this.measure = DistanceMeasure.euclideanDistance();
		}

		@Override
		public double compute(T first, T second) {
			return measure.compute(first, second) + (calls++);
		}
		
		public int getCallCount() {
			return calls;
		}
		
	}
	
}
