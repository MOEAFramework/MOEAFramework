/* Copyright 2009-2018 David Hadka
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.util.MathArrays;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.CompoundVariation;
import org.moeaframework.core.operator.real.DifferentialEvolutionVariation;
import org.moeaframework.util.weights.RandomGenerator;
import org.moeaframework.util.weights.WeightGenerator;

/**
 * Implementation of MOEA/D, the multiobjective evolutionary algorithm with
 * decomposition.  This implementation supports both the original MOEA/D
 * specification from [1] as well as the utility-based search extension from
 * [2].
 * <p>
 * References:
 * <ol>
 * <li>Li, H. and Zhang, Q. "Multiobjective Optimization problems with
 * Complicated Pareto Sets, MOEA/D and NSGA-II." IEEE Transactions on
 * Evolutionary Computation, 13(2):284-302, 2009.
 * <li>Zhang, Q., et al.  "The Performance of a New Version of MOEA/D on
 * CEC09 Unconstrained MOP Test Instances."  IEEE Congress on Evolutionary
 * Computation, 2009.
 * </ol>
 */
public class MOEAD extends AbstractAlgorithm {

	/**
	 * Represents an individual (population slot) in the MOEA/D algorithm.
	 */
	private static class Individual implements Serializable {

		private static final long serialVersionUID = 868794189268472009L;

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
		 * individual when the utility was last updated.
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
		 * individual when the utility was last updated.
		 * 
		 * @return the cached fitness of the solution currently occupying this
		 *         individual when the utility was last updated
		 */
		public double getFitness() {
			return fitness;
		}

		/**
		 * Sets the cached fitness of the solution currently occupying this
		 * individual when the utility is updated
		 * 
		 * @param fitness the new fitness of the solution currently occupying
		 *        this individual when the utility is updated
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
			double d1 = MathArrays.distance(
					individual.getWeights(), o1.getWeights());
			double d2 = MathArrays.distance(
					individual.getWeights(), o2.getWeights());

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
	 * The weight generator; or {@code null} if the default weight generator
	 * is used.
	 */
	private final WeightGenerator weightGenerator;

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
	 * The frequency, in generations, in which utility values are updated.  Set
	 * to {@code -1} to disable utility-based search.  [2] recommends to update
	 * every {@code 50} generations.
	 */
	private final int updateUtility;

	/**
	 * Set to {@code true} if using differential evolution.
	 */
	final boolean useDE;
	
	/**
	 * The current generation number.
	 */
	private int generation;
	
	/**
	 * Constructs the MOEA/D algorithm with the specified components.  This
	 * version of MOEA/D uses utility-based search as described in [2].
	 * 
	 * @param problem the problem being solved
	 * @param neighborhoodSize the size of the neighborhood used for mating,
	 *        which must be at least {@code variation.getArity()-1}.
	 * @param initialization the initialization method
	 * @param variation the variation operator
	 * @param delta the probability of mating with a solution in the
	 *        neighborhood rather than the entire population
	 * @param eta the maximum number of population slots a solution can replace
	 * @param updateUtility the frequency, in generations, in which utility
	 *        values are updated; set to {@code 50} to use the recommended
	 *        update frequency or {@code -1} to disable utility-based search.
	 */
	public MOEAD(Problem problem, int neighborhoodSize,
			Initialization initialization, Variation variation, double delta,
			double eta, int updateUtility) {
		this(problem, neighborhoodSize, null, initialization, variation, delta,
				eta, updateUtility);
	}
	
	/**
	 * Constructs the MOEA/D algorithm with the specified components.  This
	 * constructs the original MOEA/D implementation without utility-based
	 * search.
	 * 
	 * @param problem the problem being solved
	 * @param neighborhoodSize the size of the neighborhood used for mating,
	 *        which must be at least {@code variation.getArity()-1}.
	 * @param initialization the initialization method
	 * @param variation the variation operator
	 * @param delta the probability of mating with a solution in the
	 *        neighborhood rather than the entire population
	 * @param eta the maximum number of population slots a solution can replace
	 */
	public MOEAD(Problem problem, int neighborhoodSize,
			Initialization initialization, Variation variation, double delta,
			double eta) {
		this(problem, neighborhoodSize, initialization, variation, delta, eta,
				-1);
	}

	/**
	 * Constructs the MOEA/D algorithm with the specified components.  This
	 * version of MOEA/D uses utility-based search as described in [2].
	 * 
	 * @param problem the problem being solved
	 * @param neighborhoodSize the size of the neighborhood used for mating,
	 *        which must be at least {@code variation.getArity()-1}.
	 * @param weightGenerator the weight generator
	 * @param initialization the initialization method, which must generate the
	 *        same number of solutions as weights
	 * @param variation the variation operator
	 * @param delta the probability of mating with a solution in the
	 *        neighborhood rather than the entire population
	 * @param eta the maximum number of population slots a solution can replace
	 * @param updateUtility the frequency, in generations, in which utility
	 *        values are updated; set to {@code 50} to use the recommended
	 *        update frequency or {@code -1} to disable utility-based search.
	 */
	public MOEAD(Problem problem, int neighborhoodSize,
			WeightGenerator weightGenerator, Initialization initialization,
			Variation variation, double delta, double eta, int updateUtility) {
		super(problem);
		this.neighborhoodSize = neighborhoodSize;
		this.weightGenerator = weightGenerator;
		this.initialization = initialization;
		this.variation = variation;
		this.delta = delta;
		this.eta = eta;
		this.updateUtility = updateUtility;
		
		if (variation instanceof DifferentialEvolutionVariation) {
			useDE = true;
		} else if (variation instanceof CompoundVariation) {
			CompoundVariation compoundVariation = (CompoundVariation)variation;
			useDE = compoundVariation.getName().startsWith(
					DifferentialEvolutionVariation.class.getSimpleName());
		} else {
			useDE = false;
		}
	}
	
	/**
	 * Constructs the MOEA/D algorithm with the specified components.  This
	 * constructs the original MOEA/D implementation without utility-based
	 * search.
	 * 
	 * @param problem the problem being solved
	 * @param neighborhoodSize the size of the neighborhood used for mating,
	 *        which must be at least {@code variation.getArity()-1}.
	 * @param weightGenerator the weight generator
	 * @param initialization the initialization method, which must generate the
	 *        same number of solutions as weights
	 * @param variation the variation operator
	 * @param delta the probability of mating with a solution in the
	 *        neighborhood rather than the entire population
	 * @param eta the maximum number of population slots a solution can replace
	 */
	public MOEAD(Problem problem, int neighborhoodSize,
			WeightGenerator weightGenerator, Initialization initialization,
			Variation variation, double delta, double eta) {
		this(problem, neighborhoodSize, weightGenerator, initialization,
				variation, delta, eta, -1);
	}

	@Override
	public void initialize() {
		super.initialize();

		Solution[] initialSolutions = initialization.initialize();

		initializePopulation(initialSolutions.length);
		initializeNeighborhoods();
		initializeIdealPoint();
		evaluateAll(initialSolutions);
		
		for (int i = 0; i < initialSolutions.length; i++) {
			Solution solution = initialSolutions[i];
			updateIdealPoint(solution);
			population.get(i).setSolution(solution);
		}

		for (int i = 0; i < initialSolutions.length; i++) {
			population.get(i).setFitness(fitness(
					population.get(i).getSolution(),
					population.get(i).getWeights()));
		}
	}

	/**
	 * Initializes the population using a procedure attempting to create a
	 * uniform distribution of weights.
	 * 
	 * @param populationSize the population size
	 */
	private void initializePopulation(int populationSize) {
		population = new ArrayList<Individual>(populationSize);
		
		if (weightGenerator == null) {
			List<double[]> weights = new RandomGenerator(
					problem.getNumberOfObjectives(), populationSize).generate();
			
			for (double[] weight : weights) {
				population.add(new Individual(weight));
			}
		} else {
			List<double[]> weights = weightGenerator.generate();
			
			if (weights.size() != populationSize) {
				throw new FrameworkException("weight generator must return " +
						populationSize + " weights");
			}
			
			for (double[] weight : weights) {
				population.add(new Individual(weight));
			}
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

			for (int i = 0; i < neighborhoodSize; i++) {
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
	 * generation.  If the utility update frequency has been set, then this
	 * method follows the utility-based MOEA/D search described in [2].
	 * Otherwise, this follows the original MOEA/D specification from [1].
	 * 
	 * @return the population indices to be operated on in the current
	 *         generation
	 */
	private List<Integer> getSubproblemsToSearch() {
		List<Integer> indices = new ArrayList<Integer>();
		
		if (updateUtility < 0) {
			// return all indices
			for (int i = 0; i < population.size(); i++) {
				indices.add(i);
			}
		} else {
			// return 1/5 of the indices chosen by their utility
			for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
				indices.add(i);
			}
	
			for (int i = problem.getNumberOfObjectives(); i < population.size() / 5; i++) {
				int index = PRNG.nextInt(population.size());
	
				for (int j = 1; j < 10; j++) {
					int temp = PRNG.nextInt(population.size());
					
					if (population.get(temp).getUtility() > 
							population.get(index).getUtility()) {
						index = temp;
					}
				}
	
				indices.add(index);
			}
		}
		
		PRNG.shuffle(indices);

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
	 * Evaluates the fitness of the specified solution using the Chebyshev
	 * weights.
	 * 
	 * @param solution the solution
	 * @param weights the weights
	 * @return the fitness of the specified solution using the Chebyshev
	 *         weights
	 */
	private double fitness(Solution solution, double[] weights) {
		double max = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
			max = Math.max(max, Math.max(weights[i], 0.0001)
					* Math.abs(solution.getObjective(i) - idealPoint[i]));
		}

		if (solution.violatesConstraints()) {
			max += 10000.0;
		}

		return max;
	}
	
	private double sumOfConstraintViolations(Solution solution) {
		double sum = 0.0;
		
		for (int i = 0; i < solution.getNumberOfConstraints(); i++) {
			sum += Math.abs(solution.getConstraint(i));
		}
		
		return sum;
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
	private void updateSolution(Solution solution,
			List<Integer> matingIndices) {
		int c = 0;
		PRNG.shuffle(matingIndices);
		
		for (int i = 0; i < matingIndices.size(); i++) {
			Individual individual = population.get(matingIndices.get(i));
			boolean canReplace = false;
			
			if (solution.violatesConstraints() &&
					individual.getSolution().violatesConstraints()) {
				double cv1 = sumOfConstraintViolations(solution);
				double cv2 = sumOfConstraintViolations(individual.getSolution());
				
				if (cv1 < cv2) {
					canReplace = true;
				}
			} else if (individual.getSolution().violatesConstraints()) {
				canReplace = true;
			} else if (solution.violatesConstraints()) {
				// do nothing
			} else {
				if (fitness(solution, individual.getWeights()) <
						fitness(individual.getSolution(),
								individual.getWeights())) {
					canReplace = true;
				}
			}
			
			if (canReplace) {
				individual.setSolution(solution);
				c = c + 1;
			}
			
			if (c >= eta) {
				break;
			}
		}
	}

	/**
	 * Updates the utility of each individual.
	 */
	protected void updateUtility() {
		for (Individual individual : population) {
			double oldFitness = individual.getFitness();
			double newFitness = fitness(individual.getSolution(), idealPoint);
			double relativeDecrease = (oldFitness - newFitness) / oldFitness; 

			if (relativeDecrease > 0.001) {
				individual.setUtility(1.0);
			} else {
				double utility = Math.min(1.0,
						(0.95 + 0.05*relativeDecrease/0.001)* individual.getUtility());
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
			
			if (useDE) {
				// MOEA/D parent selection for differential evolution
				PRNG.shuffle(matingIndices);
				
				for (int i = 1; i < variation.getArity()-1; i++) {
					parents[i] = population.get(
							matingIndices.get(i-1)).getSolution();
				}
				
				parents[variation.getArity()-1] = 
						population.get(index).getSolution();
			} else {
				for (int i = 1; i < variation.getArity(); i++) {
					parents[i] = population.get(
							PRNG.nextItem(matingIndices)).getSolution();
				}
			}

			Solution[] offspring = variation.evolve(parents);

			for (Solution child : offspring) {
				evaluate(child);
				updateIdealPoint(child);
				updateSolution(child, matingIndices);
			}
		}

		generation++;

		if ((updateUtility >= 0) && (generation % updateUtility == 0)) {
			updateUtility();
		}
	}
	
	/**
	 * Proxy for serializing and deserializing the state of a
	 * {@code MOEAD} instance. This proxy supports saving
	 * the {@code population}, {@code idealPoint} and {@code generation}.
	 */
	private static class MOEADState implements Serializable {

		private static final long serialVersionUID = 8694911146929397897L;

		/**
		 * The {@code population} from the {@code MOEAD} instance.
		 */
		private final List<Individual> population;
		
		/**
		 * The value of the {@code idealPoint} from the {@code MOEAD} instance.
		 */
		private final double[] idealPoint;
		
		/**
		 * The value of {@code numberOfEvaluations} from the {@code MOEAD}
		 * instance.
		 */
		private final int numberOfEvaluations;
		
		/**
		 * The value of {@code generation} from the {@code MOEAD} instance.
		 */
		private final int generation;
		
		/**
		 * Constructs a proxy for serializing and deserializing the state of a
		 * {@code MOEAD} instance.
		 * 
		 * @param population the {@code population} from the {@code MOEAD}
		 *        instance
		 * @param idealPoint the value of the {@code idealPoint} from the
		 *        {@code MOEAD} instance
		 * @param numberOfEvaluations the value of {@code numberOfEvaluations}
		 *        from the {@code MOEAD} instance
		 * @param generation the value of {@code generation} from the
		 *        {@code MOEAD} instance
		 */
		public MOEADState(List<Individual> population, double[] idealPoint,
				int numberOfEvaluations, int generation) {
			super();
			this.population = population;
			this.idealPoint = idealPoint;
			this.numberOfEvaluations = numberOfEvaluations;
			this.generation = generation;
		}

		/**
		 * Returns the {@code population} from the {@code MOEAD} instance.
		 * 
		 * @return the {@code population} from the {@code MOEAD} instance
		 */
		public List<Individual> getPopulation() {
			return population;
		}

		/**
		 * Returns the value of the {@code idealPoint} from the {@code MOEAD}
		 * instance.
		 * 
		 * @return the value of the {@code idealPoint} from the {@code MOEAD}
		 *         instance
		 */
		public double[] getIdealPoint() {
			return idealPoint;
		}
		
		/**
		 * Returns the value of {@code numberOfEvaluations} from the
		 * {@code MOEAD} instance.
		 * 
		 * @return the value of {@code numberOfEvaluations} from the
		 *         {@code MOEAD} instance
		 */
		public int getNumberOfEvaluations() {
			return numberOfEvaluations;
		}

		/**
		 * Returns the value of {@code generation} from the {@code MOEAD}
		 * instance.
		 * 
		 * @return the value of {@code generation} from the {@code MOEAD}
		 *         instance
		 */
		public int getGeneration() {
			return generation;
		}
		
	}

	@Override
	public Serializable getState() throws NotSerializableException {
		return new MOEADState(population, idealPoint, numberOfEvaluations,
				generation);
	}

	@Override
	public void setState(Object objState) throws NotSerializableException {
		super.initialize();

		MOEADState state = (MOEADState)objState;

		population = state.getPopulation();
		idealPoint = state.getIdealPoint();
		numberOfEvaluations = state.getNumberOfEvaluations();
		generation = state.getGeneration();
	}
	
}
