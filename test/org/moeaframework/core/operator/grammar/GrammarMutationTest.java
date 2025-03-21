/* Copyright 2009-2025 David Hadka
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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.AbstractGrammarOperatorTest;
import org.moeaframework.core.variable.Grammar;

public class GrammarMutationTest extends AbstractGrammarOperatorTest<GrammarMutation> {

	@Override
	public GrammarMutation createInstance() {
		return new GrammarMutation(1.0);
	}

	@Test
	public void testProbabilities() {
		testProbability(0.0);
		testProbability(0.2);
		testProbability(1.0);
	}

	/**
	 * Tests if the {@code GrammarMutation} mutates codons with the specified probability.
	 * 
	 * @param probability the expected probability of mutation
	 */
	private void testProbability(double probability) {
		GrammarMutation mutation = new GrammarMutation(probability);
		int count = 0;

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			Solution original = new Solution(1, 0);
			
			Grammar grammar = new Grammar(5);
			grammar.fromArray(new int[] { 0, 1, 2, 3, 4 });
			
			original.setVariable(0, grammar);

			Solution mutated = mutation.evolve(new Solution[] { original })[0];

			count += check((Grammar)original.getVariable(0), (Grammar)mutated.getVariable(0));
		}

		Assert.assertEquals((double)count / (5 * TestThresholds.SAMPLES), probability, TestThresholds.LOW_PRECISION);
	}

	/**
	 * Checks if the two grammars are mutations of one another, returning the number of mutated codons.
	 * 
	 * @param g1 the first grammar
	 * @param g2 the second grammar
	 * @return the number of mutated codons
	 */
	private int check(Grammar g1, Grammar g2) {
		Assert.assertEquals(g1.size(), g2.size());

		int count = 0;
		for (int i = 0; i < g1.size(); i++) {
			if (g1.get(i) != g2.get(i)) {
				count++;
			}
		}

		return count;
	}

}
