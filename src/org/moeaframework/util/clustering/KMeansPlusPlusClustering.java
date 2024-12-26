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

import org.apache.commons.lang3.stream.Streams;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.moeaframework.util.validate.Validate;

/**
 * A wrapper for the Apache Commons Math K-means++ algorithm.  The "++" term specifies a procedure for initializing
 * the cluster centers before performing the k-means algorithm, which avoids theoretical shortcomings of the k-means
 * algorithm that result in suboptimal clustering.
 * <p>
 * References:
 * <ol>
 *   <li>https://en.wikipedia.org/wiki/K-means%2B%2B
 *   <li>https://en.wikipedia.org/wiki/K-means_clustering
 * </ol>
 */
public class KMeansPlusPlusClustering implements Clustering {
	
	private final ApacheDistanceMeasure<ClusterableSolution> distanceMeasure;

	/**
	 * Constructs a new K-means++ clustering method.
	 * 
	 * @param distanceMeasure the distance measure used to construct the cluster
	 */
	public KMeansPlusPlusClustering(ApacheDistanceMeasure<ClusterableSolution> distanceMeasure) {
		super();
		this.distanceMeasure = distanceMeasure;
	}
	
	@Override
	public List<Cluster> cluster(int size, Iterable<ClusterableSolution> solutions) {
		Validate.that("size", size).isGreaterThanOrEqualTo(1);

		return new KMeansPlusPlusClusterer<ClusterableSolution>(size, -1, distanceMeasure)
				.cluster(Streams.of(solutions).toList())
				.stream()
				.map(x -> new Cluster(distanceMeasure, x.getPoints()))
				.toList();
	}

}
