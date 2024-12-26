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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.moeaframework.util.Vector;
import org.moeaframework.util.validate.Validate;

/**
 * Defines a cluster of solutions along with the distance measure used to form the cluster.  Note that by definition a
 * cluster must contain at least one point.
 */
public class Cluster implements Iterable<ClusterableSolution> {
	
	private final DistanceMeasure<ClusterableSolution> distanceMeasure;
	
	private final List<ClusterableSolution> members;
	
	/**
	 * Internal constructor creating a cluster with the given solution.
	 * 
	 * @param distanceMeasure the distance measure used to construct the cluster
	 * @param solution the solution used to initialize this cluster
	 */
	Cluster(DistanceMeasure<ClusterableSolution> distanceMeasure, ClusterableSolution solution) {
		this(distanceMeasure, List.of(solution));
	}
	
	/**
	 * Internal constructor creating a cluster with the given solutions.
	 * 
	 * @param distanceMeasure the distance measure used to construct the cluster
	 * @param solutions the solutions to include in the cluster
	 * @throws IllegalArgumentException if no solutions were provided
	 */
	Cluster(DistanceMeasure<ClusterableSolution> distanceMeasure, ClusterableSolution[] solutions) {
		this(distanceMeasure, List.of(solutions));
	}
	
	/**
	 * Internal constructor creating a cluster with the given solutions.
	 * 
	 * @param distanceMeasure the distance measure used to construct the cluster
	 * @param solutions the solutions to include in the cluster
	 * @throws IllegalArgumentException if no solutions were provided
	 */
	Cluster(DistanceMeasure<ClusterableSolution> distanceMeasure, Iterable<ClusterableSolution> solutions) {
		super();
		this.distanceMeasure = distanceMeasure;
		this.members = new ArrayList<>();
		
		addAll(solutions);
		
		if (size() < 1) {
			Validate.that("solutions", solutions).fails("A cluster must contain at least one solution");
		}
	}
	
	/**
	 * Internal copy constructor.
	 * 
	 * @param cluster the cluster to copy
	 */
	Cluster(Cluster cluster) {
		this(cluster.distanceMeasure, cluster);
	}
	
	/**
	 * Returns the number of points or elements contained in this cluster.
	 * 
	 * @return the size of the cluster
	 */
	public int size() {
		return members.size();
	}

	@Override
	public Iterator<ClusterableSolution> iterator() {
		return members.iterator();
	}
	
	/**
	 * Adds a new clusterable solution to this cluster.
	 * 
	 * @param solution the solution to add
	 */
	public void add(ClusterableSolution solution) {
		members.add(solution);
	}

	/**
	 * Adds a collection of clusterable solutions to this cluster.
	 * 
	 * @param solutions the solutions to add
	 */
	public void addAll(Iterable<ClusterableSolution> solutions) {
		for (ClusterableSolution solution : solutions) {
			add(solution);
		}
	}
	
	/**
	 * Returns the clusterable solution at the given index.
	 * 
	 * @param index the index
	 * @return the clusterable solution at the given index
	 * @throws IndexOutOfBoundsException if the index is out of bounds
	 */
	public ClusterableSolution get(int index) {
		return members.get(index);
	}
	
	/**
	 * Merges this cluster with another.  Note that this produces a new cluster, leaving the two original clusters
	 * unchanged.
	 * 
	 * @param otherCluster the other cluster to merge with
	 * @return the merged cluster
	 */
	public Cluster merge(Cluster otherCluster) {
		Cluster result = new Cluster(this);
		result.addAll(otherCluster);
		return result;
	}
	
	/**
	 * Returns the center, or centroid, of this cluster.  While there are various definitions of a "centroid", here we
	 * compute the average value in each dimension.  This minimizes the sum-of-squared distances from the centroid to
	 * each point in the cluster.
	 * 
	 * @return the center point
	 */
	public double[] getCenter() {
		double[] result = Vector.of(members.get(0).getPoint().length, 0.0);
			
		for (ClusterableSolution solution : this) {
			result = Vector.add(result, solution.getPoint());
		}
			
		return Vector.divide(result, members.size());
	}
	
	/**
	 * Returns the distance between two clusters, which is defined as the minimum value found when computing all
	 * pairwise distances between individual points.
	 * 
	 * @param cluster the other cluster
	 * @return the minimum distance
	 */
	public double distanceTo(Cluster cluster) {
		double minDistance = Double.MAX_VALUE;
		
		for (ClusterableSolution solution : this) {
			for (ClusterableSolution otherSolution : cluster) {
				double distance = distanceMeasure.compute(solution, otherSolution);
				
				if (distance < minDistance) {
					minDistance = distance;
				}
			}
		}
		
		return minDistance;
	}
	
	/**
	 * Returns the solution defined to represent this cluster.  This implementation returns the solution with the
	 * smallest crowding distance, measured as the sum of distances to all other solutions within the cluster.
	 * 
	 * @return the representative solution in this cluster
	 */
	public ClusterableSolution getRepresentativeMember() {
		ClusterableSolution minSolution = null;
		double minDistance = Double.POSITIVE_INFINITY;
		
		for (int i = 0; i < members.size(); i++) {
			double distance = 0.0;
			
			for (int j = 0; j < members.size(); j++) {
				if (i == j) {
					continue;
				}
				
				distance += distanceMeasure.compute(members.get(i), members.get(j));
			}
			
			if (distance < minDistance) {
				minDistance = distance;
				minSolution = members.get(i);
			}
		}
		
		return minSolution;
	}

}
