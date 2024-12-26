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
package org.moeaframework.examples.algorithm;

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.population.NondominatedSortingPopulation;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.problem.Problem;

/**
 * The "random walker" algorithm, which uses the polynomial mutation (pm) operator to produce one random offspring
 * each iteration.
 */
public class RandomWalker extends AbstractEvolutionaryAlgorithm {
	
	public RandomWalker(Problem problem) {
		super(problem,
				Settings.DEFAULT_POPULATION_SIZE,
				new NondominatedSortingPopulation(),
				null, /* no archive */
				new RandomInitialization(problem),
				OperatorFactory.getInstance().getVariation("pm", problem));
	}
	
	@Override
	public String getName() {
		return "RandomWalker";
	}

	@Override
	protected void iterate() {
		// get the current population
		NondominatedSortingPopulation population = (NondominatedSortingPopulation)getPopulation();
		
		// randomly select a solution from the population
		int index = PRNG.nextInt(population.size());
		Solution parent = population.get(index);
		
		// mutate the selected solution
		Solution[] offspring = getVariation().evolve(new Solution[] { parent });
		
		// evaluate the offspring, add to population, and truncate the worst solution
		evaluateAll(offspring);
		population.addAll(offspring);
		population.truncate(population.size()-1);
	}

}