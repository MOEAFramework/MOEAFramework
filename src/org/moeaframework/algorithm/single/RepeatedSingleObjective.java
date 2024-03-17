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
package org.moeaframework.algorithm.single;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.weights.RandomGenerator;
import org.moeaframework.util.weights.WeightGenerator;

/**
 * Instantiates and runs several instances of a single objective algorithm.  This is intended to be used with
 * single-objective optimizers that use weighted aggregation of the objectives.  This is based on the Repeated Single
 * Objective (RSO) algorithm by E. J. Hughes [1], where they investigate running many single-objective optimizers
 * (one many) compared to running a single many-objective optimizer (many once).
 * <p>
 * Be mindful of how running multiple instances affects the {@link #step()}.  Each step performs one iteration of each
 * instance.  Thus, given 50 instances of an algorithm with a population size of 100, then 5000 evaluations will occur
 * each step.  In [1], they decrease the population size for the single objective optimizers.
 * <p>
 * References:
 * <ol>
 *   <li>E. J. Hughes.  "Evolutionary many-objective optimization: many once or one many."  2005 IEEE Congress on
 *       Evolutionary Computation, pp. 222-227.
 * </ol>
 */
public class RepeatedSingleObjective extends AbstractAlgorithm {
	
	/**
	 * A list of the instantiated algorithms.
	 */
	private final List<Algorithm> algorithms;
	
	/**
	 * Constructs a new instance of the RSO algorithm using the given single-objective algorithm.  The properties
	 * are passed to each individual algorithm with the addition of a {@code "weights"} parameter, which is
	 * randomly-generated for each instance.
	 * 
	 * @param problem the problem to solve
	 * @param algorithmName the algorithm name
	 * @param properties the algorithm properties
	 * @param instances the number of instances
	 */
	public RepeatedSingleObjective(Problem problem, int instances, String algorithmName, TypedProperties properties) {
		this(problem, instances, (p, w) -> {
			TypedProperties localProperties = new TypedProperties();
			localProperties.addAll(properties);
			localProperties.setDoubleArray("weights", w);
			
			return AlgorithmFactory.getInstance().getAlgorithm(algorithmName, localProperties, p);
		});
	}

	
	/**
	 * Constructs a new instance of the RSO algorithm using randomly-generated weights.
	 * 
	 * @param problem the problem to solve
	 * @param instances the number of single-objective algorithm instances to create using random weights
	 * @param creator function that creates a single-objective algorithm for the given weight vector
	 */
	public RepeatedSingleObjective(Problem problem, int instances, BiFunction<Problem, double[], Algorithm> creator) {
		this(problem, new RandomGenerator(problem.getNumberOfObjectives(), instances), creator);
	}
	
	/**
	 * Constructs a new instance of the RSO algorithm.  The weight generator defines the number of instances.
	 * 
	 * @param problem the problem to solve
	 * @param weightGenerator generates the weight vectors used to configure each single-objective algorithm
	 * @param creator function that creates a single-objective algorithm for the given weight vector
	 */
	public RepeatedSingleObjective(Problem problem, WeightGenerator weightGenerator,
			BiFunction<Problem, double[], Algorithm> creator) {
		super(problem);
		algorithms = new ArrayList<Algorithm>();
		
		for (double[] weight : weightGenerator.generate()) {
			algorithms.add(creator.apply(problem, weight));
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
	
	@Override
	public void saveState(ObjectOutputStream stream) throws IOException {
		super.saveState(stream);
		stream.writeInt(algorithms.size());
		
		for (Algorithm algorithm : algorithms) {
			algorithm.saveState(stream);
		}
	}

	@Override
	public void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		super.loadState(stream);
		int expectedSize = stream.readInt();
		
		if (algorithms.size() != expectedSize) {
			throw new IOException("the number of instances used by RSO differs from the loaded state");
		}
		
		for (Algorithm algorithm : algorithms) {
			algorithm.loadState(stream);
		}
	}

}
