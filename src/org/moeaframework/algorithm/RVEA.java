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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.initialization.Initialization;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.operator.Variation;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.population.ReferenceVectorGuidedPopulation;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.termination.MaxFunctionEvaluations;
import org.moeaframework.core.termination.TerminationCondition;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.validate.Validate;
import org.moeaframework.util.weights.NormalBoundaryDivisions;

/**
 * Implementation of the Reference Vector Guided Evolutionary Algorithm (RVEA).  This version does not include the
 * reference vector regeneration method proposed by the authors.
 * <p>
 * RVEA is similar in concept to NSGA-III, but replaces NSGA-III's dominance-based selection with an angle-penalized
 * distance function.  Additionally, whereas NSGA-III renormalizes the objectives every iteration, RVEA periodically
 * scales the reference vectors, potentially reducing algorithm overhead.
 * <p>
 * References:
 * <ol>
 *   <li>R. Cheng, Y. Jin, M. Olhofer, and B. Sendhoff.  "A Reference Vector Guided Evolutionary Algorithm for
 *       Many-objective Optimization."  IEEE Transactions on Evolutionary Computation, Issue 99, 2016.
 * </ol>
 */
public class RVEA extends AbstractEvolutionaryAlgorithm {
		
	/**
	 * The current iteration;
	 */
	private int iteration;
	
	/**
	 * The maximum number of iterations for the angle-penalized distance to transition between convergence and
	 * diversity.
	 */
	private int maxIterations;
	
	/**
	 * The frequency, in iterations, that the reference vectors are normalized.
	 */
	private int adaptFrequency;
	
	/**
	 * Constructs a new instance of RVEA with default settings.
	 * 
	 * @param problem the problem being solved
	 */
	public RVEA(Problem problem) {
		this(problem, NormalBoundaryDivisions.forProblem(problem));

	}
	
	/**
	 * Constructs a new instance of RVEA with default settings.
	 * 
	 * @param problem the problem being solved
	 * @param divisions the number of divisions used by the reference vector guided population
	 */
	RVEA(Problem problem, NormalBoundaryDivisions divisions) {
		this(problem,
				divisions.getNumberOfReferencePoints(problem),
				new ReferenceVectorGuidedPopulation(problem.getNumberOfObjectives(), divisions),
				OperatorFactory.getInstance().getVariation(problem),
				new RandomInitialization(problem),
				-1,
				10);
	}

	/**
	 * Constructs a new instance of the RVEA algorithm.
	 * 
	 * @param problem the problem being solved
	 * @param initialPopulationSize the initial population size
	 * @param population the population used to store solutions
	 * @param variation the variation operator
	 * @param initialization the initialization method
	 * @param maxIterations the maximum number of iterations for the angle-penalized distance to transition
	 *        between convergence and diversity, set to {@code -1} to derive the value from {@code maxEvaluations}
	 * @param adaptFrequency the frequency, in iterations, that the reference vectors are normalized
	 */
	public RVEA(Problem problem, int initialPopulationSize, ReferenceVectorGuidedPopulation population,
			Variation variation, Initialization initialization, int maxIterations, int adaptFrequency) {
		super(problem, initialPopulationSize, population, null, initialization, variation);
		this.maxIterations = maxIterations;
		this.adaptFrequency = adaptFrequency;
		
		// catch potential errors
		Validate.that("variation.getArity()", variation.getArity()).isEqualTo(2);
		Validate.that("problem.getNumberOfObjectives()", problem.getNumberOfObjectives()).isGreaterThanOrEqualTo(2);
	}
	
	@Override
	public String getName() {
		return "RVEA";
	}

	@Override
	protected void iterate() {
		ReferenceVectorGuidedPopulation population = getPopulation();
		Variation variation = getVariation();
		Population offspring = new Population();
		int populationSize = population.size();
		
		// update the scaling factor for computing the angle-penalized distance
		if (maxIterations < 0) {
			maxIterations = Settings.DEFAULT_MAX_ITERATIONS;
			System.err.println("maxIterations not configured for RVEA, defaulting to " + maxIterations);
		}
		
		population.setScalingFactor(Math.min(iteration / (double)maxIterations, 1.0));
		
		// create a random permutation of the population indices
		List<Integer> indices = new ArrayList<>();
		
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
		if ((iteration > 0) && (iteration % adaptFrequency == 0)) {
			population.adapt();
		}
		
		iteration++;
	}
	
	/**
	 * Returns the frequency, in iterations, that the reference vectors are normalized.
	 * 
	 * @return the frequency, in iterations
	 */
	public int getAdaptFrequency() {
		return adaptFrequency;
	}
	
	/**
	 * Sets the frequency, in iterations, that the reference vectors are normalized.
	 * 
	 * @param adaptFrequency the frequency, in iterations
	 */
	@Property
	public void setAdaptFrequency(int adaptFrequency) {
		Validate.that("adaptFrequency", adaptFrequency).isGreaterThan(0);
		this.adaptFrequency = adaptFrequency;
	}
	
	/**
	 * Returns the maximum number of iterations for the angle-penalized distance to transition between convergence and
	 * diversity.
	 * 
	 * @return the maximum number of iterations
	 */
	public int getMaxIterations() {
		return maxIterations;
	}
	
	/**
	 * Sets the maximum number of iterations for the angle-penalized distance to transition between convergence and
	 * diversity.  If set to {@code -1}, the max iterations will be derived from {@link #run(int)}.
	 * 
	 * @param maxIterations the maximum number of iterations
	 */
	@Property
	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}
	
	@Override
	public ReferenceVectorGuidedPopulation getPopulation() {
		return (ReferenceVectorGuidedPopulation)super.getPopulation();
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
	public void run(TerminationCondition terminationCondition) {
		if (maxIterations < 0) {
			int maxFunctionEvaluations = MaxFunctionEvaluations.derive(terminationCondition);
			
			if (maxFunctionEvaluations >= 0) {
				maxIterations = maxFunctionEvaluations / getInitialPopulationSize();
			}
		}
		
		super.run(terminationCondition);
	}

	@Override
	public void applyConfiguration(TypedProperties properties) {
		NormalBoundaryDivisions divisions = getPopulation().getDivisions();
		double alpha = getPopulation().getAlpha();
		boolean changed = false;
		
		NormalBoundaryDivisions newDivisions = NormalBoundaryDivisions.tryFromProperties(properties);
		
		if (newDivisions != null) {
			divisions = newDivisions;
			changed = true;
		}
		
		if (properties.contains("alpha")) {
			alpha = properties.getDouble("alpha");
			changed = true;
		}
		
		if (changed) {
			setPopulation(new ReferenceVectorGuidedPopulation(problem.getNumberOfObjectives(), divisions, alpha));
		}
		
		super.applyConfiguration(properties);
	}

	@Override
	public TypedProperties getConfiguration() {
		TypedProperties properties = super.getConfiguration();
		properties.addAll(getPopulation().getDivisions().toProperties());
		properties.setDouble("alpha", getPopulation().getAlpha());
		return properties;
	}
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		super.saveState(stream);
		stream.writeInt(iteration);
		getPopulation().saveState(stream);
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		super.loadState(stream);
		iteration = stream.readInt();
		getPopulation().loadState(stream);
	}

}
