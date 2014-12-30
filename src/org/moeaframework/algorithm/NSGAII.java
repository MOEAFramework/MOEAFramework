/* Copyright 2009-2015 David Hadka
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

import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.EpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

/**
 * Implementation of NSGA-II, with the ability to attach an optional 
 * &epsilon;-dominance archive.
 * <p>
 * References:
 * <ol>
 *   <li>Deb, K. et al.  "A Fast Elitist Multi-Objective Genetic Algorithm:
 *       NSGA-II."  IEEE Transactions on Evolutionary Computation, 6:182-197, 
 *       2000.
 *   <li>Kollat, J. B., and Reed, P. M.  "Comparison of Multi-Objective 
 *       Evolutionary Algorithms for Long-Term Monitoring Design."  Advances in
 *       Water Resources, 29(6):792-807, 2006.
 * </ol>
 */
public class NSGAII extends AbstractEvolutionaryAlgorithm implements
		EpsilonBoxEvolutionaryAlgorithm {

	/**
	 * The selection operator.
	 */
	private final Selection selection;

	/**
	 * The variation operator.
	 */
	private final Variation variation;

	/**
	 * Constructs the NSGA-II algorithm with the specified components.
	 * 
	 * @param problem the problem being solved
	 * @param population the population used to store solutions
	 * @param archive the archive used to store the result; can be {@code null}
	 * @param selection the selection operator
	 * @param variation the variation operator
	 * @param initialization the initialization method
	 */
	public NSGAII(Problem problem, NondominatedSortingPopulation population,
			EpsilonBoxDominanceArchive archive, Selection selection,
			Variation variation, Initialization initialization) {
		super(problem, population, archive, initialization);
		this.variation = variation;
		this.selection = selection;
	}

	@Override
	public void iterate() {
		NondominatedSortingPopulation population = getPopulation();
		EpsilonBoxDominanceArchive archive = getArchive();
		Population offspring = new Population();
		int populationSize = population.size();

		while (offspring.size() < populationSize) {
			Solution[] parents = selection.select(variation.getArity(),
					population);
			Solution[] children = variation.evolve(parents);

			offspring.addAll(children);
		}

		evaluateAll(offspring);

		if (archive != null) {
			archive.addAll(offspring);
		}

		population.addAll(offspring);
		population.truncate(populationSize);
	}

	@Override
	public EpsilonBoxDominanceArchive getArchive() {
		return (EpsilonBoxDominanceArchive)super.getArchive();
	}

	@Override
	public NondominatedSortingPopulation getPopulation() {
		return (NondominatedSortingPopulation)super.getPopulation();
	}

}
