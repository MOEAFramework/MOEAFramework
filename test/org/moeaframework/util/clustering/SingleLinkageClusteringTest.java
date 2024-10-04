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

import java.util.List;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.mock.MockSolution;

public class SingleLinkageClusteringTest {
	
	private final Solution s1 = MockSolution.of().withObjectives(-1.0, 0.5);
	private final Solution s2 = MockSolution.of().withObjectives(-1.0, -0.5);
	private final Solution s3 = MockSolution.of().withObjectives(1.0, 0.5);
	private final Solution s4 = MockSolution.of().withObjectives(1.0, -0.5);
	
	private final List<ClusterableSolution> solutions =
			List.of(s1, s2, s3, s4).stream().map(ClusterableSolution::withObjectives).toList();
	
	@Test(expected = IllegalArgumentException.class)
	public void testNoCluster() {
		Clustering.singleLinkage().cluster(0, solutions);
	}
	
	@Test
	public void testSingleCluster() {
		List<Cluster> clusters = Clustering.singleLinkage().cluster(1, solutions);

		Assert.assertEquals(1, clusters.size());
		Assert.assertArrayEquals(new double[] { 0.0, 0.0 }, clusters.get(0).getCenter(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testTwoClusters() {
		List<Cluster> clusters = Clustering.singleLinkage().cluster(2, solutions);

		Assert.assertEquals(2, clusters.size());
		Assert.assertArrayEquals(new double[] { -1.0, 0.0 }, clusters.get(0).getCenter(), TestThresholds.HIGH_PRECISION);
		Assert.assertArrayEquals(new double[] { 1.0, 0.0 }, clusters.get(1).getCenter(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testNClusters() {
		List<Cluster> clusters = Clustering.singleLinkage().cluster(10, solutions);
		Assert.assertEquals(4, clusters.size());
	}
	
	@Test
	public void testTruncate() {
		Population population = new Population(List.of(s1, s2, s3, s4));
		
		Clustering.singleLinkage().truncate(2, population);

		Assert.assertEquals(2, population.size());
		Assert.assertTrue(population.containsAll(List.of(s1, s3)));
	}
	
}
