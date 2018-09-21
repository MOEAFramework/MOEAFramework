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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.EpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

/**
 * Implementation of the &epsilon;-MOEA algorithm.  The &epsilon;-MOEA is a
 * steady-state algorithm, meaning only one individual in the population is
 * evolved per step, and uses an &epsilon;-dominance archive to maintain a
 * well-spread set of Pareto-optimal solutions.
 * <p>
 * References:
 * <ol>
 *   <li>Deb et al. "A Fast Multi-Objective Evolutionary Algorithm for Finding
 *   Well-Spread Pareto-Optimal Solutions." KanGAL Report No 2003002. Feb 2003.
 * </ol>
 */
public class EpsilonMOEA extends AbstractEvolutionaryAlgorithm implements
		EpsilonBoxEvolutionaryAlgorithm {

	/**
	 * The dominance comparator used for updating the population.
	 */
	private final DominanceComparator dominanceComparator;

	/**
	 * The selection operator.
	 */
	private final Selection selection;

	/**
	 * The variation operator.
	 */
	private final Variation variation;

	/**
	 * Constructs the &epsilon;-MOEA algorithm with the specified components.
	 * 
	 * @param problem the problem being solved
	 * @param population the population used to store solutions
	 * @param archive the archive used to store the result
	 * @param selection the selection operator
	 * @param variation the variation operator
	 * @param initialization the initialization method
	 */
	public EpsilonMOEA(Problem problem, Population population,
			EpsilonBoxDominanceArchive archive, Selection selection,
			Variation variation, Initialization initialization) {
		this(problem, population, archive, selection, variation,
				initialization, new ParetoDominanceComparator());
	}

	/**
	 * Constructs the &epsilon;-MOEA algorithm with the specified components.
	 * 
	 * @param problem the problem being solved
	 * @param population the population used to store solutions
	 * @param archive the archive used to store the result
	 * @param selection the selection operator
	 * @param variation the variation operator
	 * @param initialization the initialization method
	 * @param dominanceComparator the dominance comparator used by the
	 *        {@link #addToPopulation} method
	 */
	public EpsilonMOEA(Problem problem, Population population,
			EpsilonBoxDominanceArchive archive, Selection selection,
			Variation variation, Initialization initialization,
			DominanceComparator dominanceComparator) {
		super(problem, population, archive, initialization);
		this.variation = variation;
		this.selection = selection;
		this.dominanceComparator = dominanceComparator;
	}

	@Override
	public void iterate() {
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
	 * Adds the new solution to the population if is non-dominated with the
	 * current population, removing either a randomly-selected dominated
	 * solution or a non-dominated solution.
	 * 
	 * @param newSolution the new solution being added to the population
	 */
	protected void addToPopulation(Solution newSolution) {
		List<Integer> dominates = new ArrayList<Integer>();
		boolean dominated = false;

		for (int i = 0; i < population.size(); i++) {
			int flag = dominanceComparator.compare(newSolution, 
			        population.get(i));

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

}
