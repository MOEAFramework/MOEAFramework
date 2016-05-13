package org.moeaframework.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.util.weights.RandomGenerator;

public class MSOPS extends AbstractEvolutionaryAlgorithm {
	
	/**
	 * The selection operator.
	 */
	private final Selection selection;

	/**
	 * The variation operator.
	 */
	private final Variation variation;

	public MSOPS(Problem problem, MSOPSRankedPopulation population, Selection selection,
			Variation variation, Initialization initialization) {
		super(problem, population, null, initialization);
		this.variation = variation;
		this.selection = selection;
	}
	
	@Override
	public MSOPSRankedPopulation getPopulation() {
		return (MSOPSRankedPopulation)super.getPopulation();
	}

	@Override
	protected void iterate() {
		MSOPSRankedPopulation population = getPopulation();
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
		population.truncate(populationSize);
	}

}
