/* Copyright 2009-2019 David Hadka
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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.ParentImmutabilityTest;
import org.moeaframework.core.operator.TypeSafetyTest;
import org.moeaframework.core.variable.Grammar;

/**
 * Tests the {@link GrammarCrossover} class.
 */
public class GrammarCrossoverTest {

	/**
	 * Tests if the grammar crossover operator is type-safe.
	 */
	@Test
	public void testTypeSafety() {
		TypeSafetyTest.testTypeSafety(new GrammarCrossover(1.0));
	}

	/**
	 * Tests various probabilities to ensure the {@code GrammarCrossover}
	 * operates with the expected probability.
	 */
	@Test
	public void testProbabilities() {
		testProbability(0.0);
		testProbability(0.2);
		testProbability(1.0);
	}

	/**
	 * Tests if the {@code GrammarCrossover} operates with the specified
	 * probability.
	 * 
	 * @param probability the expected probability of crossover
	 */
	private void testProbability(double probability) {
		GrammarCrossover crossover = new GrammarCrossover(probability);
		int count = 0;

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			Solution p1 = new Solution(1, 0);
			p1.setVariable(0, new Grammar(new int[] { 0, 1, 2, 3, 4 }));

			Solution p2 = new Solution(1, 0);
			p2.setVariable(0, new Grammar(new int[] { 5, 6, 7 }));

			Solution[] offspring = crossover.evolve(new Solution[] { p1, p2 });

			if (check((Grammar)p1.getVariable(0), (Grammar)p2.getVariable(0),
					(Grammar)offspring[0].getVariable(0), (Grammar)offspring[1]
							.getVariable(0))) {
				count++;
			}
		}

		Assert.assertEquals((double)count / TestThresholds.SAMPLES,
				probability, TestThresholds.VARIATION_EPS);
	}

	/**
	 * Checks if the two offspring grammars, {@code o1} and {@code o2}, are the
	 * result of single-point crossover between the two parent grammars,
	 * {@code p1} and {@code p2}.
	 * 
	 * @param p1 the first parent grammar
	 * @param p2 the second parent grammar
	 * @param o1 the first offspring grammar
	 * @param o2 the second offspring grammar
	 * @return {@code true} if the two offspring are the result of single-point
	 *         crossover between the two parents; {@code false} otherwise
	 */
	private boolean check(Grammar p1, Grammar p2, Grammar o1, Grammar o2) {
		Assert.assertEquals(p1.size() + p2.size(), o1.size() + o2.size());

		int pos1 = -1;
		int pos2 = -1;
		boolean crossed = false;

		// find crossover point of first parent
		for (pos1 = 0; pos1 < p1.size(); pos1++) {
			if (p1.get(pos1) != o1.get(pos1)) {
				crossed = true;
				break;
			}
		}

		// find crossover point of second parent
		for (pos2 = 0; pos2 < p2.size(); pos2++) {
			if (p2.get(pos2) != o2.get(pos2)) {
				crossed = true;
				break;
			}
		}

		// ensure remaining codons in first offspring are from second parent
		for (int i = pos1; i < o1.size(); i++) {
			Assert.assertEquals(p2.get(pos2 + i - pos1), o1.get(i));
		}

		// ensure remaining codons in second offspring are from first parent
		for (int i = pos2; i < o2.size(); i++) {
			Assert.assertEquals(p1.get(pos1 + i - pos2), o2.get(i));
		}

		return crossed;
	}

	/**
	 * Tests if the parents remain unchanged during variation.
	 */
	@Test
	public void testParentImmutability() {
		GrammarCrossover gc = new GrammarCrossover(1.0);

		Solution s1 = new Solution(1, 0);
		s1.setVariable(0, new Grammar(new int[] { 0, 1, 2, 3, 4 }));

		Solution s2 = new Solution(1, 0);
		s2.setVariable(0, new Grammar(new int[] { 5, 6, 7 }));

		Solution[] parents = new Solution[] { s1, s2 };

		ParentImmutabilityTest.test(parents, gc);
	}

}
