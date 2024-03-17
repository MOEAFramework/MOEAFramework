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
package org.moeaframework.algorithm.single;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.operator.real.DifferentialEvolutionVariation;
import org.moeaframework.core.selection.DifferentialEvolutionSelection;
import org.moeaframework.core.variable.RealVariable;

/**
 * Single-objective differential evolution (DE) algorithm.
 * <p>
 * References:
 * <ol>
 *   <li>Rainer Storn and Kenneth Price.  "Differential Evolution - A Simple and Efficient Heuristic for Global
 *       Optimization over Continuous Spaces."  Journal of Global Optimization, 11(4):341-359, 1997.
 * </ol>
 */
public class DifferentialEvolution extends SingleObjectiveEvolutionaryAlgorithm {
	
	/**
	 * The differential evolution selection operator.
	 */
	private final DifferentialEvolutionSelection selection;
	
	/**
	 * Constructs a new single-objective differential evolution algorithm with default settings.
	 * 
	 * @param problem the problem
	 */
	public DifferentialEvolution(Problem problem) {
		this(problem,
				Settings.DEFAULT_POPULATION_SIZE,
				new LinearDominanceComparator(),
				new RandomInitialization(problem),
				new DifferentialEvolutionSelection(),
				new DifferentialEvolutionVariation());
	}

	/**
	 * Constructs a new instance of the single-objective differential evolution (DE) algorithm.
	 * 
	 * @param problem the problem
	 * @param initialPopulationSize the initial population size
	 * @param comparator the aggregate objective comparator
	 * @param initialization the initialization method
	 * @param selection the differential evolution selection operator
	 * @param variation the differential evolution variation operator
	 */
	public DifferentialEvolution(Problem problem, int initialPopulationSize, AggregateObjectiveComparator comparator,
			Initialization initialization, DifferentialEvolutionSelection selection,
			DifferentialEvolutionVariation variation) {
		super(problem, initialPopulationSize, new Population(), null, comparator, initialization, variation);
		
		Validate.problemType(problem, RealVariable.class);
		Validate.notNull("selection", selection);
		
		this.selection = selection;
	}
	
	@Override
	protected void iterate() {
		Population population = getPopulation();
		DifferentialEvolutionVariation variation = getVariation();
		Population children = new Population();

		//generate children
		for (int i = 0; i < population.size(); i++) {
			selection.setCurrentIndex(i);

			Solution[] parents = selection.select(variation.getArity(), population);
			children.add(variation.evolve(parents)[0]);
		}
		
		//evaluate children
		evaluateAll(children);
		
		//greedy selection of next population
		for (int i = 0; i < population.size(); i++) {
			if (((DominanceComparator)comparator).compare(children.get(i), population.get(i)) < 0) {
				population.replace(i, children.get(i));
			}
		}
	}

	@Override
	public NondominatedPopulation getResult() {
		NondominatedPopulation result = new NondominatedPopulation(comparator);
		result.addAll(getPopulation());
		return result;
	}
	
	@Override
	public DifferentialEvolutionVariation getVariation() {
		return (DifferentialEvolutionVariation)super.getVariation();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setVariation(DifferentialEvolutionVariation variation) {
		super.setVariation(variation);
	}
}
