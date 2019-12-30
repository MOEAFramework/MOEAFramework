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
package org.moeaframework.util.weights;

import org.junit.Assert;

import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.moeaframework.TestUtils;

/**
 * Abstract class for testing implementations of {@link WeightGenerator}.
 */
public abstract class WeightGeneratorTest {

	/**
	 * Generates weights using the specified sequence and checks if the weights
	 * are valid.
	 * 
	 * @param generator the weight generator to test
	 * @param D the dimension of the generator
	 */
	protected void test(WeightGenerator generator, int D) {
		int size = generator.size();
		List<double[]> weights = generator.generate();

		Assert.assertEquals(size, weights.size());

		checkWeights(weights, D);
	}

	/**
	 * Asserts the weights are in the range {@code [0, 1]} and sum to 1.
	 * 
	 * @param points the samples
	 * @param D the dimension of the samples
	 */
	protected void checkWeights(List<double[]> weights, int D) {
		for (double[] weight : weights) {
			Assert.assertEquals(D, weight.length);

			for (int j = 0; j < D; j++) {
				Assert.assertTrue((weight[j] >= 0.0)
						&& (weight[j] <= 1.0));
			}
			
			TestUtils.assertEquals(1.0, StatUtils.sum(weight));
		}
	}

}
