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
package org.moeaframework.core.operator.subset;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.Subset;

/**
 * Remove mutation operator.  Randomly removes one member from the subset.
 * <p>
 * This operator is type-safe.
 */
public class Remove implements Variation {

	/**
	 * The probability of mutating a variable.
	 */
	private final double probability;

	/**
	 * Constructs a remove mutation operator with the specified
	 * probability of mutating a variable.
	 * 
	 * @param probability the probability of mutating a variable
	 */
	public Remove(double probability) {
		super();
		this.probability = probability;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result = parents[0].copy();

		for (int i = 0; i < result.getNumberOfVariables(); i++) {
			Variable variable = result.getVariable(i);

			if ((PRNG.nextDouble() <= probability)
					&& (variable instanceof Subset)) {
				evolve((Subset)variable);
			}
		}

		return new Solution[] { result };
	}

	/**
	 * Evolves the specified subset using the remove mutation operator.
	 * 
	 * @param subset the subset to be mutated
	 */
	public static void evolve(Subset subset) {
		if (subset.size() > subset.getL()) {
			subset.remove(subset.randomMember());
		}
	}

	@Override
	public int getArity() {
		return 1;
	}

}
