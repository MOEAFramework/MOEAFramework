package org.moeaframework.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

/**
 * Implementation of the Reference Vector Guided Evolutionary Algorithm (RVEA).
 * This version does not include the reference vector regeneration method
 * proposed by the authors.
 * <p>
 * RVEA is similar in concept to NSGA-III, but replaces NSGA-III's
 * dominance-based selection with an angle-penalized distance function.
 * Additionally, whereas NSGA-III renormalizes the objectives every iteration,
 * RVEA periodically scales the reference vectors, potentially reducing
 * algorithm overhead.
 * <p>
 * References:
 * <ol>
 *   <li>R. Cheng, Y. Jin, M. Olhofer, and B. Sendhoff.  "A Reference Vector
 *       Guided Evolutionary Algorithm for Many-objective Optimization."
 *       IEEE Transactions on Evolutionary Computation, Issue 99, 2016.
 * </ol>
 */
public class RVEA extends AbstractEvolutionaryAlgorithm {
	
	/**
	 * The variation operator.
	 */
	private final Variation variation;
	
	/**
	 * The current generation;
	 */
	private int generation;
	
	/**
	 * The maximum number of generations for the angle-penalized distance to
	 * transition between convergence and diversity.
	 */
	private int maxGeneration = 1000;
	
	/**
	 * The frequency, in generations, that the reference vectors are normalized.
	 */
	private int adaptFrequency = maxGeneration / 10;

	/**
	 * Constructs a new instance of the RVEA algorithm.
	 * 
	 * @param problem the problem being solved
	 * @param population the population used to store solutions
	 * @param variation the variation operator
	 * @param initialization the initialization method
	 * @param maxGeneration the maximum number of generations for the
	 *        angle-penalized distance to transition between convergence and
	 *        diversity
	 * @param adaptFrequency the frequency, in generations, that the reference
	 *        vectors are normalized.
	 */
	public RVEA(Problem problem, ReferenceVectorGuidedPopulation population,
			Variation variation, Initialization initialization,
			int maxGeneration, int adaptFrequency) {
		super(problem, population, null, initialization);
		this.variation = variation;
		
		// catch potential errors
		if (variation.getArity() != 2) {
			throw new FrameworkException(
					"RVEA only supports operators requiring 2 parents");
		}
	}

	@Override
	protected void iterate() {
		ReferenceVectorGuidedPopulation population = getPopulation();
		Population offspring = new Population();
		int populationSize = population.size();
		
		// update the scaling factor for computing the angle-penalized distance
		population.setScalingFactor(Math.min(generation / (double)maxGeneration,
				1.0));
		
		// create a random permutation of the population indices
		List<Integer> indices = new ArrayList<Integer>();
		
		for (int i = 0; i < populationSize; i++) {
			indices.add(i);
		}
		
		PRNG.shuffle(indices);
		
		// add an extra entry so the number of indices is even
		if (indices.size() % 2 == 1) {
			indices.add(indices.get(0));
		}
		
		// genreate the offspring
		for (int i = 0; i < indices.size(); i += 2) {
			Solution[] parents = new Solution[] { population.get(indices.get(i)), population.get(indices.get(i+1)) };
			Solution[] children = variation.evolve(parents);

			offspring.addAll(children);
		}

		evaluateAll(offspring);

		// select the survivors
		population.addAll(offspring);
		population.truncate();
		
		// periodically normalize the reference vectors
		if ((generation > 0) && (generation % adaptFrequency == 0)) {
			population.adapt();
		}
		
		generation++;
	}
	
	@Override
	public ReferenceVectorGuidedPopulation getPopulation() {
		return (ReferenceVectorGuidedPopulation)super.getPopulation();
	}

}
