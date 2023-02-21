/* Copyright 2009-2023 David Hadka
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
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.junit.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

/**
 * Provides test methods for checking if the offspring form clusters around each
 * parent.
 */
public abstract class ParentCentricVariationTest extends
		DistributionVariationTest {

	@Override
	protected void check(Solution[] parents, Solution[] offspring) {
		List<DoublePoint> points = new ArrayList<DoublePoint>();

		for (Solution solution : offspring) {
			points.add(new DoublePoint(EncodingUtils.getReal(solution)));
		}

		KMeansPlusPlusClusterer<DoublePoint> clusterer = 
				new KMeansPlusPlusClusterer<DoublePoint>(parents.length, 100);

		List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(points);

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

}
