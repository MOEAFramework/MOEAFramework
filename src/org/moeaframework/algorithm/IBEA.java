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
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.FitnessComparator;
import org.moeaframework.core.fitness.IndicatorFitnessEvaluator;
import org.moeaframework.core.operator.TournamentSelection;

/**
 * Implementation of the Indicator-Based Evolutionary Algorithm (IBEA).  Instead
 * of using Pareto dominance to evaluate the quality of solutions, IBEA uses
 * an indicator function (typically hypervolume but other indicator functions
 * can be specified). 
 * <p>
 * References:
 * <ol>
 *   <li>Zitzler, E. and S. Kunzli (2004).  Indicator-Based Selection in
 *       Multiobjective Search.  Parallel Problem Solving from Nature
 *       (PPSN VIII), pp. 832-842.
 * </ol>
 */
public class IBEA extends AbstractEvolutionaryAlgorithm {
	
	/**
	 * The indicator fitness evaluator to use (e.g., hypervolume or
	 * additive-epsilon indicator).
	 */
	private IndicatorFitnessEvaluator fitnessEvaluator;
	
	/**
	 * The fitness comparator for comparing solutions based on their fitness.
	 */
	private FitnessComparator fitnessComparator;
	
	/**
	 * The selection operator.
	 */
	private Selection selection;
	
	/**
	 * The variation operator.
	 */
	private Variation variation;

	/**
	 * Constructs a new IBEA instance.
	 * 
	 * @param problem the problem
	 * @param archive the external archive; or {@code null} if no external
	 *        archive is used
	 * @param initialization the initialization operator
	 * @param variation the variation operator
	 * @param fitnessEvaluator the indicator fitness evaluator to use (e.g.,
	 *        hypervolume additive-epsilon indicator)
	 */
	public IBEA(Problem problem, NondominatedPopulation archive,
			Initialization initialization, Variation variation,
			IndicatorFitnessEvaluator fitnessEvaluator) {
		super(problem, new Population(), archive, initialization);
		this.variation = variation;
		this.fitnessEvaluator = fitnessEvaluator;
		
		fitnessComparator = new FitnessComparator(
				fitnessEvaluator.areLargerValuesPreferred());
		selection = new TournamentSelection(fitnessComparator);
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		
		fitnessEvaluator.evaluate(population);
	}

	@Override
	protected void iterate() {
		Population offspring = new Population();
		int populationSize = population.size();
		
		while (offspring.size() < populationSize) {
			Solution[] parents = selection.select(variation.getArity(),
					population);
			Solution[] children = variation.evolve(parents);

			offspring.addAll(children);
		}
		
		evaluateAll(offspring);
		population.addAll(offspring);
		fitnessEvaluator.evaluate(population);
		
		while (population.size() > populationSize) {
			int worstIndex = findWorstIndex();
			fitnessEvaluator.removeAndUpdate(population, worstIndex);
		}
	}
	
	/**
	 * Returns the index of the solution with the worst fitness value.
	 * 
	 * @return the index of the solution with the worst fitness value
	 */
	private int findWorstIndex() {
		int worstIndex = 0;
		
		for (int i = 1; i < population.size(); i++) {
			if (fitnessComparator.compare(population.get(worstIndex),
					population.get(i)) == -1) {
				worstIndex = i;
			}
		}
		
		return worstIndex;
	}

}