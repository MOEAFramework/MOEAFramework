/* Copyright 2009-2016 David Hadka
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

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.algorithm.ReferenceVectorGuidedPopulation.ReferenceVectorGuidedPopulationState;
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
		
		// generate the offspring
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
	
	@Override
	public Serializable getState() throws NotSerializableException {
		if (!isInitialized()) {
			throw new AlgorithmInitializationException(this, 
					"algorithm not initialized");
		}

		return new RVEAState(getNumberOfEvaluations(), generation,
				getPopulation().getState());
	}

	@Override
	public void setState(Object objState) throws NotSerializableException {
		RVEAState state = (RVEAState)objState;

		initialized = true;
		numberOfEvaluations = state.getNumberOfEvaluations();
		generation = state.getGeneration();
		getPopulation().setState(state.getPopulationState());
	}

	/**
	 * Proxy for serializing and deserializing the state of an
	 * {@code RVEA} instance. This proxy supports saving
	 * the {@code numberOfEvaluations}, {@code generation}, {@code population}
	 * and {@code archive}.
	 */
	private static class RVEAState implements Serializable {

		private static final long serialVersionUID = 5341464818762163296L;

		/**
		 * The number of objective function evaluations.
		 */
		private final int numberOfEvaluations;
		
		/**
		 * The current generation.
		 */
		private final int generation;

		/**
		 * The population stored in a serializable list.
		 */
		private final ReferenceVectorGuidedPopulationState populationState;

		/**
		 * Constructs a proxy to serialize and deserialize the state of an 
		 * {@code RVEA} instance.
		 * 
		 * @param numberOfEvaluations the number of objective function
		 *        evaluations
		 * @param generation the current generation
		 * @param population the population stored in a serializable object
		 */
		public RVEAState(int numberOfEvaluations, int generation,
				ReferenceVectorGuidedPopulationState populationState) {
			super();
			this.numberOfEvaluations = numberOfEvaluations;
			this.generation = generation;
			this.populationState = populationState;
		}

		/**
		 * Returns the number of objective function evaluations.
		 * 
		 * @return the number of objective function evaluations
		 */
		public int getNumberOfEvaluations() {
			return numberOfEvaluations;
		}

		/**
		 * Returns the current generation.
		 * 
		 * @return the current generation
		 */
		public int getGeneration() {
			return generation;
		}

		/**
		 * Returns the population stored in a serializable object.
		 * 
		 * @return the population stored in a serializable object
		 */
		public ReferenceVectorGuidedPopulationState getPopulationState() {
			return populationState;
		}

	}

}
