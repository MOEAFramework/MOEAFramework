/* Copyright 2009-2016 David Hadka
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

import java.io.NotSerializableException;
import java.util.Comparator;

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

/**
 * Single-objective genetic algorithm (GA) implementation with elitism.  A
 * single elite individual is retained in each generation.
 * <p>
 * References:
 * <ol>
 *   <li>John Holland.  "Adaptation in Natural and Artificial Systems."  
 *       MIT Press, ISBN: 9780262082136.
 * </ol>
 */
public class GeneticAlgorithm extends AbstractEvolutionaryAlgorithm {
	
	/**
	 * The aggregate objective comparator.
	 */
	private final AggregateObjectiveComparator comparator;

	/**
	 * The selection operator.
	 */
	private final Selection selection;

	/**
	 * The mutation operator.
	 */
	private final Variation variation;
	
	/**
	 * The solution with the best fitness score.
	 */
	private Solution eliteSolution;

	/**
	 * Constructs a new instance of the genetic algorithm (GA).
	 * 
	 * @param problem the problem
	 * @param comparator the aggregate objective comparator
	 * @param initialization the initialization method
	 * @param selection the selection operator
	 * @param variation the variation operator
	 */
	public GeneticAlgorithm(Problem problem,
			AggregateObjectiveComparator comparator,
			Initialization initialization,
			Selection selection,
			Variation variation) {
		super(problem, new Population(), null, initialization);
		this.comparator = comparator;
		this.variation = variation;
		this.selection = selection;
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
		Population offspring = new Population();
		int populationSize = population.size();

		while (offspring.size() < populationSize) {
			Solution[] parents = selection.select(variation.getArity(),
					population);
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
	public void setState(Object objState) throws NotSerializableException {
		super.setState(objState);
		
		eliteSolution = getPopulation().get(0);
		updateEliteSolution();
	}

}
