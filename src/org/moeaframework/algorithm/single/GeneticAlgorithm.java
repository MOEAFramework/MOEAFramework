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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Comparator;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.selection.TournamentSelection;
import org.moeaframework.core.spi.OperatorFactory;

/**
 * Single-objective genetic algorithm (GA) implementation with elitism.  A single elite individual is retained in each
 * generation.
 * <p>
 * References:
 * <ol>
 *   <li>John Holland.  "Adaptation in Natural and Artificial Systems."  MIT Press, ISBN: 9780262082136.
 * </ol>
 */
public class GeneticAlgorithm extends SingleObjectiveEvolutionaryAlgorithm {
	
	/**
	 * The selection operator.
	 */
	private TournamentSelection selection;

	/**
	 * The solution with the best fitness score.
	 */
	private Solution eliteSolution;
	
	/**
	 * Constructs a new instance of the genetic algorithm (GA) with default settings.
	 * 
	 * @param problem the problem
	 */
	public GeneticAlgorithm(Problem problem) {
		this(problem,
				Settings.DEFAULT_POPULATION_SIZE,
				new LinearDominanceComparator(),
				new RandomInitialization(problem),
				OperatorFactory.getInstance().getVariation(problem));
	}
	
	/**
	 * Constructs a new instance of the genetic algorithm (GA).
	 * 
	 * @param problem the problem
	 * @param initialPopulationSize the initial population size
	 * @param comparator the aggregate objective comparator
	 * @param initialization the initialization method
	 * @param variation the variation operator
	 */
	public GeneticAlgorithm(Problem problem, int initialPopulationSize, AggregateObjectiveComparator comparator,
			Initialization initialization, Variation variation) {
		super(problem, initialPopulationSize, new Population(), null, comparator, initialization, variation);
	}

	@Override
	protected void initialize() {
		super.initialize();

		eliteSolution = getPopulation().get(0);
		updateEliteSolution();
	}

	@Override
	public void iterate() {
		Population population = getPopulation();
		Variation variation = getVariation();
		Population offspring = new Population();
		int populationSize = population.size();

		while (offspring.size() < populationSize) {
			Solution[] parents = selection.select(variation.getArity(), population);
			Solution[] children = variation.evolve(parents);

			offspring.addAll(children);
		}

		evaluateAll(offspring);

		population.clear();
		population.add(eliteSolution);
		population.addAll(offspring);
		population.truncate(populationSize, comparator);
		
		updateEliteSolution();
	}
	
	/**
	 * Update the elite solution.
	 */
	public void updateEliteSolution() {
		for (Solution solution : getPopulation()) {
			if (((Comparator<Solution>)comparator).compare(eliteSolution, solution) > 0) {
				eliteSolution = solution;
			}
		}
	}
	
	@Override
	public NondominatedPopulation getResult() {
		NondominatedPopulation result = new NondominatedPopulation(comparator);
		
		if (eliteSolution != null) {
			result.add(eliteSolution);
		}
		
		return result;
	}
	
	@Override
	@Property("operator")
	public void setVariation(Variation variation) {
		super.setVariation(variation);
	}
	
	@Override
	public void setComparator(AggregateObjectiveComparator comparator) {
		super.setComparator(comparator);
		
		selection = new TournamentSelection(
				selection != null ? selection.getSize() : 2,
				comparator);
	}
	
	/**
	 * Returns the tournament selection operator.  This method is primarily for testing and should remain private to
	 * prevent callers from modifying the selection operator (to ensure the comparator is kept consistent).
	 * 
	 * @return the tournament selection operator
	 */
	TournamentSelection getSelection() {
		return selection;
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		super.loadState(stream);
		
		eliteSolution = getPopulation().get(0);
		updateEliteSolution();
	}

}
