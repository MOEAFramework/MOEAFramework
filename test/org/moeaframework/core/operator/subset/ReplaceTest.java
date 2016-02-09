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

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.ParentImmutabilityTest;
import org.moeaframework.core.operator.TypeSafetyTest;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.Subset;

/**
 * Tests for {@link Replace} mutation.
 */
public class ReplaceTest {

	/**
	 * Tests if the replace crossover operator is type-safe.
	 */
	@Test
	public void testTypeSafety() {
		TypeSafetyTest.testTypeSafety(new Replace(1.0));
	}

	/**
	 * Tests various probabilities to ensure replace mutation is applied the
	 * correct number of times on average.
	 */
	@Test
	public void testProbabilities() {
		testProbability(0.0);
		testProbability(0.2);
		testProbability(1.0);
	}

	/**
	 * Tests if the replace mutation occurs with the specified probability.
	 * 
	 * @param probability the probability
	 */
	private void testProbability(double probability) {
		Replace replace = new Replace(probability);
		int count = 0;

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			Solution original = new Solution(1, 0);
			original.setVariable(0, new Subset(50, 100));

			Solution mutated = replace.evolve(new Solution[] { original })[0];

			if (testSubset((Subset)original.getVariable(0),
					(Subset)mutated.getVariable(0))) {
				count++;
			}
		}

		Assert.assertEquals((double)count / TestThresholds.SAMPLES,
				probability, TestThresholds.VARIATION_EPS);
	}

	/**
	 * Returns {@code true} if the result is a valid replacement; {@code false}
	 * otherwise.
	 * 
	 * @param v1 the first permutation
	 * @param v2 the second permutation
	 * @return {@code true} if the result is a valid replacement; {@code false}
	 *         otherwise
	 */
	protected boolean testSubset(Subset v1, Subset v2) {
		Set<Integer> set = new HashSet<Integer>(v1.getSet());
		set.removeAll(v2.getSet());
		return set.size() == 1;
	}

	/**
	 * Tests if the parents remain unchanged during variation.
	 */
	@Test
	public void testParentImmutability() {
		Replace ins = new Replace(1.0);

		Solution solution = new Solution(1, 0);
		solution.setVariable(0, new Permutation(100));

		Solution[] parents = new Solution[] { solution };

		ParentImmutabilityTest.test(parents, ins);
	}

}
