package org.moeaframework.algorithm;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.real.DifferentialEvolutionSelection;
import org.moeaframework.core.operator.real.DifferentialEvolutionVariation;

/**
 * Implementation of the Multiple Single Objective Pareto Sampling (MSOPS)
 * algorithm.  This implementation only supports differential evolution.
 * <p>
 * References:
 * <ol>
 *   <li>E. J. Hughes.  "Multiple Single Objective Pareto Sampling."  2003
 *       Congress on Evolutionary Computation, pp. 2678-2684.
 *   <li>Matlab source code available from
 *       <a href="http://code.evanhughes.org/">http://code.evanhughes.org/</a>.
 * </ol>
 * 
 * @see MSOPSRankedPopulation
 */
public class MSOPS extends AbstractEvolutionaryAlgorithm {
	
	/**
	 * The selection operator.
	 */
	private final DifferentialEvolutionSelection selection;

	/**
	 * The variation operator.
	 */
	private final DifferentialEvolutionVariation variation;

	/**
	 * Constructs a new instance of the MSOPS algorithm.
	 * 
	 * @param problem the problem
	 * @param population the population supporting MSOPS ranking
	 * @param selection the differential evolution selection operator
	 * @param variation the differential evolution variation operator
	 * @param initialization the initialization method
	 */
	public MSOPS(Problem problem, MSOPSRankedPopulation population,
			DifferentialEvolutionSelection selection,
			DifferentialEvolutionVariation variation,
			Initialization initialization) {
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
		int neighborhoodSize = (int)Math.ceil(populationSize/2.0);

		for (int i = 0; i < populationSize; i++) {
			// findNearest(i, ...) always puts the i-th solution at index 0
			selection.setCurrentIndex(0);
			
			Solution[] parents = selection.select(variation.getArity(),
					population.findNearest(i, neighborhoodSize));
			Solution[] children = variation.evolve(parents);

			offspring.addAll(children);
		}

		evaluateAll(offspring);
		
		population.addAll(offspring);
		population.truncate(populationSize);
	}

}
