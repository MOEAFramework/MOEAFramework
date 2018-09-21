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
package org.moeaframework.core.indicator;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Abstract class for implementing R indicator functions.
 * <p>
 * References:
 * <ol>
 *   <li>Hansen, M. P. and A. Jaszkiewicz (1998).  Evaluating the Quality of
 *       Approximations to the Non-dominated Set.  IMM Technical Report
 *       IMM-REP-1998-7.
 * </ol>
 */
public abstract class RIndicator extends NormalizedIndicator {
	
	/**
	 * Interface for defining utility functions.  These utility functions
	 * assume the solutions have been normalized.
	 */
	public interface UtilityFunction {
		
		/**
		 * Computes the utility of the given solution.
		 * 
		 * @param solution the solution
		 * @param weights the weights for the utility calculation
		 * @return the utility of the given solution
		 */
		public double computeUtility(Solution solution, double[] weights);
		
	}
	
	/**
	 * Utility computed as the sum of the weighted objective values.
	 */
	public static class LinearWeightedSumUtility implements UtilityFunction {

		@Override
		public double computeUtility(Solution solution, double[] weights) {
			double sum = 0.0;
			
			for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
				sum += weights[i] * solution.getObjective(i);
			}
			
			return 1.0 - sum;
		}

	}
	
	/**
	 * Chebychev (also referred to as Tchebycheff) utility function.
	 */
	public static class ChebychevUtility implements UtilityFunction {

		@Override
		public double computeUtility(Solution solution, double[] weights) {
			double max = 0.0;
			
			for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
				max = Math.max(max, weights[i] * solution.getObjective(i));
			}
			
			return 1.0 - max;
		}
		
	}
	
	/**
	 * The utility function used by the R2 calculation.
	 */
	protected UtilityFunction utilityFunction;
	
	/**
	 * The weights, typically uniformly distributed.
	 */
	protected double[][] weights;
	
	/**
	 * Constructs a new R indicator using the Chebychev utility function.
	 * 
	 * @param problem the problem
	 * @param subdivisions the number of subdivisions along each objective
	 * @param referenceSet the reference set
	 */
	public RIndicator(Problem problem, int subdivisions,
			NondominatedPopulation referenceSet) {
		this(problem, subdivisions, referenceSet, new ChebychevUtility());
	}
	
	/**
	 * Constructs a new R indicator using the specified utility function.
	 * 
	 * @param problem the problem
	 * @param subdivisions the number of subdivisions along each objective
	 * @param referenceSet the reference set
	 * @param utilityFunction the utility function
	 */
	public RIndicator(Problem problem, int subdivisions,
			NondominatedPopulation referenceSet,
			UtilityFunction utilityFunction) {
		super(problem, referenceSet);
		this.utilityFunction = utilityFunction;
		
		weights = generateUniformWeights(subdivisions,
				problem.getNumberOfObjectives());
	}
	
	/**
	 * Computes the expected utility for the given population.
	 * 
	 * @param population the population
	 * @return the expected utility
	 */
	public double expectedUtility(NondominatedPopulation population) {
		double sum = 0.0;
		
		for (int i = 0; i < weights.length; i++) {
			double max = Double.NEGATIVE_INFINITY;
			
			for (Solution solution : population) {
				max = Math.max(max, utilityFunction.computeUtility(solution,
						weights[i]));
			}
			
			sum += max;
		}
		
		return sum / weights.length;
	}

	/**
	 * Generates uniformly-distributed weights.
	 * 
	 * @param s the number of subdivisions along each objective
	 * @param k the number of objectives
	 * @return the uniformly-distributed weights
	 */
	private static double[][] generateUniformWeights(int s, int k) {
		int counter = 0;
		int N = ArithmeticUtils.pow(s+1, k);
		
		double[][] weights = new double[
				(int)CombinatoricsUtils.binomialCoefficient(s+k-1, k-1)][k];
		
		for (int i = 0; i < N; i++) {
			int sum = 0;
			int[] kary = toBaseK(i, s+1, k);
			
			for (int j = 0; j < k; j++) {
				sum += kary[j];
			}
			
			if (sum == s) {
				for (int j = 0; j < k; j++) {
					weights[counter][j] = kary[j] / (double)s;
				}
				
				counter++;
			}
		}
		
		return weights;
	}

	/**
	 * Converts an integer into its base-k representation.
	 * 
	 * @param number the integer to convert
	 * @param k the base
	 * @param length the length of the resulting base-k representation
	 * @return the base-k representation of the given number
	 */
	private static int[] toBaseK(int number, int k, int length) {
		int value = length-1;
		int[] kary = new int[length];
		int i = 0;
		
		if (number >= ArithmeticUtils.pow(k, length)) {
			throw new FrameworkException("number can not be represented in " +
					"base-k with specified number of digits");
		}
		
		while (number != 0) {
			if (number >= ArithmeticUtils.pow(k, value)) {
				kary[i]++;
				number -= ArithmeticUtils.pow(k, value);
			} else {
				value--;
				i++;
			}
		}
		
		return kary;
	}
	
	/**
	 * Returns the default number of subdivisions for a given problem. The
	 * defaults, for an M objective problem, are:
	 * <ul>
	 *   <li>if M=2, then 500
	 *   <li>if M=3, then 30
	 *   <li>if M=4, then 12
	 *   <li>if M=5, then 8
	 *   <li>else 3
	 * </ul>
	 * 
	 * @param problem the problem
	 * @return the default number of subdivisions for a given problem
	 */
	public static int getDefaultSubdivisions(Problem problem) {
		switch (problem.getNumberOfObjectives()) {
		case 2:
			return 500;
		case 3:
			return 30;
		case 4:
			return 12;
		case 5:
			return 8;
		default:
			return 3;
		}
	}


}
