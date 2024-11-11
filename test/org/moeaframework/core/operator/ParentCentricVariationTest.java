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
package org.moeaframework.core.operator;

import java.util.List;

import org.junit.Ignore;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.util.clustering.Cluster;
import org.moeaframework.util.clustering.Clustering;

/**
 * Provides test methods for checking if the offspring form clusters around each parent.
 */
@Ignore("Abstract test class")
public abstract class ParentCentricVariationTest<T extends Variation> extends DistributionVariationTest<T> {

	@Override
	protected void checkDistribution(Solution[] parents, Solution[] offspring) {
		List<Cluster> clusters = Clustering.kMeansPlusPlus().clusterVariables(parents.length, offspring);
		
		for (Solution solution : parents) {
			boolean match = false;

			for (int i = 0; i < clusters.size(); i++) {
				boolean allEqual = true;

				double[] centroid = clusters.get(i).getCenter();
				double[] parent = EncodingUtils.getReal(solution);

				for (int j = 0; j < parent.length; j++) {
					if (Math.abs(parent[j] - centroid[j]) > TestThresholds.LOW_PRECISION) {
						allEqual = false;
					}
				}

				if (allEqual) {
					match = true;
				}
			}

			Assert.assertTrue("Failed to find cluster near solution", match);
		}
	}

}
