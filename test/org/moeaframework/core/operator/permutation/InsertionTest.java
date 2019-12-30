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
package org.moeaframework.core.operator.permutation;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.ParentImmutabilityTest;
import org.moeaframework.core.operator.TypeSafetyTest;
import org.moeaframework.core.variable.Permutation;

/**
 * Tests for {@link Insertion} mutation.
 */
public class InsertionTest {

	/**
	 * Tests if the insertion mutation operator is type-safe.
	 */
	@Test
	public void testTypeSafety() {
		TypeSafetyTest.testTypeSafety(new Insertion(1.0));
	}

	/**
	 * Tests various probabilities to ensure insertion mutation is applied the
	 * correct number of times on average.
	 */
	@Test
	public void testProbabilities() {
		testProbability(0.0);
		testProbability(0.2);
		testProbability(1.0);
	}

	/**
	 * Tests if the insertion mutation occurs with the specified probability.
	 * 
	 * @param probability the probability
	 */
	private void testProbability(double probability) {
		Insertion insertion = new Insertion(probability);
		int count = 0;

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			Solution original = new Solution(1, 0);
			original.setVariable(0, new Permutation(100));

			Solution mutated = insertion.evolve(new Solution[] { original })[0];

			if (testInsertion((Permutation)original.getVariable(0),
					(Permutation)mutated.getVariable(0))) {
				count++;
			}
		}

		Assert.assertEquals((double)count / TestThresholds.SAMPLES,
				probability, TestThresholds.VARIATION_EPS);
	}

	/**
	 * Returns {@code true} if the result is a valid insertion; {@code false}
	 * otherwise.
	 * 
	 * @param v1 the first permutation
	 * @param v2 the second permutation
	 * @return {@code true} if the result is a valid insertion; {@code false}
	 *         otherwise
	 */
	protected boolean testInsertion(Permutation v1, Permutation v2) {
		// find first index where permutations differ
		int index1 = -1;
		int index2 = -1;

		for (int i = 0; i < v1.size(); i++) {
			if (v1.get(i) != v2.get(i)) {
				index1 = i;
				break;
			}
		}

		if (index1 == -1) {
			return false;
		}

		boolean removed = false;
		if (v2.get(index1) == v1.get(index1 + 1)) {
			removed = true;
		}

		// then ensure the shifted elements are correct
		for (int i = index1; i < v1.size(); i++) {
			if (removed) {
				if (v2.get(i) == v1.get(index1)) {
					index2 = i;
					break;
				} else {
					Assert.assertEquals(v2.get(i), v1.get(i + 1));
				}
			} else {
				if (v1.get(i) == v2.get(index1)) {
					index2 = i;
					break;
				} else {
					Assert.assertEquals(v2.get(i + 1), v1.get(i));
				}
			}
		}

		// ensure elements after insertion match
		for (int i = index2 + 1; i < v1.size(); i++) {
			Assert.assertEquals(v1.get(i), v2.get(i));
		}

		return true;
	}

	/**
	 * Tests if the parents remain unchanged during variation.
	 */
	@Test
	public void testParentImmutability() {
		Insertion ins = new Insertion(1.0);

		Solution solution = new Solution(1, 0);
		solution.setVariable(0, new Permutation(100));

		Solution[] parents = new Solution[] { solution };

		ParentImmutabilityTest.test(parents, ins);
	}

}
