package org.moeaframework.algorithm.single;

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

public class GeneticAlgorithm extends AbstractEvolutionaryAlgorithm {
	
	private final SingleObjectiveComparator comparator;

	private final Selection selection;

	private final Variation variation;

	public GeneticAlgorithm(Problem problem,
			SingleObjectiveComparator comparator,
			Initialization initialization,
			Selection selection,
			Variation variation) {
		super(problem, new Population(), null, initialization);
		this.comparator = comparator;
		this.variation = variation;
		this.selection = selection;
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
