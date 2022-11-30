/* Copyright 2009-2022 David Hadka
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

import java.util.List;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.real.DifferentialEvolutionSelection;
import org.moeaframework.core.operator.real.DifferentialEvolutionVariation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.Vector;
import org.moeaframework.util.weights.RandomGenerator;

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
	 * Constructs a new MSOPS instance with default settings.
	 * 
	 * @param problem the problem being solved
	 */
	public MSOPS(Problem problem) {
		this(problem,
				new MSOPSRankedPopulation(generateWeights(problem, Settings.DEFAULT_POPULATION_SIZE / 2)),
				new DifferentialEvolutionSelection(),
				new DifferentialEvolutionVariation(),
				new RandomInitialization(problem, Settings.DEFAULT_POPULATION_SIZE));
	}

	/**
	 * Constructs a new instance of the MSOPS algorithm.
	 * 
	 * @param problem the problem being solved
	 * @param population the population supporting MSOPS ranking
	 * @param selection the differential evolution selection operator
	 * @param variation the differential evolution variation operator
	 * @param initialization the initialization method
	 */
	public MSOPS(Problem problem, MSOPSRankedPopulation population, DifferentialEvolutionSelection selection,
			DifferentialEvolutionVariation variation, Initialization initialization) {
		super(problem, population, null, initialization, variation);
		this.selection = selection;
		
		problem.assertType(RealVariable.class);
	}
	
	/**
	 * Generates randomly-distributed, normalized weights.
	 * 
	 * @param problem the problem
	 * @param numberOfWeights the number of weights
	 * @return the normalized weights
	 */
	static final List<double[]> generateWeights(Problem problem, int numberOfWeights) {
		List<double[]> weights = new RandomGenerator(problem.getNumberOfObjectives(), numberOfWeights).generate();
		
		// normalize weights so their magnitude is 1
		for (int i = 0; i < weights.size(); i++) {
			weights.set(i, Vector.normalize(weights.get(i)));
		}
		
		return weights;
	}
	
	@Override
	public DifferentialEvolutionVariation getVariation() {
		return (DifferentialEvolutionVariation)super.getVariation();
	}
	
	public void setVariation(DifferentialEvolutionVariation variation) {
		super.setVariation(variation);
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
			
			Solution[] parents = selection.select(variation.getArity(), population.findNearest(i, neighborhoodSize));
			Solution[] children = variation.evolve(parents);

			offspring.addAll(children);
		}

		evaluateAll(offspring);
		
		population.addAll(offspring);
		population.truncate(populationSize);
	}

}
