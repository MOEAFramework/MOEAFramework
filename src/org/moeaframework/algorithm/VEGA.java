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
package org.moeaframework.algorithm;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.ObjectiveComparator;
import org.moeaframework.core.operator.TournamentSelection;

/**
 * Implementation of the Vector Evaluated Genetic Algorithm (VEGA).  VEGA should
 * be avoided in practice, since many modern algorithms outperform it and
 * exhibit better convergence properties, but is included due to its historical
 * significance.  VEGA is considered the earliest MOEA.  It supports M
 * objectives during the selection phase by selecting M different subgroups,
 * each selected based on the i-th objective value, for i=1,...,M.
 * <p>
 * There is one small algorithmic difference between this implementation and
 * [1].  In [1], applying the genetic operators fills the entire population.
 * However, since custom variation operators can be specified, it is possible
 * that the population will not be filled completely.  As a result, this
 * implementation will continue selecting parents until the population is full.
 * <p>
 * References:
 * <ol>
 *   <li>Schaffer, D. (1985).  Multiple Objective Optimization with Vector
 *       Evaluated Genetic Algorithms.  Proceedings of the 1st International
 *       Conference on Genetic Algorithms, pp. 93-100.
 * </ol>
 */
public class VEGA extends AbstractEvolutionaryAlgorithm {
	
	/**
	 * The selection operator.
	 */
	private Selection selection;
	
	/**
	 * The variation operator.
	 */
	private Variation variation;

	/**
	 * Constructs a new VEGA instance.
	 * 
	 * @param problem the problem
	 * @param population the population
	 * @param archive the external archive; or {@code null} if no external
	 *        archive is used
	 * @param initialization the initialization operator
	 * @param variation the variation operator
	 */
	public VEGA(Problem problem, Population population,
			NondominatedPopulation archive, Initialization initialization,
			Variation variation) {
		super(problem, population, archive, initialization);
		this.variation = variation;
		
		selection = new VEGASelection();
	}

	@Override
	protected void iterate() {
		int populationSize = population.size();
		
		// select the parents from the M different subgroups
		Solution[] parents = selection.select(populationSize, population);
		
		// shuffle the parents
		PRNG.shuffle(parents);
		
		// loop until the next generation is filled
		int index = 0;
		boolean filled = false;
		
		population.clear();
		
		while (!filled) {
			Solution[] offspring = variation.evolve(
					select(parents, index, variation.getArity()));
			
			for (int i = 0; i < offspring.length; i++) {
				population.add(offspring[i]);
				
				if (population.size() >= populationSize) {
					filled = true;
					break;
				}
			}
			
			index += variation.getArity() % populationSize;
		}
		
		// evaluate the offspring
		evaluateAll(population);
	}
	
	/**
	 * Returns the subset of parents for the next variation operator.
	 * 
	 * @param parents all parents
	 * @param index the starting index
	 * @param size the size of the subset
	 * @return the subset of parents
	 */
	private Solution[] select(Solution[] parents, int index, int size) {
		Solution[] result = new Solution[size];
		
		for (int i = 0; i < size; i++) {
			result[i] = parents[(index+i) % parents.length];
		}
		
		return result;
	}

	/**
	 * VEGA selection operator that selects parents based on only one of the
	 * objectives.  
	 */
	private class VEGASelection implements Selection {
		
		private Selection[] selectors;
		
		public VEGASelection() {
			super();
			
			selectors = new Selection[problem.getNumberOfObjectives()];
			
			for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
				selectors[i] = new TournamentSelection(
						new ObjectiveComparator(i));
			}
		}

		@Override
		public Solution[] select(int arity, Population population) {
			Solution[] result = new Solution[arity];
			
			for (int i = 0; i < arity; i++) {
				Selection selector = selectors[i % problem.getNumberOfObjectives()];
				result[i] = selector.select(1, population)[0];
			}
			
			return result;
		}
		
	}
	
}
