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
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.operator.real.DifferentialEvolutionVariation;
import org.moeaframework.core.selection.DifferentialEvolutionSelection;
import org.moeaframework.core.variable.RealVariable;

/**
 * Implementation of the Generalized Differential Evolution (GDE3) algorithm.
 * <p>
 * References:
 * <ol>
 *   <li>Kukkonen and Lampinen (2005). "GDE3: The Third Evolution Step of Generalized Differential Evolution."
 *       KanGAL Report Number 2005013.
 * </ol>
 */
public class GDE3 extends AbstractEvolutionaryAlgorithm {

	/**
	 * The dominance comparator used to determine if offspring survive until the non-dominated sorting step.
	 */
	private final DominanceComparator comparator;

	/**
	 * The selection operator.
	 */
	private final DifferentialEvolutionSelection selection;
	
	/**
	 * Constructs the GDE3 algorithm with default settings.
	 * 
	 * @param problem the problem being solved
	 */
	public GDE3(Problem problem) {
		this(problem,
				Settings.DEFAULT_POPULATION_SIZE,
				new NondominatedSortingPopulation(),
				new DifferentialEvolutionSelection(),
				new DifferentialEvolutionVariation(),
				new RandomInitialization(problem));
	}
	
	/**
	 * Constructs the GDE3 algorithm with the specified components.
	 * 
	 * @param problem the problem being solved
	 * @param initialPopulationSize the initial population size
	 * @param population the population used to store solutions
	 * @param selection the selection operator
	 * @param variation the variation operator
	 * @param initialization the initialization method
	 */
	public GDE3(Problem problem, int initialPopulationSize, NondominatedSortingPopulation population,
			DifferentialEvolutionSelection selection, DifferentialEvolutionVariation variation,
			Initialization initialization) {
		this(problem, initialPopulationSize, population, population.getComparator(), selection, variation,
				initialization);
	}

	/**
	 * Constructs the GDE3 algorithm with the specified components.
	 * 
	 * @param problem the problem being solved
	 * @param initialPopulationSize the initial population size
	 * @param population the population used to store solutions
	 * @param comparator the dominance comparator used to determine if offspring survive until the non-dominated
	 *        sorting step
	 * @param selection the selection operator
	 * @param variation the variation operator
	 * @param initialization the initialization method
	 */
	public GDE3(Problem problem, int initialPopulationSize, NondominatedSortingPopulation population,
			DominanceComparator comparator, DifferentialEvolutionSelection selection,
			DifferentialEvolutionVariation variation, Initialization initialization) {
		super(problem, initialPopulationSize, population, null, initialization, variation);
		
		Validate.problemType(problem, RealVariable.class);
		Validate.notNull("comparator", comparator);
		Validate.notNull("selection", selection);
		
		this.comparator = comparator;
		this.selection = selection;
	}

	@Override
	public void iterate() {
		NondominatedSortingPopulation population = getPopulation();
		DifferentialEvolutionVariation variation = getVariation();
		Population children = new Population();
		int populationSize = population.size();

		//generate children
		for (int i = 0; i < populationSize; i++) {
			selection.setCurrentIndex(i);

			Solution[] parents = selection.select(variation.getArity(), population);
			children.add(variation.evolve(parents)[0]);
		}
		
		//evaluate children
		evaluateAll(children);
		
		//determine composition of next population
		Population offspring = new Population();
		
		for (int i = 0; i < populationSize; i++) {
			int result = comparator.compare(children.get(i), population.get(i));
			
			if (result < 0) {
				offspring.add(children.get(i));
			} else if (result > 0) {
				offspring.add(population.get(i));
			} else {
				offspring.add(children.get(i));
				offspring.add(population.get(i));
			}
		}

		population.clear();
		population.addAll(offspring);
		population.prune(populationSize);
	}

	@Override
	public NondominatedSortingPopulation getPopulation() {
		return (NondominatedSortingPopulation)super.getPopulation();
	}
	
	@Override
	public DifferentialEvolutionVariation getVariation() {
		return (DifferentialEvolutionVariation)super.getVariation();
	}
	
	/**
	 * Replaces the differential evolution variation operator to be used by this algorithm.
	 * 
	 * @param variation the differential evolution variation operator
	 */
	@Property("operator")
	public void setVariation(DifferentialEvolutionVariation variation) {
		super.setVariation(variation);
	}
	
	@Override
	@Property("populationSize")
	public void setInitialPopulationSize(int initialPopulationSize) {
		super.setInitialPopulationSize(initialPopulationSize);
	}

}
