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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.math3.util.KthSelector;
import org.apache.commons.math3.util.Pair;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.FitnessComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.indicator.IndicatorUtils;
import org.moeaframework.core.operator.TournamentSelection;

/**
 * Implementation of the strength-based evolutionary algorithm (SPEA2).  SPEA2
 * uses a novel strength-based measure of fitness for handling multiple
 * objectives.
 * <p>
 * Note: First, there is a naming difference between this implementation and
 * the original SPEA2 paper.  The original SPEA2 paper defines a "population"
 * and an "archive", but the population is really the offspring and the archive
 * is the population.  Secondly, the SPEA2 paper defines a parameter
 * {@code k = sqrt(population.size())} for computing a crowding-based niching
 * factor.  The SPEA2 C implementation in PISA (written by the same authors
 * as the paper) recommends using {@code k=1} for performance reasons.  This
 * implementation makes {@code k} a user-specified parameter to support either
 * option.  {@code k} should be at least {@code 1} and no larger than
 * {@code population.size()}.
 * <p>
 * References:
 * <ol>
 *   <li>Zitzler, E., M. Laumanns, and L. Thiele (2001).  SPEA2: Improving the
 *       Strength Pareto Evolutionary Algorithm.  TIK-Report 103.
 * </ol>
 */
public class SPEA2 extends AbstractEvolutionaryAlgorithm {
	
	/**
	 * The selection operator.
	 */
	private final Selection selection;
	
	/**
	 * The variation operator.
	 */
	private final Variation variation;
	
	/**
	 * The number of offspring.
	 */
	private final int numberOfOffspring;
	
	/**
	 * Strength-based fitness evaluator.
	 */
	protected final StrengthFitnessEvaluator fitnessEvaluator;
	
	/**
	 * Compares solutions based on strength.
	 */
	protected final FitnessComparator fitnessComparator;

	/**
	 * Constructs a new instance of SPEA2.
	 * 
	 * @param problem the problem
	 * @param initialization the initialization procedure
	 * @param variation the variation operator
	 * @param numberOfOffspring the number of offspring generated each iteration
	 * @param k niching parameter specifying that crowding is computed using
	 *        the {@code k}-th nearest neighbor, recommend {@code k=1}
	 */
	public SPEA2(Problem problem, Initialization initialization,
			Variation variation, int numberOfOffspring, int k) {
		super(problem, new Population(), null, initialization);
		this.variation = variation;
		this.numberOfOffspring = numberOfOffspring;
		
		fitnessEvaluator = new StrengthFitnessEvaluator(k);
		fitnessComparator = new FitnessComparator(fitnessEvaluator.areLargerValuesPreferred());
		selection = new TournamentSelection(fitnessComparator);
	}

	@Override
	protected void initialize() {
		super.initialize();
		
		fitnessEvaluator.evaluate(population);
	}

	@Override
	protected void iterate() {
		// mating and selection to generate offspring
		Population offspring = new Population();
		int populationSize = population.size();

		while (offspring.size() < numberOfOffspring) {
			Solution[] parents = selection.select(variation.getArity(),
					population);
			Solution[] children = variation.evolve(parents);

			offspring.addAll(children);
		}

		// evaluate the offspring
		evaluateAll(offspring);
		
		// evaluate the fitness of the population and offspring
		offspring.addAll(population);
		fitnessEvaluator.evaluate(offspring);
		
		// perform environmental selection to downselect the next population
		population.clear();
		population.addAll(truncate(offspring, populationSize));
	}
	
	/**
	 * Returns the population of solutions that survive to the next generation.
	 * 
	 * @param offspring all offspring solutions
	 * @param size the number of solutions to retain
	 * @return the population of solutions that survive to the next generation
	 */
	protected Population truncate(Population offspring, int size) {
		Population survivors = new Population();
		
		// add all non-dominated solutions with a fitness < 1
		Iterator<Solution> iterator = offspring.iterator();
		
		while (iterator.hasNext()) {
			Solution solution = iterator.next();
			double fitness = (Double)solution.getAttribute(
					FitnessEvaluator.FITNESS_ATTRIBUTE);
			
			if (fitness < 1.0) {
				survivors.add(solution);
				iterator.remove();
			}
		}
		
		if (survivors.size() < size) {
			// fill remaining spaces with dominated solutions
			offspring.sort(fitnessComparator);
			
			while (survivors.size() < size) {
				survivors.add(offspring.get(0));
				offspring.remove(0);
			}
		} else if (survivors.size() > size) {
			// some of the survivors must be truncated
			MutableDistanceMap map = new MutableDistanceMap(computeDistanceMatrix(survivors));
			
			while (survivors.size() > size) {
				int index = map.findMostCrowdedPoint();
				
				map.removePoint(index);
				survivors.remove(index);
			}
		}
		
		return survivors;
	}
	
	/**
	 * Computes the distance matrix containing the pair-wise distances between
	 * solutions in objective space.  The diagonal will contain all 0's.
	 * 
	 * @param population the population of solutions
	 * @return the distance matrix
	 */
	protected double[][] computeDistanceMatrix(Population population) {
		double[][] distances = new double[population.size()][population.size()];
		
		for (int i = 0; i < population.size(); i++) {
			distances[i][i] = 0.0;
			
			for (int j = i+1; j < population.size(); j++) {
				distances[i][j] = distances[j][i] = 
						IndicatorUtils.euclideanDistance(problem,
								population.get(i), population.get(j));
			}
		}
		
		return distances;
	}
	
	/**
	 * Mapping of pair-wise distances between points.  This mapping is mutable,
	 * allowing points to be removed.
	 */
	public static class MutableDistanceMap {
		
		/**
		 * The internal mapping of distances.
		 */
		private List<List<Pair<Integer, Double>>> distanceMatrix;
		
		/**
		 * Constructs a new mapping of pair-wise distances between points.
		 * 
		 * @param rawDistanceMatrix the distance matrix
		 */
		public MutableDistanceMap(double[][] rawDistanceMatrix) {
			super();
			initialize(rawDistanceMatrix);
		}
		
		/**
		 * Initializes the internal data structures.
		 * 
		 * @param rawDistanceMatrix the distance matrix
		 */
		protected void initialize(double[][] rawDistanceMatrix) {
			distanceMatrix = new LinkedList<List<Pair<Integer, Double>>>();
			
			for (int i = 0; i < rawDistanceMatrix.length; i++) {
				List<Pair<Integer, Double>> distances = new LinkedList<Pair<Integer, Double>>();
				
				for (int j = 0; j < rawDistanceMatrix[i].length; j++) {
					if (i != j) {
						distances.add(new Pair<Integer, Double>(j, rawDistanceMatrix[i][j]));
					}
				}
				
				Collections.sort(distances, new Comparator<Pair<Integer, Double>>() {

					@Override
					public int compare(Pair<Integer, Double> o1,
							Pair<Integer, Double> o2) {
						return Double.compare(o1.getSecond(), o2.getSecond());
					}
					
				});
				
				distanceMatrix.add(distances);
			}
		}
		
		/**
		 * Returns the most crowded point according to SPEA2's truncation
		 * strategy.  The most crowded point is the point with the smallest
		 * distance to its nearest neighbor.  Ties are broken by looking at
		 * the next nearest neighbor repeatedly until a difference is found.
		 * 
		 * @return the index of the most crowded point
		 */
		public int findMostCrowdedPoint() {
			double minimumDistance = Double.POSITIVE_INFINITY;
			int minimumIndex = -1;
			
			for (int i = 0; i < distanceMatrix.size(); i++) {
				List<Pair<Integer, Double>> distances = distanceMatrix.get(i);
				Pair<Integer, Double> point = distances.get(0);
				
				if (point.getSecond() < minimumDistance) {
					minimumDistance = point.getSecond();
					minimumIndex = i;
				} else if (point.getSecond() == minimumDistance) {
					for (int k = 0; k < distances.size(); k++) {
						double kdist1 = distances.get(k).getSecond();
						double kdist2 = distanceMatrix.get(minimumIndex).get(k).getSecond();

						if (kdist1 < kdist2) {
							minimumIndex = i;
							break;
						} else if (kdist2 < kdist1) {
							break;
						}
					}
				}
			}
			
			return minimumIndex;
		}
		
		/**
		 * Removes the point with the given index.
		 * 
		 * @param index the index to remove
		 */
		public void removePoint(int index) {
			distanceMatrix.remove(index);
			
			for (List<Pair<Integer, Double>> distances : distanceMatrix) {
				ListIterator<Pair<Integer, Double>> iterator = distances.listIterator();
				
				while (iterator.hasNext()) {
					Pair<Integer, Double> point = iterator.next();
					
					if (point.getFirst() == index) {
						iterator.remove();
					} else if (point.getFirst() > index) {
						// decrement the index so it stays aligned with the
						// index in distanceMatrix
						iterator.set(new Pair<Integer, Double>(
								point.getFirst()-1, point.getSecond()));
					}
				}
			}
		}
		
	}
	
	/**
	 * Fitness evaluator for the strength measure with crowding-based niching.
	 */
	public class StrengthFitnessEvaluator implements FitnessEvaluator {
		
		/**
		 * Crowding is based on the distance to the {@code k}-th nearest
		 * neighbor.
		 */
		private final int k;
		
		/**
		 * Pareto dominance comparator.
		 */
		private final DominanceComparator comparator;
		
		/**
		 * Constructs a new fitness evaluator for computing the strength
		 * measure with crowding-based niching.
		 * 
		 * @param k crowding is based on the distance to the {@code k}-th
		 *        nearest neighbor
		 */
		public StrengthFitnessEvaluator(int k) {
			super();
			this.k = k;
			
			comparator = new ParetoDominanceComparator();
		}

		@Override
		public void evaluate(Population population) {
			int[] strength = new int[population.size()];
			double[] fitness = new double[population.size()];
			
			// count the number of individuals each solution dominates
			for (int i = 0; i < population.size()-1; i++) {
				for (int j = i+1; j < population.size(); j++) {
					int comparison = comparator.compare(population.get(i),
							population.get(j));
					
					if (comparison < 0) {
						strength[i]++;
					} else if (comparison > 0) {
						strength[j]++;
					}
				}
			}
			
			// the raw fitness is the sum of the dominance counts (strength)
			// of all dominated solutions
			for (int i = 0; i < population.size()-1; i++) {
				for (int j = i+1; j < population.size(); j++) {
					int comparison = comparator.compare(population.get(i),
							population.get(j));
					
					if (comparison < 0) {
						fitness[j] += strength[i];
					} else if (comparison > 0) {
						fitness[i] += strength[j];
					}
				}
			}
			
			// add density to the fitness
			double[][] distances = computeDistanceMatrix(population);
			KthSelector selector = new KthSelector();
			
			for (int i = 0; i < population.size(); i++) {
				double kdist = selector.select(distances[i], null, k);
				fitness[i] += 1.0 / (kdist + 2.0);
			}
			
			// assign fitness attribute to solutions
			for (int i = 0; i < population.size(); i++) {
				population.get(i).setAttribute(FITNESS_ATTRIBUTE, fitness[i]);
			}
		}

		@Override
		public boolean areLargerValuesPreferred() {
			return false;
		}
		
	}

}
