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
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.Population;

/**
 * Interface for a clustering algorithm.
 * <p>
 * Please note that this interface expects the clustering algorithm to construct a fixed number of clusters.  Consider
 * using one of Apache Commons Math's {@link org.apache.commons.math3.ml.clustering.Clusterer} implementations directly
 * if this is too restrictive.
 */
public interface Clustering {
	
	/**
	 * Construct clusters using the points defined by the clusterable solutions.
	 * 
	 * @param size the number of clusters to construct
	 * @param solutions the clusterable solutions
	 * @return the clusters
	 */
	List<Cluster> cluster(int size, Iterable<ClusterableSolution> solutions);
	
	/**
	 * Construct clusters using the points defined by the clusterable solutions.
	 * 
	 * @param size the number of clusters to construct
	 * @param solutions the clusterable solutions
	 * @return the clusters
	 */
	public default List<Cluster> cluster(int size, ClusterableSolution[] solutions) {
		return cluster(size, List.of(solutions));
	}
	
	/**
	 * Construct clusters based on the objective values of the solutions.
	 * 
	 * @param size the number of clusters to construct
	 * @param solutions the solutions
	 * @return the clusters
	 */
	public default List<Cluster> clusterObjectives(int size, Solution[] solutions) {
		return clusterObjectives(size, List.of(solutions));
	}
	
	/**
	 * Construct clusters based on the objective values of the solutions.
	 * 
	 * @param size the number of clusters to construct
	 * @param solutions the solutions
	 * @return the clusters
	 */
	public default List<Cluster> clusterObjectives(int size, Iterable<Solution> solutions) {
		return cluster(size, Streams.of(solutions).map(ClusterableSolution::withObjectives).toList());
	}
	
	/**
	 * Construct clusters based on the decision variables of the solutions.  See
	 * {@link ClusterableSolution#withVariables(Solution)} for limitations of this method.
	 * 
	 * @param size the number of clusters to construct
	 * @param solutions the solutions
	 * @return the clusters
	 */
	public default List<Cluster> clusterVariables(int size, Solution[] solutions) {
		return clusterVariables(size, List.of(solutions));
	}
	
	/**
	 * Construct clusters based on the decision variables of the solutions.  See
	 * {@link ClusterableSolution#withVariables(Solution)} for limitations of this method.
	 * 
	 * @param size the number of clusters to construct
	 * @param solutions the solutions
	 * @return the clusters
	 */
	public default List<Cluster> clusterVariables(int size, Iterable<Solution> solutions) {
		return cluster(size, Streams.of(solutions).map(ClusterableSolution::withVariables).toList());
	}
	
	/**
	 * Truncates a population to the given size by forming clusters based on the objective values and selecting the
	 * representative member from each cluster.
	 * 
	 * @param size the resulting size of the population
	 * @param population the population to truncate
	 */
	public default void truncate(int size, Population population) {
		List<Cluster> clusters = clusterObjectives(size, population);
		
		population.clear();
		
		for (Cluster cluster : clusters) {
			population.add(cluster.getRepresentativeMember().getSolution());
		}
	}
	
	/**
	 * Constructs the single-linkage clustering algorithm using Euclidean distances.
	 * 
	 * @return the clustering algorithm
	 */
	public static Clustering singleLinkage() {
		return new SingleLinkageClustering(DistanceMeasure.euclideanDistance());
	}
	
	/**
	 * Constructs the k-means++ clustering algorithm using Euclidean distances.
	 * 
	 * @return the clustering algorithm
	 */
	public static Clustering kMeansPlusPlus() {
		return new KMeansPlusPlusClustering(DistanceMeasure.euclideanDistance());
	}

}
