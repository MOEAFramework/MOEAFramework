/* Copyright 2018-2019 Ibrahim DEMIR
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
package org.moeaframework.algorithm.sa;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

/**
 * @preview
 */
class SingleLinkageClustering {
	
	private final NondominatedPopulation population;
	
	private final List<Cluster> clusters;

	public SingleLinkageClustering(NondominatedPopulation population) {
		super();
		this.population = population;
		clusters = new ArrayList<Cluster>();
		
		for (Solution solution : population) {
			clusters.add(new Cluster(solution));
		}
	}
	
	public NondominatedPopulation cluster(int n) {
		if (n >= population.size() || population.size() <= 1) {
			return population;
		}
		
		for (int i = population.size(); i > n; i--) {
			Cluster minClusterA = null;
			Cluster minClusterB = null;
			double minDistance = Double.MAX_VALUE;
			
			for (int a = 0; a < clusters.size(); a++) {
				for (int b = a+1; b < clusters.size(); b++) {
					double distance = clusters.get(a).distance(clusters.get(b));
					
					if (distance < minDistance) {
						minDistance = distance;
						minClusterA = clusters.get(a);
						minClusterB = clusters.get(b);
					}
				}
			}
			
			merge(minClusterA,minClusterB);
		}
		
		NondominatedPopulation reducedPopulation = new NondominatedPopulation();
		
		for(Cluster cluster : clusters) {
			reducedPopulation.add(cluster.getRepresentativeMember());
		}
		
		return reducedPopulation;
	}
	
	private void merge(Cluster clusterA, Cluster clusterB) {
		clusterA.addAllElements(clusterB);
		clusters.remove(clusterB);
	}
	
	private class Cluster {
		
		private List<Solution> elements = new ArrayList<Solution>();
		
		public Cluster(Solution element) {
			super();
			this.elements.add(element);
		}

		public List<Solution> getElements(){
			return elements;
		}

		public void addAllElements(Cluster cluster) {
			this.elements.addAll(cluster.getElements());
		}
		
		/**		
		 * @param reference
		 *            the reference cluster
		 * @return the distance between the clusters by selecting closest solutions in both clusters
		 */
		public double distance(Cluster reference) {
			double minDistance = Double.MAX_VALUE;
			
			for (Solution solution : this.elements) {
				for (Solution referenceSolution : reference.elements) {
					double distance = solution.distanceTo(referenceSolution);
					
					if (distance < minDistance) {
						minDistance=distance;
					}
				}
			}
			
			return minDistance;
		}
		
		/**
		 * @return the solution which has the shortest crowding distance
		 */
		public Solution getRepresentativeMember() {
			Solution minSolution = null;
			double minDistance = Double.MAX_VALUE;
			
			for (int i = 0; i < elements.size(); i++) {
				double distance = 0;
				
				for (int j = 0; j < elements.size(); j++) {
					if (i == j) {
						continue;
					}
					
					distance += elements.get(i).distanceTo(elements.get(j));
				}
				
				if (distance < minDistance) {
					minDistance = distance;
					minSolution = elements.get(i); 
				}
			}
			
			return minSolution;
		}
	}
}
