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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.EpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.selection.TournamentSelection;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;

/**
 * Implementation of NSGA-II, with the ability to attach an optional &epsilon;-dominance archive.
 * <p>
 * References:
 * <ol>
 *   <li>Deb, K. et al.  "A Fast Elitist Multi-Objective Genetic Algorithm: NSGA-II."  IEEE Transactions on
 *       Evolutionary Computation, 6:182-197, 2000.
 *   <li>Kollat, J. B., and Reed, P. M.  "Comparison of Multi-Objective Evolutionary Algorithms for Long-Term
 *       Monitoring Design."  Advances in Water Resources, 29(6):792-807, 2006.
 * </ol>
 */
public class NSGAII extends AbstractEvolutionaryAlgorithm implements EpsilonBoxEvolutionaryAlgorithm {

	/**
	 * The selection operator.  If {@code null}, this algorithm uses binary tournament selection without replacement,
	 * replicating the behavior of the original NSGA-II implementation.
	 */
	private Selection selection;
	
	/**
	 * Constructs the NSGA-II algorithm with default settings.
	 * 
	 * @param problem the problem being solved
	 */
	public NSGAII(Problem problem) {
		this(problem,
				Settings.DEFAULT_POPULATION_SIZE,
				new NondominatedSortingPopulation(),
				null,
				new TournamentSelection(2, new ChainedComparator(new ParetoDominanceComparator(), new CrowdingComparator())),
				OperatorFactory.getInstance().getVariation(problem),
				new RandomInitialization(problem));
	}

	/**
	 * Constructs the NSGA-II algorithm with the specified components.
	 * 
	 * @param problem the problem being solved
	 * @param initialPopulationSize the initial population size
	 * @param population the population used to store solutions
	 * @param archive the archive used to store the result; can be {@code null}
	 * @param selection the selection operator
	 * @param variation the variation operator
	 * @param initialization the initialization method
	 */
	public NSGAII(Problem problem, int initialPopulationSize, NondominatedSortingPopulation population,
			EpsilonBoxDominanceArchive archive, Selection selection, Variation variation,
			Initialization initialization) {
		super(problem, initialPopulationSize, population, archive, initialization, variation);
		this.selection = selection;
	}

	@Override
	public void iterate() {
		NondominatedSortingPopulation population = getPopulation();
		EpsilonBoxDominanceArchive archive = getArchive();
		Variation variation = getVariation();
		Population offspring = new Population();
		int populationSize = population.size();

		if (selection == null) {
			// recreate the original NSGA-II implementation using binary tournament selection without replacement;
			// this version works by maintaining a pool of candidate parents.
			LinkedList<Solution> pool = new LinkedList<Solution>();
			
			DominanceComparator comparator = new ChainedComparator(
					new ParetoDominanceComparator(),
					new CrowdingComparator());
			
			while (offspring.size() < populationSize) {
				// ensure the pool has enough solutions
				while (pool.size() < 2*variation.getArity()) {
					List<Solution> poolAdditions = new ArrayList<Solution>();
					
					for (Solution solution : population) {
						poolAdditions.add(solution);
					}
					
					PRNG.shuffle(poolAdditions);
					pool.addAll(poolAdditions);
				}
				
				// select the parents using a binary tournament
				Solution[] parents = new Solution[variation.getArity()];
				
				for (int i = 0; i < parents.length; i++) {
					parents[i] = TournamentSelection.binaryTournament(
							pool.removeFirst(),
							pool.removeFirst(),
							comparator);
				}
				
				// evolve the children
				offspring.addAll(variation.evolve(parents));
			}
		} else {
			// run NSGA-II using selection with replacement; this version allows
			// using custom selection operators
			while (offspring.size() < populationSize) {
				Solution[] parents = selection.select(variation.getArity(), population);

				offspring.addAll(variation.evolve(parents));
			}
		}

		evaluateAll(offspring);

		if (archive != null) {
			archive.addAll(offspring);
		}

		population.addAll(offspring);
		population.truncate(populationSize);
	}
	
	@Override
	@Property("operator")
	public void setVariation(Variation variation) {
		super.setVariation(variation);
	}
	
	@Override
	@Property("populationSize")
	public void setInitialPopulationSize(int initialPopulationSize) {
		super.setInitialPopulationSize(initialPopulationSize);
	}

	@Override
	public EpsilonBoxDominanceArchive getArchive() {
		return (EpsilonBoxDominanceArchive)super.getArchive();
	}
	
	/**
	 * Sets the archive used by this algorithm.  This value can not be set after initialization.
	 * 
	 * @param archive the archive
	 */
	public void setArchive(EpsilonBoxDominanceArchive archive) {
		super.setArchive(archive);
	}

	@Override
	public NondominatedSortingPopulation getPopulation() {
		return (NondominatedSortingPopulation)super.getPopulation();
	}

	@Override
	public void applyConfiguration(TypedProperties properties) {
		if (!properties.getBoolean("withReplacement", true)) {
			selection = null;
		}
		
		super.applyConfiguration(properties);
	}

	@Override
	public TypedProperties getConfiguration() {
		TypedProperties properties = super.getConfiguration();
		
		if (selection == null) {
			properties.setBoolean("withReplacement", false);
		}
		
		return properties;
	}

}
