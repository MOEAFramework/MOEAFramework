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
package org.moeaframework.core.operator.permutation;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.Permutation;

/**
 * Swap mutation operator. Randomly selects two entries in the permutation and
 * swaps their position.
 * <p>
 * This operator is type-safe.
 */
public class Swap implements Variation {

	/**
	 * The probability of mutating a variable.
	 */
	private final double probability;

	/**
	 * Constructs a swap mutation operator with the specified probability of 
	 * mutating a variable.
	 * 
	 * @param probability the probability of mutating a variable
	 */
	public Swap(double probability) {
		super();
		this.probability = probability;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result = parents[0].copy();

		for (int i = 0; i < result.getNumberOfVariables(); i++) {
			Variable variable = result.getVariable(i);

			if ((PRNG.nextDouble() <= probability)
					&& (variable instanceof Permutation)) {
				evolve((Permutation)variable);
			}
		}

		return new Solution[] { result };
	}

	/**
	 * Evolves the specified permutation using the swap mutation operator.
	 * 
	 * @param permutation the permutation to be mutated
	 */
	public static void evolve(Permutation permutation) {
		int i = PRNG.nextInt(permutation.size());
		int j = PRNG.nextInt(permutation.size() - 1);

		if (i == j) {
			j = permutation.size() - 1;
		}

		permutation.swap(i, j);
	}

	@Override
	public int getArity() {
		return 1;
	}

}
