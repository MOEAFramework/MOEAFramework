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
package org.moeaframework.core.operator.grammar;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.configuration.Prefix;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.core.variable.Grammar;

/**
 * Uniform mutation for grammars. Each integer codon in the grammar representation is uniformly mutated with a
 * specified probability.
 * <p>
 * This variation operator is type-safe.
 */
@Prefix("gm")
public class GrammarMutation implements Mutation {

	/**
	 * The probability of mutating each integer codon in the grammar representation.
	 */
	private double probability;
	
	/**
	 * Constructs a uniform mutation operators for grammars.
	 */
	public GrammarMutation() {
		this(1.0);
	}

	/**
	 * Constructs a uniform mutation operator for grammars with the specified probability of mutating each integer
	 * codon in the grammar representation.
	 * 
	 * @param probability the probability of mutating each integer codon in the grammar representation
	 */
	public GrammarMutation(double probability) {
		super();
		setProbability(probability);
	}
	
	@Override
	public String getName() {
		return "gm";
	}

	/**
	 * Returns the probability of mutating each integer codon in the grammar representation.
	 * 
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * Sets  the probability of mutating each integer codon in the grammar representation.
	 * 
	 * @param probability the probability (0.0 - 1.0)
	 */
	public void setProbability(double probability) {
		Validate.probability("probability", probability);
		this.probability = probability;
	}

	@Override
	public Solution mutate(Solution parent) {
		Solution result = parent.copy();

		for (int i = 0; i < result.getNumberOfVariables(); i++) {
			Variable variable = result.getVariable(i);

			if (variable instanceof Grammar grammar) {
				mutate(grammar, probability);
			}
		}

		return result;
	}

	/**
	 * Performs uniform mutation on the specified grammar. Each integer codon in the grammar representation is
	 * uniformly mutated with the specified probability in the range {@code [0, getMaximumValue()-1]}.
	 * 
	 * @param grammar the grammar to mutate
	 * @param probability the probability of mutating each integer codon in the grammar representation
	 */
	public static void mutate(Grammar grammar, double probability) {
		for (int i = 0; i < grammar.size(); i++) {
			if (PRNG.nextDouble() <= probability) {
				grammar.set(i, PRNG.nextInt(grammar.getMaximumValue()));
			}
		}
	}
}
