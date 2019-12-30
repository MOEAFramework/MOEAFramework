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
 * Tests the {@link HUX} class.
 */
public class HUXTest {

	/**
	 * Tests if the grammar crossover operator is type-safe.
	 */
	@Test
	public void testTypeSafety() {
		TypeSafetyTest.testTypeSafety(new HUX(1.0));
	}

	/**
	 * Test to ensure matching bits are not modified.
	 */
	@Test
	public void testCorrectBits() {
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			BinaryVariable parent1 = new BinaryVariable(100);
			BinaryVariable parent2 = new BinaryVariable(100);

			BitFlip.evolve(parent1, 0.5);
			BitFlip.evolve(parent2, 0.5);

			BinaryVariable offspring1 = parent1.copy();
			BinaryVariable offspring2 = parent2.copy();

			HUX.evolve(offspring1, offspring2);

			for (int j = 0; j < 100; j++) {
				if (parent1.get(j) == parent2.get(j)) {
					Assert.assertEquals(offspring1.get(j), offspring2.get(j));
				}
			}
		}
	}

	/**
	 * Tests if the correct number of bits are swapped. On average, half of the
	 * differing bits should be swapped.
	 */
	@Test
	public void testSwapProbability() {
		double sum = 0.0;

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			BinaryVariable parent1 = new BinaryVariable(100);
			BinaryVariable parent2 = new BinaryVariable(100);

			BitFlip.evolve(parent1, 0.5);
			BitFlip.evolve(parent2, 0.5);

			BinaryVariable offspring1 = parent1.copy();
			BinaryVariable offspring2 = parent2.copy();

			HUX.evolve(offspring1, offspring2);

			int differingBits = parent1.hammingDistance(parent2);
			int changedBits1 = offspring1.hammingDistance(parent1);
			int changedBits2 = offspring2.hammingDistance(parent2);

			Assert.assertEquals(changedBits1, changedBits2);

			sum += (double)changedBits1 / differingBits;
		}

		Assert.assertEquals(sum / TestThresholds.SAMPLES, 0.5,
				TestThresholds.VARIATION_EPS);
	}

	/**
	 * Tests if the parents remain unchanged during variation.
	 */
	@Test
	public void testParentImmutability() {
		HUX hux = new HUX(1.0);

		BinaryVariable bv1 = new BinaryVariable(100);
		BinaryVariable bv2 = new BinaryVariable(100);

		BitFlip.evolve(bv1, 0.5);
		BitFlip.evolve(bv2, 0.5);

		Solution s1 = new Solution(1, 0);
		s1.setVariable(0, bv1);

		Solution s2 = new Solution(1, 0);
		s2.setVariable(0, bv2);

		Solution[] parents = new Solution[] { s1, s2 };

		ParentImmutabilityTest.test(parents, hux);
	}

}
