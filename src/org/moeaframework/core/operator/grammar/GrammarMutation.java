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
package org.moeaframework.core.operator.grammar;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.Grammar;

/**
 * Uniform mutation for grammars. Each integer codon in the grammar
 * representation is uniformly mutated with a specified probability.
 * <p>
 * This variation operator is type-safe.
 */
public class GrammarMutation implements Variation {

	/**
	 * The probability of mutating each integer codon in the grammar
	 * representation.
	 */
	private final double probability;

	/**
	 * Constructs a uniform mutation operator for grammars with the specified
	 * probability of mutating each integer codon in the grammar representation.
	 * 
	 * @param probability the probability of mutating each integer codon in the
	 *        grammar representation
	 */
	public GrammarMutation(double probability) {
		super();
		this.probability = probability;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result1 = parents[0].copy();

		for (int i = 0; i < result1.getNumberOfVariables(); i++) {
			Variable variable1 = result1.getVariable(i);

			if (variable1 instanceof Grammar) {
				evolve((Grammar)variable1, probability);
			}
		}

		return new Solution[] { result1 };
	}

	/**
	 * Performs uniform mutation on the specified grammar. Each integer codon in
	 * the grammar representation is uniformly mutated with the specified
	 * probability in the range {@code [0, getMaximumValue()-1]}.
	 * 
	 * @param grammar the grammar to mutate
	 * @param probability the probability of mutating each integer codon in the
	 *        grammar representation
	 */
	public static void evolve(Grammar grammar, double probability) {
		for (int i = 0; i < grammar.size(); i++) {
			if (PRNG.nextDouble() <= probability) {
				grammar.set(i, PRNG.nextInt(grammar.getMaximumValue()));
			}
		}
	}

	@Override
	public int getArity() {
		return 1;
	}

}
