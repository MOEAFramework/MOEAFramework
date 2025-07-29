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
package org.moeaframework.core.operator.permutation;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestEnvironment;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.AbstractPermutationOperatorTest;
import org.moeaframework.core.variable.Permutation;

public class SwapTest extends AbstractPermutationOperatorTest<Swap> {
	
	@Override
	public Swap createInstance() {
		return new Swap(1.0);
	}

	@Test
	public void testProbabilities() {
		testProbability(0.0);
		testProbability(0.2);
		testProbability(1.0);
	}

	/**
	 * Tests if the swap mutation occurs with the specified probability.
	 * 
	 * @param probability the probability
	 */
	private void testProbability(double probability) {
		Swap swap = new Swap(probability);
		int count = 0;

		for (int i = 0; i < TestEnvironment.SAMPLES; i++) {
			Solution original = new Solution(1, 0);
			original.setVariable(0, new Permutation(100));

			Solution mutated = swap.evolve(new Solution[] { original })[0];

			if (testSwap((Permutation)original.getVariable(0), (Permutation)mutated.getVariable(0))) {
				count++;
			}
		}

		Assert.assertEquals(probability, (double)count / TestEnvironment.SAMPLES, TestEnvironment.LOW_PRECISION);
	}

	/**
	 * Returns {@code true} if {@code p2} is the result of a swap from {@code p1}; {@code false} otherwise.  Also checks
	 * if the swap is valid.
	 * 
	 * @param p1 the first permutation
	 * @param p2 the second permutation
	 * @return {@code true} if {@code p2} is the result of a swap from {@code p1}; {@code false} otherwise
	 */
	protected boolean testSwap(Permutation p1, Permutation p2) {
		int index1 = -1;
		int index2 = -1;

		for (int i = 0; i < p1.size(); i++) {
			if (p1.get(i) != p2.get(i)) {
				if (index1 == -1) {
					index1 = i;
				} else if (index2 == -1) {
					index2 = i;
				} else {
					Assert.fail("Permutations differ in more than two indices, which is not a valid swap");
				}
			}
		}

		if ((index1 != -1) && (index2 != -1)) {
			Assert.assertEquals(p1.get(index1), p2.get(index2));
			Assert.assertEquals(p1.get(index2), p2.get(index1));
			return true;
		} else if ((index1 == -1) && (index2 == -1)) {
			return false;
		} else {
			Assert.fail("Permutations differ in only one index, which is not a valid swap");
			return false;
		}
	}

}
