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
import org.moeaframework.core.configuration.Prefix;
import org.moeaframework.core.operator.TypeSafeCrossover;
import org.moeaframework.core.variable.Grammar;

/**
 * Single-point crossover for grammars. A crossover point is selected in both parents with the tail portions swapped.
 * <p>
 * This variation operator is type-safe.
 */
@Prefix("gx")
public class GrammarCrossover extends TypeSafeCrossover<Grammar> {
	
	/**
	 * Constructs a new grammar crossover operator with 100% probability of being applied to each solution.
	 */
	public GrammarCrossover() {
		this(1.0);
	}

	/**
	 * Constructs a single-point crossover operator for grammars with the specified probability of applying this
	 * operator to each grammar variable.
	 * 
	 * @param probability the probability of applying this operator to each grammar variable
	 */
	public GrammarCrossover(double probability) {
		super(Grammar.class, probability);
	}
	
	@Override
	public String getName() {
		return "gx";
	}

	/**
	 * Performs single-point crossover on the specified grammars. Crossover points are chosen for both parents and
	 * the tail sections swapped.  The two grammars are modified as a result of this operation.
	 * 
	 * @param g1 the first grammar
	 * @param g2 the second grammar
	 */
	public void evolve(Grammar g1, Grammar g2) {
		int pos1 = PRNG.nextInt(g1.size() - 1) + 1;
		int pos2 = PRNG.nextInt(g2.size() - 1) + 1;

		int[] removed1 = g1.cut(pos1, g1.size() - 1);
		int[] removed2 = g2.cut(pos2, g2.size() - 1);

		g1.insert(pos1, removed2);
		g2.insert(pos2, removed1);
	}

}
