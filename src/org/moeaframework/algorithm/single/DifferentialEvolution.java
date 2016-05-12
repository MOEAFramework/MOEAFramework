package org.moeaframework.algorithm.single;

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.operator.real.DifferentialEvolutionVariation;
import org.moeaframework.core.operator.real.DifferentialEvolutionSelection;

public class DifferentialEvolution extends AbstractEvolutionaryAlgorithm {
	
	private DominanceComparator comparator;
	
	private DifferentialEvolutionSelection selection;
	
	private DifferentialEvolutionVariation variation;

	public DifferentialEvolution(Problem problem, DominanceComparator comparator,
			Initialization initialization,
			DifferentialEvolutionSelection selection,
			DifferentialEvolutionVariation variation) {
		super(problem, new Population(), null, initialization);
		this.comparator = comparator;
		this.selection = selection;
		this.variation = variation;
	}

	@Override
	protected void iterate() {
		Population population = getPopulation();
		Population children = new Population();

		//generate children
		for (int i = 0; i < population.size(); i++) {
			selection.setCurrentIndex(i);

			Solution[] parents = selection.select(variation.getArity(),
					population);
			children.add(variation.evolve(parents)[0]);
		}
		
		//evaluate children
		evaluateAll(children);
		
		//greedy selection of next population
		for (int i = 0; i < population.size(); i++) {
			if (comparator.compare(children.get(i), population.get(i)) < 0) {
				population.replace(i, children.get(i));
			}
		}
	}

	@Override
	public NondominatedPopulation getResult() {
		NondominatedPopulation result = new NondominatedPopulation(comparator);
		result.addAll(getPopulation());
		return result;
	}
	
}
