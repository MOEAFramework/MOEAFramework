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
package org.moeaframework.core.indicator;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/*
 * Portions of this source code are derived from the PISA library.  The PISA
 * license is provided below.
 * 
 * Copyright (c) 2006-2007 Swiss Federal Institute of Technology, Computer 
 * Engineering and Networks Laboratory. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its 
 * documentation for any purpose, without fee, and without written agreement is 
 * hereby granted, provided that the above copyright notice and the following 
 * two paragraphs appear in all copies of this software.
 *
 * IN NO EVENT SHALL THE SWISS FEDERAL INSTITUTE OF TECHNOLOGY, COMPUTER 
 * ENGINEERING AND NETWORKS LABORATORY BE LIABLE TO ANY PARTY FOR DIRECT, 
 * INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE 
 * USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE SWISS FEDERAL 
 * INSTITUTE OF TECHNOLOGY, COMPUTER ENGINEERING AND NETWORKS LABORATORY HAS 
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE SWISS FEDERAL INSTITUTE OF TECHNOLOGY, COMPUTER ENGINEERING AND NETWORKS 
 * LABORATORY, SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE 
 * SWISS FEDERAL INSTITUTE OF TECHNOLOGY, COMPUTER ENGINEERING AND NETWORKS 
 * LABORATORY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS.
 */

/**
 * Hypervolume indicator as implemented by the PISA library.  This version is computationally expensive and is being
 * replaced by {@link WFGNormalizedHypervolume}.
 */
public class PISAHypervolume extends NormalizedIndicator {

	/**
	 * Constructs a hypervolume evaluator for the specified problem and reference set.  See {@link DefaultNormalizer}
	 * for details on configuring normalization.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 */
	public PISAHypervolume(Problem problem, NondominatedPopulation referenceSet) {
		super(problem, referenceSet, DefaultNormalizer.getInstance().getHypervolumeNormalizer(problem, referenceSet));
	}
	
	/**
	 * Constructs a hypervolume evaluator for the specified problem using the given minimum and maximum bounds.
	 * 
	 * @param problem the problem
	 * @param normalizer a user-provided normalizer
	 */
	public PISAHypervolume(Problem problem, Normalizer normalizer) {
		super(problem, new NondominatedPopulation(), normalizer);
	}

	/**
	 * Inverts the objective values since this hypervolume algorithm operates on maximization problems.
	 * 
	 * @param problem the problem
	 * @param solution the solution to be inverted
	 */
	protected static void invert(Problem problem, Solution solution) {
		for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
			double value = solution.getObjective(j);

			if (value < 0.0) {
				value = 0.0;
			} else if (value > 1.0) {
				value = 1.0;
			}

			solution.setObjective(j, 1.0 - value);
		}
	}

	/**
	 * Returns {@code true} if {@code solution1} dominates {@code solution2} with respect to the first
	 * {@code numberOfObjectives} objectives; {@code false} otherwise.
	 * 
	 * @param solution1 the first solution
	 * @param solution2 the second solution
	 * @param numberOfObjectives the number of objectives used when determining domination
	 * @return {@code true} if {@code solution1} dominates {@code solution2} with respect to the first
	 *         {@code numberOfObjectives} objectives; {@code false} otherwise
	 */
	private static boolean dominates(Solution solution1, Solution solution2, int numberOfObjectives) {
		boolean betterInAnyObjective = false;
		boolean worseInAnyObjective = false;

		for (int i = 0; i < numberOfObjectives; i++) {
			if (worseInAnyObjective) {
				break;
			}

			if (solution1.getObjective(i) > solution2.getObjective(i)) {
				betterInAnyObjective = true;
			} else if (solution1.getObjective(i) < solution2.getObjective(i)) {
				worseInAnyObjective = true;
			}
		}

		return !worseInAnyObjective && betterInAnyObjective;
	}

	/**
	 * Swaps the {@code i}-th and {@code j}-th indices in the population.
	 * 
	 * @param population the population
	 * @param i the first index to be swapped
	 * @param j the second index to be swapped
	 */
	private static void swap(List<Solution> population, int i, int j) {
		Solution temp = population.get(i);
		population.set(i, population.get(j));
		population.set(j, temp);
	}

	/**
	 * All nondominated points regarding the first {@code numberOfObjectives} dimensions are collected.  The points
	 * {@code 0..numberOfSolutions-1} in 'front' are considered.  The points in 'front' are resorted, such that points
	 * {@code [0..n-1]} represent the nondominated points.
	 * 
	 * @returns the value {@code n}
	 */
	private static int filterNondominatedSet(List<Solution> population, int numberOfSolutions, int numberOfObjectives) {
		int i = 0;
		int n = numberOfSolutions;

		while (i < n) {
			int j = i + 1;
			while (j < n) {
				if (dominates(population.get(i), population.get(j), numberOfObjectives)) {
					// remove point j
					n--;
					swap(population, j, n);
				} else if (dominates(population.get(j), population.get(i), numberOfObjectives)) {
					// remove point i; ensure that the point copied to index i is considered in the next outer loop
					// (thus, decrement i)
					n--;
					swap(population, i, n);
					i--;
					break;
				} else {
					j++;
				}
			}

			i++;
		}

		return n;
	}

	/**
	 * Calculate next value regarding dimension {@code objective}; consider points {@code 0..numberOfSolutions-1}
	 * in 'front'.
	 */
	private static double surfaceUnchangedTo(List<Solution> population, int numberOfSolutions, int objective) {
		double min = population.get(0).getObjective(objective);

		for (int i = 1; i < numberOfSolutions; i++) {
			min = Math.min(min, population.get(i).getObjective(objective));
		}

		return min;
	}

	/**
	 * Remove all points which have a value {@code <= threshold} regarding the dimension {@code objective}.  The points
	 * {@code [0..numberOfSolutions-1]} in 'front' are considered.  'front' is resorted, such that points
	 * {@code [0..n-1]} represent the remaining points.
	 * 
	 * @returns the value {@code n}
	 */
	private static int reduceNondominatedSet(List<Solution> population, int numberOfSolutions, int objective,
			double threshold) {
		int n = numberOfSolutions;

		for (int i = 0; i < n; i++) {
			if (population.get(i).getObjective(objective) <= threshold) {
				n--;
				swap(population, i, n);
			}
		}

		return n;
	}

	/**
	 * The internal, un-normalized hypervolume calculation.  While this method is public, we do not encourage its use
	 * since incorrect arguments can cause unexpected behavior.  Instead, use the
	 * {@link PISAHypervolume(Problem, NondominatedPopulation)} constructor to create a normalizing version of the
	 * hypervolume calculation.
	 * 
	 * @param population the population
	 * @param numberOfSolutions the number of solutions
	 * @param numberOfObjectives the number of objectives
	 * @return the hypervolume metric
	 */
	public static double calculateHypervolume(List<Solution> population, int numberOfSolutions,
			int numberOfObjectives) {
		double volume = 0.0;
		double distance = 0.0;
		int n = numberOfSolutions;

		while (n > 0) {
			int numberOfNondominatedPoints = filterNondominatedSet(population, n, numberOfObjectives - 1);

			double tempVolume = 0.0;
			if (numberOfObjectives < 3) {
				tempVolume = population.get(0).getObjective(0);
			} else {
				tempVolume = calculateHypervolume(population, numberOfNondominatedPoints, numberOfObjectives - 1);
			}

			double tempDistance = surfaceUnchangedTo(population, n, numberOfObjectives - 1);
			volume += tempVolume * (tempDistance - distance);
			distance = tempDistance;
			n = reduceNondominatedSet(population, n, numberOfObjectives - 1, distance);
		}

		return volume;
	}

	@Override
	public double evaluate(NondominatedPopulation approximationSet) {
		return evaluate(problem, normalize(approximationSet));
	}

	/**
	 * Computes the hypervolume of the normalized approximation set.
	 * 
	 * @param problem the problem
	 * @param approximationSet the normalized approximation set
	 * @return the hypervolume of the normalized approximation set
	 */
	static double evaluate(Problem problem, NondominatedPopulation approximationSet) {
		List<Solution> solutions = new ArrayList<Solution>();

		outer: for (Solution solution : approximationSet) {
			//prune any solutions which exceed the Nadir point
			for (int i=0; i<solution.getNumberOfObjectives(); i++) {
				if (solution.getObjective(i) > 1.0) {
					continue outer;
				}
			}
			
			Solution clone = solution.copy();
			invert(problem, clone);	
			solutions.add(clone);
		}

		return calculateHypervolume(solutions, solutions.size(), problem.getNumberOfObjectives());
	}

}
