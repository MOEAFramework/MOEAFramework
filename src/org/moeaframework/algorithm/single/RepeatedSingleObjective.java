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
package org.moeaframework.algorithm.single;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.weights.RandomGenerator;

/**
 * Instantiates and runs several instances of a single objective algorithm.
 * This is intended to be used with single-objective optimizers that use
 * weighted aggregation of the objectives.  This is based on the Repeated Single
 * Objective (RSO) algorithm by E. J. Hughes [1], where he investigates running
 * many single-objective optimizers (one many) compared to running a single
 * many-objective optimizer (many once).
 * <p>
 * Be mindful of how running multiple instances affects the {@link #step()}.
 * Each step performs one iteration of each instance.  Thus, given 50 instances
 * of an algorithm with a population size of 100, then 5000 evaluations will
 * occur each step.  In [1], they decrease the population size for the single
 * objective optimizers.
 * <p>
 * References:
 * <ol>
 *   <li>E. J. Hughes.  "Evolutionary many-objective optimization: many once or
 *       one many."  2005 IEEE Congress on Evolutionary Computation,
 *       pp. 222-227.
 * </ol>
 */
public class RepeatedSingleObjective extends AbstractAlgorithm {
	
	/**
	 * The name of the algorithm.
	 */
	private final String algorithmName;
	
	/**
	 * Any additional algorithm properties.
	 */
	private final Properties properties;
	
	/**
	 * A list of the instantiated algorithms.
	 */
	private final List<Algorithm> algorithms;

	/**
	 * Constructs a new instance of the Many-Once algorithm, which runs many
	 * instances of a single-objective optimizer with varying weights.
	 * 
	 * @param problem the problem
	 * @param algorithmName the algorithm name
	 * @param properties the algorithm properties
	 * @param instances the number of instances
	 */
	public RepeatedSingleObjective(Problem problem, String algorithmName, Properties properties, int instances) {
		super(problem);
		this.algorithmName = algorithmName;
		this.properties = properties;
		
		// setup the algorithm instances		
		List<double[]> weights = new RandomGenerator(
				problem.getNumberOfObjectives(), instances).generate();
		algorithms = new ArrayList<Algorithm>();
		
		for (double[] weight : weights) {
			algorithms.add(createInstance(weight));
		}
	}

	@Override
	public int getNumberOfEvaluations() {
		int evaluations = 0;
		
		for (Algorithm algorithm : algorithms) {
			evaluations += algorithm.getNumberOfEvaluations();
		}
		
		return evaluations;
	}

	@Override
	public NondominatedPopulation getResult() {
		NondominatedPopulation result = new NondominatedPopulation();
		
		for (Algorithm algorithm : algorithms) {
			result.addAll(algorithm.getResult());
		}
		
		return result;
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		
		// call iterate to force each algorithm to initialize
		iterate();
	}

	@Override
	protected void iterate() {
		for (Algorithm algorithm : algorithms) {
			algorithm.step();
		}
	}
	
	/**
	 * Constructs a new instance of the algorithm using the given weights.
	 * This assumes the weights are passed using the {@code "weights"} property.
	 * 
	 * @param weights the weights
	 * @return the new algorithm instance
	 */
	protected Algorithm createInstance(double[] weights) {
		TypedProperties typedProperties = new TypedProperties(
				new Properties(properties));
		
		typedProperties.setDoubleArray("weights", weights);
		
		return AlgorithmFactory.getInstance().getAlgorithm(
				algorithmName, typedProperties.getProperties(), problem);
	}

}
