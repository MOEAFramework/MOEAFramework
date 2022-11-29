/* Copyright 2009-2022 David Hadka
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

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.variable.RealVariable;

/**
 * Single-objective (mu + lambda) evolution strategy (ES) algorithm.  In this
 * implementation, mu and lambda are both equal to the initial population size.
 * For example, with an initial population of size 1, this mimics the classic
 * (1 + 1)-ES algorithm.  Can only be used with mutation operators with a single
 * parent. 
 * <p>
 * References:
 * <ol>
 *   <li>Ingo Rechenberg.  "Evolutionsstrategie: Optimierung technischer
 *       Systeme nach Prinzipien der biologischen Evolution."  Ph.D. thesis,
 *       Fromman-Holzboog, 1971.
 * </ol>
 */
public class EvolutionStrategy extends AbstractEvolutionaryAlgorithm {
	
	/**
	 * The aggregate objective comparator.
	 */
	private final AggregateObjectiveComparator comparator;
	
	public EvolutionStrategy(Problem problem) {
		this(problem,
				new LinearDominanceComparator(),
				new RandomInitialization(problem, Settings.DEFAULT_POPULATION_SIZE),
				new SelfAdaptiveNormalVariation());
	}

	/**
	 * Constructs a new instance of the evolution strategy (ES) algorithm.
	 * 
	 * @param problem the problem
	 * @param comparator the aggregate objective comparator
	 * @param initialization the initialization method
	 * @param mutation the mutation operator
	 */
	public EvolutionStrategy(Problem problem, AggregateObjectiveComparator comparator, Initialization initialization,
			Mutation mutation) {
		super(problem, new Population(), null, initialization, mutation);
		this.comparator = comparator;
		
		problem.assertType(RealVariable.class);
	}

	@Override
	public void iterate() {
		Population population = getPopulation();
		Population offspring = new Population();
		int populationSize = population.size();
		
		for (int i = 0; i < population.size(); i++) {
			Solution[] parents = new Solution[] { population.get(i) };
			Solution[] children = variation.evolve(parents);

			offspring.addAll(children);
		}

		evaluateAll(offspring);

		population.addAll(offspring);
		population.truncate(populationSize, comparator);
	}
	
	@Override
	public NondominatedPopulation getResult() {
		NondominatedPopulation result = new NondominatedPopulation(comparator);
		result.addAll(getPopulation());
		return result;
	}

}
