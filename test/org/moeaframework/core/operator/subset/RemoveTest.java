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

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.ParentImmutabilityTest;
import org.moeaframework.core.operator.TypeSafetyTest;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.Subset;

/**
 * Tests for {@link Remove} mutation.
 */
public class RemoveTest {

	/**
	 * Tests if the remove mutation operator is type-safe.
	 */
	@Test
	public void testTypeSafety() {
		TypeSafetyTest.testTypeSafety(new Remove(1.0));
	}

	/**
	 * Tests various probabilities to ensure remove mutation is applied the
	 * correct number of times on average.
	 */
	@Test
	public void testProbabilities() {
		testProbability(0.0);
		testProbability(0.2);
		testProbability(1.0);
	}

	/**
	 * Tests if the remove mutation occurs with the specified probability.
	 * 
	 * @param probability the probability
	 */
	private void testProbability(double probability) {
		Remove remove = new Remove(probability);
		int count = 0;
		int skipped = 0;

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			int n = PRNG.nextInt(1, 20);
			int l = PRNG.nextInt(0, n);
			int u = PRNG.nextInt(l, n);
			
			Subset subset = new Subset(l, u, n);
			subset.randomize();
			
			Solution original = new Solution(1, 0);
			original.setVariable(0, subset);

			Solution mutated = remove.evolve(new Solution[] { original })[0];
			Subset newSubset = (Subset)mutated.getVariable(0);
			
			if ((subset.size() == newSubset.size()) && (subset.size() == subset.getL())) {
				skipped++;
			} else if (testSubset(subset, newSubset)) {
				count++;
			}
		}

		Assert.assertEquals(probability, (double)count / (TestThresholds.SAMPLES - skipped),
				TestThresholds.VARIATION_EPS);
	}

	/**
	 * Returns {@code true} if the result is a valid add; {@code false}
	 * otherwise.
	 * 
	 * @param v1 the first subset
	 * @param v2 the second subset
	 * @return {@code true} if the result is a valid add; {@code false}
	 *         otherwise
	 */
	protected boolean testSubset(Subset v1, Subset v2) {
		Set<Integer> set = v1.getSet();
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
