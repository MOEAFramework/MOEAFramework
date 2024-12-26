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
package org.moeaframework.examples.TSP;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.problem.AbstractProblem;

/**
 * The optimization problem definition.  This is a 1 variable, 1 objective optimization problem.  The single
 * variable is a permutation that defines the nodes visited by the salesman.
 */
public class TSPProblem extends AbstractProblem {

	/**
	 * The TSP problem instance.
	 */
	private final TSPInstance instance;
	
	/**
	 * The TSP heuristic for aiding the optimization process.
	 */
	private final TSP2OptHeuristic heuristic;
	
	/**
	 * Constructs a new optimization problem for the given TSP problem instance.
	 * 
	 * @param instance the TSP problem instance
	 */
	public TSPProblem(TSPInstance instance) {
		super(1, 1);
		this.instance = instance;
		
		heuristic = new TSP2OptHeuristic(instance);
	}

	@Override
	public void evaluate(Solution solution) {
		Tour tour = toTour(solution);
		
		// apply the heuristic and save the modified tour
		heuristic.apply(tour);
		fromTour(solution, tour);

		solution.setObjectiveValue(0, tour.distance(instance));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		
		solution.setVariable(0, new Permutation(instance.getDimension()));
		
		return solution;
	}
	
	/**
	 * Converts a MOEA Framework solution to a {@link Tour}.
	 * 
	 * @param solution the MOEA Framework solution
	 * @return the tour defined by the solution
	 */
	public static Tour toTour(Solution solution) {
		int[] permutation = Permutation.getPermutation(solution.getVariable(0));
		
		// increment values since TSP nodes start at 1
		for (int i = 0; i < permutation.length; i++) {
			permutation[i]++;
		}
		
		return Tour.createTour(permutation);
	}
	
	/**
	 * Saves a {@link Tour} into a MOEA Framework solution.
	 * 
	 * @param solution the MOEA Framework solution
	 * @param tour the tour
	 */
	public static void fromTour(Solution solution, Tour tour) {
		int[] permutation = tour.toArray();
		
		// decrement values to get permutation
		for (int i = 0; i < permutation.length; i++) {
			permutation[i]--;
		}
		
		Permutation.setPermutation(solution.getVariable(0), permutation);
	}
	
}