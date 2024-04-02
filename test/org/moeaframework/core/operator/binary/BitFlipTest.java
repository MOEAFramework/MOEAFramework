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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.operator.AbstractBinaryOperatorTest;
import org.moeaframework.core.variable.BinaryVariable;

public class BitFlipTest extends AbstractBinaryOperatorTest<BitFlip> {

	@Override
	public BitFlip createInstance() {
		return new BitFlip(1.0);
	}

	@Test
	public void testProbabilities() {
		testProbability(0.0);
		testProbability(1.0 / 100.0);
		testProbability(0.5);
		testProbability(1.0);
	}

	/**
	 * Tests if the correct number of bits are flipped given the specified probability.
	 * 
	 * @param probability the probability of flipping a bit
	 */
	private void testProbability(double probability) {
		double sum = 0.0;

		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			BinaryVariable original = new BinaryVariable(100);
			BinaryVariable mutated = original.copy();
			BitFlip.mutate(mutated, probability);

			sum += original.hammingDistance(mutated);
		}

		Assert.assertEquals((sum / TestThresholds.SAMPLES) / 100, probability, TestThresholds.LOW_PRECISION);
	}

}
