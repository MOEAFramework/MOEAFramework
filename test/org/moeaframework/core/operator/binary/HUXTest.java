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
package org.moeaframework.core.operator.binary;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.operator.AbstractBinaryOperatorTest;
import org.moeaframework.core.variable.BinaryVariable;

public class HUXTest extends AbstractBinaryOperatorTest<HUX> {
	
	@Override
	public HUX createInstance() {
		return new HUX(1.0);
	}

	@Test
	public void testMatchingBitsUnchanged() {
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			BinaryVariable parent1 = createTestVariable();
			BinaryVariable parent2 = createTestVariable();

			BinaryVariable offspring1 = parent1.copy();
			BinaryVariable offspring2 = parent2.copy();

			createInstance().evolve(offspring1, offspring2);

			for (int j = 0; j < 100; j++) {
				if (parent1.get(j) == parent2.get(j)) {
					Assert.assertEquals(offspring1.get(j), offspring2.get(j));
				}
			}
		}
	}

	@Test
	public void testBitSwapProbability() {
		double sum = 0.0;

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			BinaryVariable parent1 = createTestVariable();
			BinaryVariable parent2 = createTestVariable();

			BinaryVariable offspring1 = parent1.copy();
			BinaryVariable offspring2 = parent2.copy();

			createInstance().evolve(offspring1, offspring2);

			int differingBits = parent1.hammingDistance(parent2);
			int changedBits1 = offspring1.hammingDistance(parent1);
			int changedBits2 = offspring2.hammingDistance(parent2);

			Assert.assertEquals(changedBits1, changedBits2);

			sum += (double)changedBits1 / differingBits;
		}

		Assert.assertEquals(sum / TestThresholds.SAMPLES, 0.5, TestThresholds.VARIATION_EPS);
	}

}
