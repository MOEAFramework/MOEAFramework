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
package org.moeaframework.util.weights;

import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.Assert;

/**
 * Abstract class for testing implementations of {@link WeightGenerator}.
 */
@Ignore("Abstract test class")
public abstract class AbstractWeightGeneratorTest<T extends WeightGenerator> {
	
	protected static final int SAMPLES = 100;
	
	/**
	 * Returns a new instance of the weight generator being tested.
	 * 
	 * @param numberOfObjectives the number of objectives / dimension of each sample
	 * @return the weight generator
	 */
	public abstract T createInstance(int numberOfObjectives);
	
	/**
	 * Returns the expected number of samples that will be generated.
	 * 
	 * @param numberOfObjectives the number of objectives / dimension of each sample
	 * @return the expected number of samples
	 */
	public abstract int getExpectedNumberOfSamples(int numberOfObjectives);

	@Test
	public void test2D() {
		test(createInstance(2), 2);
	}
	
	@Test
	public void test3D() {
		test(createInstance(3), 3);
	}
	
	@Test
	public void test5D() {
		test(createInstance(5), 5);
	}
	
	@Test
	public void test10D() {
		test(createInstance(10), 10);
	}

	/**
	 * Generates weights using the specified sequence and checks if the weights are valid.
	 * 
	 * @param generator the weight generator to test
	 * @param D the dimension of the generator
	 */
	protected void test(WeightGenerator generator, int D) {
		int expectedSize = getExpectedNumberOfSamples(D);
		List<double[]> weights = generator.generate();

		Assert.assertEquals(expectedSize, generator.size());
		Assert.assertEquals(expectedSize, weights.size());

		checkBounds(weights, D);
	}

	/**
	 * Asserts the weights are in the range {@code [0, 1]} and sum to 1.
	 * 
	 * @param points the samples
	 * @param D the dimension of the samples
	 */
	protected void checkBounds(List<double[]> weights, int D) {
		for (double[] weight : weights) {
			Assert.assertEquals(D, weight.length);

			for (int j = 0; j < D; j++) {
				Assert.assertBetween(0.0, 1.0, weight[j]);
			}
			
			Assert.assertEquals(1.0, StatUtils.sum(weight));
		}
	}

}
