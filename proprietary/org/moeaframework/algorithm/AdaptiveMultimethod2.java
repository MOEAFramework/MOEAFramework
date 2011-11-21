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

import java.util.Arrays;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.util.ArrayMath;

/**
 * Auto-adaptive multi-method recombination operator. Applies operators with
 * probabilities proportional to the number of offspring produced by each
 * operator in the archive.
 */
public class AdaptiveMultimethod2 extends AdaptiveMultimethod {
	
	private int[] currentCount;
	
	private double[] movingAverageCount;

	/**
	 * Constructs an auto-adaptive multi-method recombination operator with the
	 * specified archive for updating probabilities.
	 * 
	 * @param archive the archive used to update the probabilities
	 */
	public AdaptiveMultimethod2(Population archive) {
		super(archive);
	}
	
	/**
	 * Returns the probability that the operator at the specified index is
	 * applied.
	 * 
	 * @param index the index of the operator whose probability is returned
	 * @return the probability that the operator at the specified index is
	 *         applied
	 */
	public double getOperatorProbability(int index) {
		return probabilities[index];
	}
	
	public void solutionAcceptedToArchive(Solution solution) {
		if (solution.hasAttribute(OPERATOR_ATTRIBUTE)) {
			currentCount[(Integer)solution.getAttribute(OPERATOR_ATTRIBUTE)]++;
		}
	}
	
	public void update() {
		double alpha = 0.2;
		int N = getNumberOfOperators();
		
		if ((currentCount == null) || (movingAverageCount == null)) {
			currentCount = new int[N];
			movingAverageCount = new double[N];
		}
		
		for (int i=0; i<N; i++) {
			movingAverageCount[i] = alpha*currentCount[i] + (1.0-alpha)*movingAverageCount[i];
		}
		
		double sum = ArrayMath.sum(movingAverageCount);
		double[] probabilities = new double[N];
		
		if (sum <= 0.05) {
			Arrays.fill(movingAverageCount, 1.0);
			sum = ArrayMath.sum(movingAverageCount);
		}

		for (int i = 0; i < N; i++) {
			probabilities[i] = movingAverageCount[i] / sum;
		}
		
		correctProbabilities(probabilities, 0.05);		
		Arrays.fill(currentCount, 0);

		this.probabilities = probabilities;
	}

	/**
	 * Sets the {@code nextOperatorIndex} value to a randomly-selected operator
	 * using the current operator probabilities.
	 * 
	 * @return the {@code nextOperatorIndex} value
	 */
	protected int getNextOperator() {
		if (nextOperatorIndex != NO_OPERATOR_SELECTED) {
			return nextOperatorIndex;
		}

		double rand = PRNG.nextDouble();
		double sum = 0.0;

		for (int i = 0; i < getNumberOfOperators(); i++) {
			sum += probabilities[i];

			if (sum > rand) {
				nextOperatorIndex = i;
				return nextOperatorIndex;
			}
		}

		throw new IllegalStateException();
	}

	/**
	 * Procedure for adjusting an array of probabilities to ensure the
	 * probabilities are at least {@code minimum}.
	 * 
	 * @param probabilities the array of probabilities
	 * @param minimum the minimum probability
	 */
	public static void correctProbabilities(double[] probabilities,
			double minimum) {
		double diff = 0.0;
		double allowance = 0.0;

		for (int i = 0; i < probabilities.length; i++) {
			if (probabilities[i] <= minimum) {
				diff += minimum - probabilities[i];
				probabilities[i] = minimum;
			} else {
				allowance += probabilities[i] - minimum;
			}
		}

		for (int i = 0; i < probabilities.length; i++) {
			if (probabilities[i] > minimum) {
				probabilities[i] -= diff
						* ((probabilities[i] - minimum) / allowance);
			}
		}
	}

}
