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
package org.moeaframework.examples.misc;

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.problem.misc.Srinivas;

/**
 * Example showing how to write a custom optimization algorithm.  This simple "random walker" algorithm will mutate
 * one individual from the population using polynomial mutation (PM) each iteration.
 */
public class RandomWalkerExample {

	public static class RandomWalker extends AbstractEvolutionaryAlgorithm {
		
		public RandomWalker(Problem problem) {
			super(problem,
					Settings.DEFAULT_POPULATION_SIZE,
					new NondominatedSortingPopulation(),
					null, /* no archive */
					new RandomInitialization(problem),
					OperatorFactory.getInstance().getVariation("pm", problem));
		}

		@Override
		protected void iterate() {
			// get the current population
			NondominatedSortingPopulation population = (NondominatedSortingPopulation)getPopulation();
			
			// randomly select a solution from the population
			int index = PRNG.nextInt(population.size());
			Solution parent = population.get(index);
			
			// mutate the selected solution
			Solution offspring = getVariation().evolve(new Solution[] { parent })[0];
			
			// evaluate the objectives/constraints
			evaluate(offspring);
			
			// add the offspring to the population
			population.add(offspring);
			
			// use non-dominated sorting to remove the worst solution
			population.truncate(population.size()-1);
		}

	}
	
	public static void main(String[] args) {
		RandomWalker algorithm = new RandomWalker(new Srinivas());
		algorithm.run(10000);
		algorithm.getResult().display();
	}
	
}
