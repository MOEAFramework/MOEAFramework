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
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.moeaframework.analysis.DefaultEpsilons;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.EpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.selection.TournamentSelection;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;

/**
 * Implementation of the &epsilon;-MOEA algorithm.  The &epsilon;-MOEA is a steady-state algorithm, meaning only one
 * individual in the population is evolved per step, and uses an &epsilon;-dominance archive to maintain a well-spread
 * set of Pareto-optimal solutions.
 * <p>
 * References:
 * <ol>
 *   <li>Deb et al. "A Fast Multi-Objective Evolutionary Algorithm for Finding Well-Spread Pareto-Optimal Solutions."
 *       KanGAL Report No 2003002. Feb 2003.
 * </ol>
 */
public class EpsilonMOEA extends AbstractEvolutionaryAlgorithm implements EpsilonBoxEvolutionaryAlgorithm {

	/**
	 * The dominance comparator used for updating the population.
	 */
	private final DominanceComparator dominanceComparator;

	/**
	 * The selection operator.
	 */
	private final Selection selection;
	
	/**
	 * Constructs the &epsilon;-MOEA algorithm with default settings.
	 * 
	 * @param problem the problem
	 */
	public EpsilonMOEA(Problem problem) {
		this(problem,
				Settings.DEFAULT_POPULATION_SIZE,
				new Population(),
				new EpsilonBoxDominanceArchive(DefaultEpsilons.getInstance().getEpsilons(problem)),
				new TournamentSelection(2),
				OperatorFactory.getInstance().getVariation(problem),
				new RandomInitialization(problem));
	}

	/**
	 * Constructs the &epsilon;-MOEA algorithm with the specified components.
	 * 
	 * @param problem the problem being solved
	 * @param initialPopulationSize the initial population size
	 * @param population the population used to store solutions
	 * @param archive the archive used to store the result
	 * @param selection the selection operator
	 * @param variation the variation operator
	 * @param initialization the initialization method
	 */
	public EpsilonMOEA(Problem problem, int initialPopulationSize, Population population,
			EpsilonBoxDominanceArchive archive, Selection selection, Variation variation,
			Initialization initialization) {
		this(problem, initialPopulationSize, population, archive, selection, variation, initialization,
				new ParetoDominanceComparator());
	}

	/**
	 * Constructs the &epsilon;-MOEA algorithm with the specified components.
	 * 
	 * @param problem the problem being solved
	 * @param initialPopulationSize the initial population size
	 * @param population the population used to store solutions
	 * @param archive the archive used to store the result
	 * @param selection the selection operator
	 * @param variation the variation operator
	 * @param initialization the initialization method
	 * @param dominanceComparator the dominance comparator used by the {@link #addToPopulation} method
	 */
	public EpsilonMOEA(Problem problem, int initialPopulationSize, Population population,
			EpsilonBoxDominanceArchive archive, Selection selection, Variation variation,
			Initialization initialization, DominanceComparator dominanceComparator) {
		super(problem, initialPopulationSize, population, archive, initialization, variation);
		
		Validate.notNull("selection", selection);
		Validate.notNull("dominanceComparator", dominanceComparator);
		
		this.selection = selection;
		this.dominanceComparator = dominanceComparator;
	}

	@Override
	public void iterate() {
		Population population = getPopulation();
		EpsilonBoxDominanceArchive archive = getArchive();
		Variation variation = getVariation();
		Solution[] parents = null;
		
		if (archive.size() <= 1) {
			parents = selection.select(variation.getArity(), population);
		} else {
			parents = ArrayUtils.add(
					selection.select(variation.getArity() - 1, population),
					archive.get(PRNG.nextInt(archive.size())));
		}
		
		PRNG.shuffle(parents);

		Solution[] children = variation.evolve(parents);

		for (Solution child : children) {
			evaluate(child);
			addToPopulation(child);
			archive.add(child);
		}
	}

	/**
	 * Adds the new solution to the population if is non-dominated with the current population, removing either a
	 * randomly-selected dominated solution or a non-dominated solution.
	 * 
	 * @param newSolution the new solution being added to the population
	 */
	protected void addToPopulation(Solution newSolution) {
		Population population = getPopulation();
		List<Integer> dominates = new ArrayList<Integer>();
		boolean dominated = false;

		for (int i = 0; i < population.size(); i++) {
			int flag = dominanceComparator.compare(newSolution, population.get(i));

			if (flag < 0) {
				dominates.add(i);
			} else if (flag > 0) {
				dominated = true;
			}
		}

		if (!dominates.isEmpty()) {
			population.remove(dominates.get(PRNG.nextInt(dominates.size())));
			population.add(newSolution);
		} else if (!dominated) {
			population.remove(PRNG.nextInt(population.size()));
			population.add(newSolution);
		}
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
		Validate.notNull("archive", archive);
		super.setArchive(archive);
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
	public void applyConfiguration(TypedProperties properties) {
		if (properties.contains("epsilon")) {
			setArchive(new EpsilonBoxDominanceArchive(properties.getDoubleArray("epsilon")));
		}
		
		super.applyConfiguration(properties);
	}

	@Override
	public TypedProperties getConfiguration() {
		TypedProperties properties = super.getConfiguration();
		properties.setDoubleArray("epsilon", getArchive().getComparator().getEpsilons().toArray());
		return properties;
	}

}
