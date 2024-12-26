/* Copyright 2018-2019 Ibrahim DEMIR, 2009-2025 David Hadka
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

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.util.validate.Validate;

/**
 * Single-linkage clustering, which is a hierarchical clustering method that at each step joins the clusters with the
 * closest pair of elements.
 * <p>
 * References:
 * <ol>
 *   <li>https://en.wikipedia.org/wiki/Single-linkage_clustering
 * </ol>
 */
public class SingleLinkageClustering implements Clustering {
	
	private final DistanceMeasure<ClusterableSolution> distanceMeasure;

	/**
	 * Constructs a new single-linkage clustering method.
	 * 
	 * @param distanceMeasure the distance measure used to construct the cluster
	 */
	public SingleLinkageClustering(DistanceMeasure<ClusterableSolution> distanceMeasure) {
		super();
		this.distanceMeasure = distanceMeasure;
	}
	
	@Override
	public List<Cluster> cluster(int size, Iterable<ClusterableSolution> solutions) {
		Validate.that("size", size).isGreaterThanOrEqualTo(1);
		
		List<Cluster> clusters = new ArrayList<>();
		
		for (ClusterableSolution solution : solutions) {
			clusters.add(new Cluster(distanceMeasure, solution));
		}

		while (clusters.size() > size) {
			Cluster minClusterA = null;
			Cluster minClusterB = null;
			double minDistance = Double.POSITIVE_INFINITY;
			
			for (int a = 0; a < clusters.size(); a++) {
				for (int b = a + 1; b < clusters.size(); b++) {
					double distance = clusters.get(a).distanceTo(clusters.get(b));
					
					if (distance < minDistance) {
						minDistance = distance;
						minClusterA = clusters.get(a);
						minClusterB = clusters.get(b);
					}
				}
			}
			
			minClusterA.addAll(minClusterB);
			clusters.remove(minClusterB);
		}
		
		return clusters;
	}

}
