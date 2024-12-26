/* Copyright 2009-2025 David Hadka
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

import java.util.List;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.mock.MockSolution;

public class ClusterTest {
	
	@Test(expected = IllegalArgumentException.class)
	public void testAtLeastOneSolution() {
		new Cluster(DistanceMeasure.euclideanDistance(), List.of());
	}
	
	@Test
	public void test() {
		Solution s1 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution s2 = MockSolution.of().withObjectives(0.0, -1.0);
		Solution s3 = MockSolution.of().withObjectives(0.0, 0.0);
		
		Cluster cluster = new Cluster(DistanceMeasure.euclideanDistance(),
				List.of(s1, s2, s3).stream().map(ClusterableSolution::withObjectives).toList());
		
		Assert.assertEquals(3, cluster.size());
		Assert.assertSame(s1, cluster.get(0).getSolution());
		Assert.assertSame(s2, cluster.get(1).getSolution());
		Assert.assertSame(s3, cluster.get(2).getSolution());
		Assert.assertArrayEquals(new double[] { 0.0, 0.0 }, cluster.getCenter(), TestThresholds.HIGH_PRECISION);
		Assert.assertSame(s3, cluster.getRepresentativeMember().getSolution());
	}
	
	@Test
	public void testDistanceTo() {
		Solution s1 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution s2 = MockSolution.of().withObjectives(0.0, -1.0);
		Solution s3 = MockSolution.of().withObjectives(1.0, 0.0);
		Solution s4 = MockSolution.of().withObjectives(-1.0, 0.0);
		
		Cluster c1 = new Cluster(DistanceMeasure.euclideanDistance(),
				List.of(s1, s2).stream().map(ClusterableSolution::withObjectives).toList());
		Cluster c2 = new Cluster(DistanceMeasure.euclideanDistance(),
				List.of(s3, s4).stream().map(ClusterableSolution::withObjectives).toList());
		
		Assert.assertEquals(0.0, c1.distanceTo(c1));
		Assert.assertEquals(Math.sqrt(2.0), c1.distanceTo(c2));
		Assert.assertEquals(Math.sqrt(2.0), c2.distanceTo(c1));
	}
	
	@Test
	public void testCopyConstructor() {
		Solution s1 = MockSolution.of().withObjectives(0.0, 1.0);
		Solution s2 = MockSolution.of().withObjectives(0.0, -1.0);
		
		Cluster c1 = new Cluster(DistanceMeasure.euclideanDistance(),
				List.of(s1).stream().map(ClusterableSolution::withObjectives).toList());
		
		Cluster c2 = new Cluster(c1);
		
		Assert.assertEquals(1, c2.size());
		Assert.assertSame(s1, c2.get(0).getSolution());
		
		c2.add(ClusterableSolution.withObjectives(s2));
		
		Assert.assertEquals(2, c2.size());
		Assert.assertEquals(1, c1.size());
	}
	
}
