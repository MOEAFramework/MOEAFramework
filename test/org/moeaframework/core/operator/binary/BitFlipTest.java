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
package org.moeaframework.core.operator.binary;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.ParentImmutabilityTest;
import org.moeaframework.core.operator.TypeSafetyTest;
import org.moeaframework.core.variable.BinaryVariable;

/**
 * Tests the {@link BitFlip} class.
 */
public class BitFlipTest {

	/**
	 * Tests if the grammar crossover operator is type-safe.
	 */
	@Test
	public void testTypeSafety() {
		TypeSafetyTest.testTypeSafety(new BitFlip(1.0));
	}

	/**
	 * Tests if the correct number of bits are flipped for several probability
	 * values.
	 */
	@Test
	public void testProbabilities() {
		testProbability(0.0);
		testProbability(1.0 / 100.0);
		testProbability(0.5);
		testProbability(1.0);
	}

	/**
	 * Tests if the correct number of bits are flipped given the specified
	 * probability.
	 * 
	 * @param probability the probability of flipping a bit
	 */
	private void testProbability(double probability) {
		double sum = 0.0;

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			BinaryVariable original = new BinaryVariable(100);
			BinaryVariable mutated = original.copy();
			BitFlip.evolve(mutated, probability);

			sum += original.hammingDistance(mutated);
		}

		Assert.assertEquals((sum / TestThresholds.SAMPLES) / 100, probability,
				TestThresholds.VARIATION_EPS);
	}

	/**
	 * Tests if the parents remain unchanged during variation.
	 */
	@Test
	public void testParentImmutability() {
		BitFlip bf = new BitFlip(1.0);

		Solution solution = new Solution(1, 0);
		solution.setVariable(0, new BinaryVariable(100));

		Solution[] parents = new Solution[] { solution };

		ParentImmutabilityTest.test(parents, bf);
	}

}
