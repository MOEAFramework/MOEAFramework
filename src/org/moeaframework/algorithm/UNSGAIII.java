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
package org.moeaframework.algorithm;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.RankComparator;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.selection.TournamentSelection;
import org.moeaframework.util.weights.NormalBoundaryDivisions;

import static org.moeaframework.algorithm.ReferencePointNondominatedSortingPopulation.getNiche;
import static org.moeaframework.algorithm.ReferencePointNondominatedSortingPopulation.getNicheDistance;

/**
 * Implementation of the "unified" NSGA-III, or U-NSGA-III, which improves selection pressure by replacing the random
 * selection of NSGA-III with tournament selection.
 * 
 * References:
 * <ol>
 *   <li>H. Seada and K. Deb. "A Unified Evolutionary Optimization Procedure for Single, Multiple, and Many Objectives."
 *       IEEE Transactions on Evolutionary Computation, 20(3):358–369, June 2016.
 *   <li>H. Saeda and K. Deb. "U-NSGA-III: A Univied Evolutionary Algorithm for Single, Multiple, and Many-Objective
 *       Optimization."  COIN Report Number 2014022.
 * </ol>
 */
public class UNSGAIII extends NSGAIII {
	
	/**
	 * Creates a new U-NSGA-III instance with default settings.
	 * 
	 * @param problem the problem to solve
	 */
	public UNSGAIII(Problem problem) {
		this(problem, NormalBoundaryDivisions.forProblem(problem));
	}
	
	/**
	 * Creates a new U-NSGA-III instance with the given number of reference point divisions.
	 * 
	 * @param problem the problem to solve
	 * @param divisions the number of divisions for generating reference points
	 */
	public UNSGAIII(Problem problem, NormalBoundaryDivisions divisions) {
		this(problem,
				getInitialPopulationSize(problem, divisions),
				new ReferencePointNondominatedSortingPopulation(problem.getNumberOfObjectives(), divisions),
				new TournamentSelection(2, new UnifiedDominanceComparator()),
				getDefaultVariation(problem),
				new RandomInitialization(problem));
	}
	
	/**
	 * Constructs a new U-NSGA-III instance with the specified components.
	 * 
	 * @param problem the problem being solved
	 * @param initialPopulationSize the initial population size
	 * @param population the reference point population used to store solutions
	 * @param selection the selection operator
	 * @param variation the variation operator
	 * @param initialization the initialization method
	 */
	public UNSGAIII(Problem problem, int initialPopulationSize, ReferencePointNondominatedSortingPopulation population,
			Selection selection, Variation variation, Initialization initialization) {
		super(problem,
				initialPopulationSize,
				population,
				selection,
				variation,
				initialization);
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		getPopulation().updateNiches();
	}
	
	/**
	 * The unified dominance comparator used by U-NSGA-III.  This uses the niche attributes stored by the
	 * reference-point non-dominated sorting population.  The "niche" of the solution identifies the closest reference
	 * point, and the "niche distance" measures the perpendicular distance from the solution to the reference point.
	 * <ol>
	 *   <li>If one or both parents are infeasible
	 *     <ol>
	 *       <li>Select the parent with a lower constraint violation
	 *       <li>Otherwise, randomly pick one of the parents
	 *     </ol>
	 *   <li>If the solutions belong to the same niche
	 *     <ol>
	 *       <li>If the solutions are the same rank, select the parent with the lower "niche distance"
	 *       <li>Otherwise, pick the solution with the lower rank
	 *     </ol>
	 *   <li>Otherwise, randomly pick one of the parents
	 * </ol>
	 */
	static class UnifiedDominanceComparator implements DominanceComparator {
		
		private final AggregateConstraintComparator constraintComparator = new AggregateConstraintComparator();
		
		private final RankComparator rankComparator = new RankComparator();

		@Override
		public int compare(Solution solution1, Solution solution2) {
			if (solution1.violatesConstraints() || solution2.violatesConstraints()) {
				int cmp = constraintComparator.compare(solution1, solution2);
				
				if (cmp == 0) {
					return PRNG.nextBoolean() ? -1 : 1;
				}

				return cmp;
			}
			
			if (getNiche(solution1) == getNiche(solution2)) {
				int cmp = rankComparator.compare(solution1, solution2);
				
				if (cmp == 0) {
					return Double.compare(getNicheDistance(solution1), getNicheDistance(solution2));
				}
				
				return cmp;
			}
			
			return PRNG.nextBoolean() ? -1 : 1;
		}

	}

}
