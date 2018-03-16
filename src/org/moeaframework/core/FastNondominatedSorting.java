/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.core;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.comparator.DominanceComparator;

/**
 * Fast non-dominated sorting algorithm for dominance depth ranking. Assigns the
 * {@code rank} and {@code crowdingDistance} attributes to solutions. Solutions
 * of rank 0 belong to the Pareto non-dominated front.  Requires at worst
 * O(MN^2) operations instead of O(MN^3) required by a naive implementation of
 * {@link NondominatedSorting}, but tends to run slower than the naive
 * implementation except on edge cases (e.g., for N solutions there are N
 * fronts).
 * <p>
 * [1] does not discuss how to handle duplicate solutions.  A straightforward
 * interpretation is that duplicate solutions should have the worst crowding
 * distance (and hence are truncated/pruned from the population first).
 * Therefore, duplicate solutions are assigned a crowding distance of 0.
 * <p>
 * References:
 * <ol>
 * <li>Deb et al (2002). "A Fast and Elitist Multiobjective Genetic Algorithm:
 * NSGA-II." IEEE Transactions on Evolutionary Computation. 6(2):182-197.
 * </ol>
 */
public class FastNondominatedSorting extends NondominatedSorting {

	/**
	 * Constructs a fast non-dominated sorting operator using Pareto dominance.
	 */
	public FastNondominatedSorting() {
		super();
	}

	/**
	 * Constructs a fast non-dominated sorting operator using the specified
	 * dominance comparator.
	 * 
	 * @param comparator the dominance comparator
	 */
	public FastNondominatedSorting(DominanceComparator comparator) {
		super(comparator);
	}

	@Override
	public void evaluate(Population population) {
		int N = population.size();
		
		// precompute the dominance relations
		int[][] dominanceChecks = new int[N][N];
		
		for (int i = 0; i < N; i++) {
			Solution si = population.get(i);
			
			for (int j = i+1; j < N; j++) {
				if (i != j) {
					Solution sj = population.get(j);
					
					dominanceChecks[i][j] = comparator.compare(si, sj);
					dominanceChecks[j][i] = -dominanceChecks[i][j];
				}
			}
		}
		
		// compute for each solution s_i the solutions s_j that it dominates
		// and the number of times it is dominated
		int[] dominatedCounts = new int[N];
		List<List<Integer>> dominatesList = new ArrayList<List<Integer>>();
		List<Integer> currentFront = new ArrayList<Integer>();
		
		
		for (int i = 0; i < N; i++) {
			List<Integer> dominates = new ArrayList<Integer>();
			int dominatedCount = 0;
			
			for (int j = 0; j < N; j++) {
				if (i != j) {
					if (dominanceChecks[i][j] < 0) {
						dominates.add(j);
					} else if (dominanceChecks[j][i] < 0) {
						dominatedCount += 1;
					}
				}
			}
			
			if (dominatedCount == 0) {
				currentFront.add(i);
			}
			
			dominatesList.add(dominates);
			dominatedCounts[i] = dominatedCount;
		}
		
		// assign ranks
		int rank = 0;
		
		while (!currentFront.isEmpty()) {
			List<Integer> nextFront = new ArrayList<Integer>();
			Population solutionsInFront = new Population();
			
			for (int i = 0; i < currentFront.size(); i++) {
				Solution solution = population.get(currentFront.get(i));
				solution.setAttribute(RANK_ATTRIBUTE, rank);
				
				// update the dominated counts as compute next front
				for (Integer j : dominatesList.get(currentFront.get(i))) {
					dominatedCounts[j] -= 1;
					
					if (dominatedCounts[j] == 0) {
						nextFront.add(j);
					}
				}
				
				solutionsInFront.add(solution);
			}
			
			updateCrowdingDistance(solutionsInFront);
			
			rank += 1;
			currentFront = nextFront;
		}
	}

}
