package org.moeaframework.algorithm.single;

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

public class EvolutionaryStrategy extends AbstractEvolutionaryAlgorithm {
	
	private final SingleObjectiveComparator comparator;

	private final Variation variation;

	public EvolutionaryStrategy(Problem problem,
			SingleObjectiveComparator comparator,
			Initialization initialization,
			Variation variation) {
		super(problem, new Population(), null, initialization);
		this.comparator = comparator;
		this.variation = variation;
		
		if (variation.getArity() != 1) {
			throw new FrameworkException("EvolutionaryStrategy only supports variation operators with 1 parent");
		}
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
