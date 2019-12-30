/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.core.operator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.Clusterable;
import org.apache.commons.math3.stat.clustering.KMeansPlusPlusClusterer;
import org.junit.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.util.Vector;

/**
 * Provides test methods for checking if the offspring form clusters around each
 * parent.
 */
public abstract class ParentCentricVariationTest extends
		DistributionVariationTest {

	@Override
	protected void check(Solution[] parents, Solution[] offspring) {
		List<ClusterablePoint> points = new ArrayList<ClusterablePoint>();

		for (Solution solution : offspring) {
			points.add(new ClusterablePoint(EncodingUtils.getReal(solution)));
		}

		KMeansPlusPlusClusterer<ClusterablePoint> clusterer = 
				new KMeansPlusPlusClusterer<ClusterablePoint>(new Random());

		List<Cluster<ClusterablePoint>> clusters = clusterer.cluster(points,
				parents.length, 100);

		for (Solution solution : parents) {
			boolean match = false;

			for (int i = 0; i < clusters.size(); i++) {
				boolean allEqual = true;

				double[] centroid = clusters.get(i).getCenter().getPoint();
				double[] parent = EncodingUtils.getReal(solution);

				for (int j = 0; j < parent.length; j++) {
					if (Math.abs(parent[j] - centroid[j]) > 
							TestThresholds.VARIATION_EPS) {
						allEqual = false;
					}
				}

				if (allEqual) {
					match = true;
				}
			}

			Assert.assertTrue(match);
		}
	}

	protected static class ClusterablePoint implements
			Clusterable<ClusterablePoint> {

		private final double[] point;

		public ClusterablePoint(double[] point) {
			this.point = point;
		}

		@Override
		public ClusterablePoint centroidOf(Collection<ClusterablePoint> points) {
			double[] average = new double[point.length];

			for (ClusterablePoint point : points) {
				average = Vector.add(average, point.point);
			}

			return new ClusterablePoint(Vector.divide(average, points.size()));
		}

		@Override
		public double distanceFrom(ClusterablePoint point) {
			double sum = 0.0;

			for (int i = 0; i < this.point.length; i++) {
				sum += Math.pow(this.point[i] - point.point[i], 2.0);
			}

			return Math.sqrt(sum);
		}

		public double[] getPoint() {
			return point;
		}

	}

}
