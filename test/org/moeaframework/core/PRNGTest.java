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
package org.moeaframework.core;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.TestThresholds;

@RunWith(CIRunner.class)
@Retryable
public class PRNGTest {

	private static int N = 1000000;

	@Test
	public void testNextFloat() {
		DescriptiveStatistics statistics = new DescriptiveStatistics();

		for (int i = 0; i < N; i++) {
			statistics.addValue(PRNG.nextFloat());
		}

		Assert.assertUniformDistribution(0.0, 1.0, statistics);
	}

	@Test
	public void testNextFloatRange() {
		DescriptiveStatistics statistics = new DescriptiveStatistics();

		for (int i = 0; i < N; i++) {
			statistics.addValue(PRNG.nextFloat(15.0f, 20.0f));
		}

		Assert.assertUniformDistribution(15.0, 20.0, statistics);
	}

	@Test
	public void testNextDouble() {
		DescriptiveStatistics statistics = new DescriptiveStatistics();

		for (int i = 0; i < N; i++) {
			statistics.addValue(PRNG.nextDouble());
		}

		Assert.assertUniformDistribution(0.0, 1.0, statistics);
	}

	@Test
	public void testNextDoubleRange() {
		DescriptiveStatistics statistics = new DescriptiveStatistics();

		for (int i = 0; i < N; i++) {
			statistics.addValue(PRNG.nextDouble(15.0, 20.0));
		}

		Assert.assertUniformDistribution(15.0, 20.0, statistics);
	}

	@Test
	public void testNextInt() {
		int lessThanEqualZero = 0;
		int greaterThanZero = 0;
		int lessThanHalfNegative = 0;
		int greaterThanHalfPositive = 0;

		for (int i = 0; i < N; i++) {
			int value = PRNG.nextInt();

			if (value <= 0) {
				lessThanEqualZero++;

				if (value < Integer.MIN_VALUE / 2) {
					lessThanHalfNegative++;
				}
			} else {
				greaterThanZero++;

				if (value > Integer.MAX_VALUE / 2) {
					greaterThanHalfPositive++;
				}
			}
		}

		Assert.assertEquals(N / 2.0, lessThanEqualZero, TestThresholds.STATISTICS_EPS * N / 2.0);
		Assert.assertEquals(N / 2.0, greaterThanZero, TestThresholds.STATISTICS_EPS * N / 2.0);
		Assert.assertEquals(N / 4.0, lessThanHalfNegative, TestThresholds.STATISTICS_EPS * N / 4.0);
		Assert.assertEquals(N / 4.0, greaterThanHalfPositive, TestThresholds.STATISTICS_EPS * N / 4.0);
	}

	@Test
	public void testNextIntRange1() {
		DescriptiveStatistics statistics = new DescriptiveStatistics();

		for (int i = 0; i < N; i++) {
			statistics.addValue(PRNG.nextInt(15));
		}

		Assert.assertUniformDistribution(0, 14, statistics);
	}

	@Test
	public void testNextIntRange2() {
		DescriptiveStatistics statistics = new DescriptiveStatistics();

		for (int i = 0; i < N; i++) {
			statistics.addValue(PRNG.nextInt(15, 20));
		}

		Assert.assertUniformDistribution(15, 20, statistics);
	}

	@Test
	public void testNextBoolean() {
		DescriptiveStatistics statistics = new DescriptiveStatistics();

		for (int i = 0; i < N; i++) {
			statistics.addValue(PRNG.nextBoolean() ? 1 : 0);
		}

		Assert.assertUniformDistribution(0, 1, statistics);
	}

	@Test
	public void testNextGaussian() {
		DescriptiveStatistics statistics = new DescriptiveStatistics();

		for (int i = 0; i < N; i++) {
			statistics.addValue(PRNG.nextGaussian());
		}

		testGaussianDistribution(0.0, 1.0, statistics);
	}

	@Test
	public void testNextGaussianParameterized() {
		DescriptiveStatistics statistics = new DescriptiveStatistics();

		for (int i = 0; i < N; i++) {
			statistics.addValue(PRNG.nextGaussian(5.0, 2.0));
		}

		testGaussianDistribution(5.0, 2.0, statistics);
	}

	@Test
	public void testShuffleList() {
		int P = 7;
		List<Integer> list = new ArrayList<Integer>();
		List<DescriptiveStatistics> statistics = new ArrayList<DescriptiveStatistics>();

		for (int i = 0; i < P; i++) {
			list.add(i);
			statistics.add(new DescriptiveStatistics());
		}

		for (int i = 0; i < 50000; i++) {
			PRNG.shuffle(list);

			for (int j = 0; j < P; j++) {
				statistics.get(j).addValue(list.get(j));
			}

			testPermutation(P, list.toArray(Integer[]::new));
		}

		for (int i = 0; i < P; i++) {
			Assert.assertUniformDistribution(0, P - 1, statistics.get(i));
		}
	}

	@Test
	public void testShuffleObjectArray() throws Exception {
		testShuffleArray(Object[].class, 5);
	}

	@Test
	public void testShuffleEmptyObjectArray() throws Exception {
		testShuffleArray(Object[].class, 0);
	}

	@Test
	public void testShuffleIntArray() throws Exception {
		testShuffleArray(int[].class, 5);
	}

	@Test
	public void testShuffleEmptyIntArray() throws Exception {
		testShuffleArray(int[].class, 0);
	}

	@Test
	public void testShuffleDoubleArray() throws Exception {
		testShuffleArray(double[].class, 5);
	}

	@Test
	public void testShuffleEmptyDoubleArray() throws Exception {
		testShuffleArray(double[].class, 0);
	}

	@Test
	public void testShuffleFloatArray() throws Exception {
		testShuffleArray(float[].class, 5);
	}

	@Test
	public void testShuffleEmptyFloatArray() throws Exception {
		testShuffleArray(float[].class, 0);
	}

	@Test
	public void testShuffleLongArray() throws Exception {
		testShuffleArray(long[].class, 5);
	}

	@Test
	public void testShuffleEmptyLongArray() throws Exception {
		testShuffleArray(long[].class, 0);
	}

	@Test
	public void testShuffleShortArray() throws Exception {
		testShuffleArray(short[].class, 5);
	}

	@Test
	public void testShuffleEmptyShortArray() throws Exception {
		testShuffleArray(short[].class, 0);
	}

	@Test
	public void testShuffleByteArray() throws Exception {
		testShuffleArray(byte[].class, 5);
	}

	@Test
	public void testShuffleEmptyByteArray() throws Exception {
		testShuffleArray(byte[].class, 0);
	}

	@Test
	public void testShuffleBooleanArray() {
		int K = 4;
		int P = 2 * K;
		boolean[] array = new boolean[P];
		DescriptiveStatistics[] statistics = new DescriptiveStatistics[P];

		for (int i = 0; i < P; i++) {
			if (i < K) {
				array[i] = true;
			}

			statistics[i] = new DescriptiveStatistics();
		}

		for (int i = 0; i < 50000; i++) {
			PRNG.shuffle(array);

			for (int j = 0; j < P; j++) {
				statistics[j].addValue(array[j] ? 1 : 0);
			}

			testPermutation(K, array);
		}

		for (int i = 0; i < P; i++) {
			Assert.assertUniformDistribution(0, 1, statistics[i]);
		}
	}

	/**
	 * Tests if the {@code shuffle} method produces valid permutations of a typed array, and that the distribution of
	 * the values for each index are approximately uniform.
	 * 
	 * @param type the class of the array
	 * @param size the size of the array
	 */
	private void testShuffleArray(Class<?> type, int size) throws Exception {
		Object array = Array.newInstance(type.getComponentType(), size);
		DescriptiveStatistics[] statistics = new DescriptiveStatistics[size];

		for (int i = 0; i < size; i++) {
			// casts are needed when truncating the int
			if (type.getComponentType() == short.class) {
				Array.set(array, i, (short)i);
			} else if (type.getComponentType() == byte.class) {
				Array.set(array, i, (byte)i);
			} else {
				Array.set(array, i, i);
			}

			statistics[i] = new DescriptiveStatistics();
		}

		for (int i = 0; i < 50000; i++) {
			Method method = PRNG.class.getMethod("shuffle", type);
			method.invoke(null, array);

			Integer[] integerArray = new Integer[size];

			for (int j = 0; j < size; j++) {
				int value = ((Number)Array.get(array, j)).intValue();
				integerArray[j] = value;
				statistics[j].addValue(value);
			}

			testPermutation(size, integerArray);
		}

		for (int i = 0; i < size; i++) {
			Assert.assertUniformDistribution(0, size - 1, statistics[i]);
		}
	}

	/**
	 * Asserts that the statistical distribution satisfies the properties of a Gaussian distribution with the specified
	 * mean and standard deviation.
	 * 
	 * @param mu the mean value of the distribution
	 * @param sigma the standard deviation of the distribution
	 * @param statistics the captures statistics of a sampled distribution
	 */
	private void testGaussianDistribution(double mu, double sigma, DescriptiveStatistics statistics) {
		Assert.assertEquals(mu, statistics.getMean(), TestThresholds.STATISTICS_EPS);
		Assert.assertEquals(sigma * sigma, statistics.getVariance(), TestThresholds.STATISTICS_EPS);
		Assert.assertEquals(0.0, statistics.getSkewness(), TestThresholds.STATISTICS_EPS);
		Assert.assertEquals(0.0, statistics.getKurtosis(), TestThresholds.STATISTICS_EPS);
	}

	/**
	 * Asserts that the array is a valid permutation of {@code n} integers.
	 * 
	 * @param n the size of the permutation
	 * @param array the permutation
	 */
	private void testPermutation(int n, Integer[] array) {
		Assert.assertEquals(n, array.length);

		for (int i = 0; i < n; i++) {
			boolean found = false;

			for (int j = 0; j < array.length; j++) {
				if (array[j] == i) {
					found = true;
					break;
				}
			}

			Assert.assertTrue("Not a valid permutation: " + Arrays.toString(array), found);
		}
	}

	/**
	 * Asserts that the array is a valid permutation of {@code n} boolean values.
	 * 
	 * @param n the number of {@code true} values
	 * @param array the permutation
	 */
	private void testPermutation(int n, boolean[] array) {
		int count = 0;

		for (int i = 0; i < array.length; i++) {
			count += array[i] ? 1 : 0;
		}

		Assert.assertEquals(n, count);
	}

}
