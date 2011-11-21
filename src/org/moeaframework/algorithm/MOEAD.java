/* Copyright 2009-2011 David Hadka
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math.util.MathUtils;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.util.ArrayMath;

/**
 * Implementation of MOEA/D, the multiobjective evolutionary algorithm with
 * decomposition.
 * <p>
 * References:
 * <ol>
 * <li>Li, H. and Zhang, Q. "Multiobjective Optimization problems with
 * Complicated Pareto Sets, MOEA/D and NSGA-II." IEEE Transactions on
 * Evolutionary Computation, 13(2):284-302, 2009.
 * </ol>
 */
public class MOEAD extends AbstractAlgorithm {

	/**
	 * Represents an individual (population slot) in the MOEA/D algorithm.
	 */
	private static class Individual {

		/**
		 * The current solution occupying this individual.
		 */
		private Solution solution;

		/**
		 * The Chebyshev weights for this individual.
		 */
		private double[] weights;

		/**
		 * The neighborhood of this individual.
		 */
		private List<Individual> neighbors;

		/**
		 * The utility of this individual.
		 */
		private double utility;

		/**
		 * The cached fitness of the solution currently occupying this
		 * individual.
		 */
		private double fitness;

		/**
		 * Constructs an individual with the specified Chebyshev weights.
		 * 
		 * @param weights the Chebyshev weights for this individual
		 */
		public Individual(double[] weights) {
			this.weights = weights;

			neighbors = new ArrayList<Individual>();
			utility = 1.0;
		}

		/**
		 * Returns the current solution occupying this individual.
		 * 
		 * @return the current solution occupying this individual
		 */
		public Solution getSolution() {
			return solution;
		}

		/**
		 * Sets the current solution occupying this individual.
		 * 
		 * @param solution the new solution occupying this individual
		 */
		public void setSolution(Solution solution) {
			this.solution = solution;
		}

		/**
		 * Returns the Chebyshev weights for this individual.
		 * 
		 * @return the Chebyshev weights for this individual
		 */
		public double[] getWeights() {
			return weights;
		}

		/**
		 * Returns the neighborhood of this individual.
		 * 
		 * @return the neighborhood of this individual
		 */
		public List<Individual> getNeighbors() {
			return neighbors;
		}

		/**
		 * Adds a neighboring individual to the neighborhood of this individual.
		 * 
		 * @param neighbor the individual to be added to the neighborhood
		 */
		public void addNeighbor(Individual neighbor) {
			neighbors.add(neighbor);
		}

		/**
		 * Returns the utility of this individual.
		 * 
		 * @return the utility of this individual
		 */
		public double getUtility() {
			return utility;
		}

		/**
		 * Sets the utility of this individual.
		 * 
		 * @param utility the new utility of this individual
		 */
		public void setUtility(double utility) {
			this.utility = utility;
		}

		/**
		 * Returns the cached fitness of the solution currently occupying this
		 * individual.
		 * 
		 * @return the cached fitness of the solution currently occupying this
		 *         individual
		 */
		public double getFitness() {
			return fitness;
		}

		/**
		 * Sets the cached fitness of the solution currently occupying this
		 * individual.
		 * 
		 * @param fitness the new fitness of the solution currently occupying
		 *        this individual
		 */
		public void setFitness(double fitness) {
			this.fitness = fitness;
		}

	}

	/**
	 * Compares individuals based on their distance from a specified individual.
	 */
	private static class WeightSorter implements Comparator<Individual> {

		/**
		 * The individual from which weight distances are computed.
		 */
		private final Individual individual;

		/**
		 * Constructs a comparator for comparing individuals based on their 
		 * distance from the specified individual.
		 * 
		 * @param individual the individual from which weight distances are
		 *        computed
		 */
		public WeightSorter(Individual individual) {
			this.individual = individual;
		}

		@Override
		public int compare(Individual o1, Individual o2) {
			double d1 = MathUtils.distance(individual.getWeights(), o1
					.getWeights());
			double d2 = MathUtils.distance(individual.getWeights(), o2
					.getWeights());

			return Double.compare(d1, d2);
		}

	}

	/**
	 * The current population.
	 */
	private List<Individual> population;

	/**
	 * The ideal point; each index stores the best observed value for each
	 * objective.
	 */
	private double[] idealPoint;

	/**
	 * The size of the neighborhood used for mating.
	 */
	private final int neighborhoodSize;

	/**
	 * The probability of mating with a solution in the neighborhood rather
	 * than the entire population.
	 */
	private final double delta;

	/**
	 * The maximum number of population slots a solution can replace.
	 */
	private final double eta;

	/**
	 * The initialization operator.
	 */
	private final Initialization initialization;

	/**
	 * The variation operator.
	 */
	private final Variation variation;

	/**
	 * The frequency, in generations, in which utility values are updated.
	 */
	private final int updateUtility;

	/**
	 * The current generation number.
	 */
	private int generation;

	/**
	 * Constructs the MOEA/D algorithm with the specified components.
	 * 
	 * @param problem the problem being solved
	 * @param neighborhoodSize the size of the neighborhood used for mating
	 * @param initialization the initialization method
	 * @param variation the variation operator
	 * @param delta the probability of mating with a solution in the
	 *        neighborhood rather than the entire population
	 * @param eta the maximum number of population slots a solution can replace
	 * @param updateUtility the frequency, in generations, in which utility
	 *        values are updated
	 */
	public MOEAD(Problem problem, int neighborhoodSize,
			Initialization initialization, Variation variation, double delta,
			double eta, int updateUtility) {
		super(problem);
		this.neighborhoodSize = neighborhoodSize;
		this.initialization = initialization;
		this.variation = variation;
		this.delta = delta;
		this.eta = eta;
		this.updateUtility = updateUtility;
	}

	@Override
	public void initialize() {
		super.initialize();

		Solution[] initialSolutions = initialization.initialize();

		initializePopulation(initialSolutions.length);
		initializeNeighborhoods();
		initializeIdealPoint();

		for (int i = 0; i < initialSolutions.length; i++) {
			Solution solution = initialSolutions[i];
			evaluate(solution);
			updateIdealPoint(solution);
			population.get(i).setSolution(solution);
		}

		for (int i = 0; i < initialSolutions.length; i++) {
			population.get(i).setFitness(
					fitness(population.get(i).getSolution(), population.get(i)
							.getWeights()));
		}
	}

	/**
	 * Initializes the population using a procedure attempting to create a
	 * uniform distribution of weights.
	 * 
	 * @param populationSize the population size
	 */
	private void initializePopulation(int populationSize) {
		if (problem.getNumberOfObjectives() == 2) {
			initializePopulation2D(populationSize);
		} else {
			initializePopulationND(populationSize);
		}
	}

	/**
	 * Initializes the population for 2D problems.
	 * 
	 * @param populationSize the population size
	 */
	private void initializePopulation2D(int populationSize) {
		population = new ArrayList<Individual>(populationSize);

		for (int i = 0; i < populationSize; i++) {
			double a = i / (double)(populationSize - 1);
			population.add(new Individual(new double[] { a, 1 - a }));
		}
	}

	/**
	 * Initializes the population for problems of arbitrary dimension.
	 * 
	 * @param populationSize the population size
	 */
	private void initializePopulationND(int populationSize) {
		int N = 50;
		int numberOfObjectives = problem.getNumberOfObjectives();
		List<double[]> weights = new ArrayList<double[]>(populationSize * N);

		// create random weights
		for (int i = 0; i < populationSize * N; i++) {
			double[] weight = new double[numberOfObjectives];
			for (int j = 0; j < numberOfObjectives; j++) {
				weight[j] = PRNG.nextDouble();
			}

			double sum = ArrayMath.sum(weight);
			for (int j = 0; j < numberOfObjectives; j++) {
				weight[j] /= sum;
			}

			weights.add(weight);
		}

		population = new ArrayList<Individual>(populationSize);

		// initialize population with weights (1,0,...,0), (0,1,...,0), ...,
		// (0,...,0,1)
		for (int i = 0; i < numberOfObjectives; i++) {
			double[] weight = new double[numberOfObjectives];
			weight[i] = 1.0;
			population.add(new Individual(weight));
		}

		// fill in remaining weights with the weight vector with the largest
		// distance from the assigned weights
		while (population.size() < populationSize) {
			double[] weight = null;
			double distance = Double.NEGATIVE_INFINITY;

			for (int i = 0; i < weights.size(); i++) {
				double d = Double.POSITIVE_INFINITY;

				for (int j = 0; j < population.size(); j++) {
					d = Math.min(d, MathUtils.distance(weights.get(i),
							population.get(j).getWeights()));
				}

				if (d > distance) {
					weight = weights.get(i);
					distance = d;
					break;
				}
			}

			population.add(new Individual(weight));
			weights.remove(weight);
		}
	}

	/**
	 * Constructs the neighborhoods for all individuals in the population based
	 * on the distances between weights.
	 */
	private void initializeNeighborhoods() {
		List<Individual> sortedPopulation = new ArrayList<Individual>(
				population);

		for (Individual individual : population) {
			Collections.sort(sortedPopulation, new WeightSorter(individual));

			for (int i = 1; i <= neighborhoodSize; i++) {
				individual.addNeighbor(sortedPopulation.get(i));
			}
		}
	}

	/**
	 * Initializes the ideal point.
	 */
	private void initializeIdealPoint() {
		idealPoint = new double[problem.getNumberOfObjectives()];
		Arrays.fill(idealPoint, Double.POSITIVE_INFINITY);
	}

	/**
	 * Updates the ideal point with the specified solution.
	 * 
	 * @param solution the solution
	 */
	private void updateIdealPoint(Solution solution) {
		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			idealPoint[i] = Math.min(idealPoint[i], solution.getObjective(i));
		}
	}

	@Override
	public NondominatedPopulation getResult() {
		NondominatedPopulation result = new NondominatedPopulation();

		if (population != null) {
			for (Individual individual : population) {
				result.add(individual.getSolution());
			}
		}

		return result;
	}

	/**
	 * Returns the population indices to be operated on in the current
	 * generation. Only 1/5 of the population is operated on in each
	 * generation, and individuals with higher utility are preferred.
	 * 
	 * @return the population indices to be operated on in the current
	 *         generation
	 */
	private List<Integer> getSubproblemsToSearch() {
		List<Integer> indices = new ArrayList<Integer>();

		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			indices.add(i);
		}

		for (int i = problem.getNumberOfObjectives(); i < population.size() / 5; i++) {
			int index = PRNG.nextInt(population.size());

			for (int j = 1; j < 10; j++) {
				int temp = PRNG.nextInt(population.size());
				if (population.get(temp).getUtility() > population.get(index)
						.getUtility()) {
					index = temp;
				}
			}

			indices.add(index);
		}

		return indices;
	}

	/**
	 * Returns the population indices to be considered during mating. With
	 * probability {@code delta} the neighborhood is returned; otherwise, the
	 * entire population is returned.
	 * 
	 * @param index the index of the first parent
	 * @return the population indices to be considered during mating
	 */
	private List<Integer> getMatingIndices(int index) {
		List<Integer> matingIndices = new ArrayList<Integer>();

		if (PRNG.nextDouble() <= delta) {
			for (Individual individual : population.get(index).getNeighbors()) {
				matingIndices.add(population.indexOf(individual));
			}
		} else {
			for (int i = 0; i < population.size(); i++) {
				matingIndices.add(i);
			}
		}

		return matingIndices;
	}

	/**
	 * Evaluates the fitness of the specified solution using the Tchebycheff
	 * weights.
	 * 
	 * @param solution the solution
	 * @param weights the weights
	 * @return the fitness of the specified solution using the Tchebycheff
	 *         weights
	 */
	private double fitness(Solution solution, double[] weights) {
		double max = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			max = Math.max(max, weights[i]
					* (solution.getObjective(i) - idealPoint[i]));
		}

		if (solution.violatesConstraints()) {
			max += 10000.0;
		}

		return max;
	}

	/**
	 * Updates the population with the specified solution. Only the specified
	 * population indices are considered for updating. A maximum of {@code eta}
	 * indices will be modified.
	 * 
	 * @param solution the solution
	 * @param matingIndices the population indices that are available for
	 *        updating
	 */
	private void updateSolution(Solution solution, List<Integer> matingIndices) {
		int c = 0;

		while ((c < eta) && !matingIndices.isEmpty()) {
			int j = PRNG.nextItem(matingIndices);
			Individual individual = population.get(j);

			if (fitness(solution, individual.getWeights()) <= fitness(
					individual.getSolution(), individual.getWeights())) {
				individual.setSolution(solution);
				c = c + 1;
			}

			matingIndices.remove((Integer)j);
		}
	}

	/**
	 * Updates the utility of each individual.
	 */
	private void updateUtility() {
		for (Individual individual : population) {
			double oldFitness = individual.getFitness();
			double newFitness = fitness(individual.getSolution(), idealPoint);
			double relativeDecrease = oldFitness - newFitness;

			if (relativeDecrease > 0.001) {
				individual.setUtility(1.0);
			} else {
				double utility = Math.min(1.0, 0.95 * (1.0 + delta / 0.001)
						* individual.getUtility());
				individual.setUtility(utility);
			}

			individual.setFitness(newFitness);
		}
	}

	@Override
	public void iterate() {
		List<Integer> indices = getSubproblemsToSearch();

		for (Integer index : indices) {
			List<Integer> matingIndices = getMatingIndices(index);

			Solution[] parents = new Solution[variation.getArity()];
			parents[0] = population.get(index).getSolution();
			for (int i = 1; i < variation.getArity(); i++) {
				parents[i] = population.get(PRNG.nextItem(matingIndices))
						.getSolution();
			}

			Solution[] offspring = variation.evolve(parents);

			for (Solution child : offspring) {
				evaluate(child);
				updateIdealPoint(child);
				updateSolution(child, matingIndices);
			}
		}

		generation++;

		if (generation % updateUtility == 0) {
			updateUtility();
		}
	}

}
