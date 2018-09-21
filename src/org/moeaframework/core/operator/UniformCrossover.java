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
package org.moeaframework.core.operator;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;

/**
 * Crossover operator where each index is swapped with a specified probability.
 */
public class UniformCrossover implements Variation {

	/**
	 * The probability an index is swapped between two solutions.
	 */
	private final double probability;

	/**
	 * Constructs a uniform crossover operator with the specified probability an
	 * index is swapped between two solutions.
	 * 
	 * @param probability the probability an index is swapped between two
	 *        solutions
	 */
	public UniformCrossover(double probability) {
		this.probability = probability;
	}

	/**
	 * Returns the probability an index is swapped between two solutions.
	 * 
	 * @return the probability an index is swapped between two solutions
	 */
	public double getProbability() {
		return probability;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result1 = parents[0].copy();
		Solution result2 = parents[1].copy();

		if (PRNG.nextDouble() <= probability) {
			for (int i = 0; i < result1.getNumberOfVariables(); i++) {
				if (PRNG.nextBoolean()) {
					Variable temp = result1.getVariable(i);
					result1.setVariable(i, result2.getVariable(i));
					result2.setVariable(i, temp);
				}
			}
		}

		return new Solution[] { result1, result2 };
	}

	@Override
	public int getArity() {
		return 2;
	}

}
