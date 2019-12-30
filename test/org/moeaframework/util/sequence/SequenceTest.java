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
package org.moeaframework.util.sequence;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.moeaframework.RetryOnTravis;
import org.moeaframework.TestThresholds;
import org.moeaframework.TravisRunner;

/**
 * Abstract class for testing implementation of {@link Sequence}.
 */
@RunWith(TravisRunner.class)
@RetryOnTravis
public abstract class SequenceTest {

	/**
	 * Tests the specified sequence for 1, 2 and 10 dimensions.
	 * 
	 * @param sequence the sequence to test
	 */
	protected void test(Sequence sequence) {
		test(sequence, 1);
		test(sequence, 2);
		test(sequence, 10);
	}

	/**
	 * Generates samples using the specified sequence and checks if the samples
	 * are uniformly distributed in the range {@code [0, 1]}.
	 * 
	 * @param sequence the sequence to test
	 * @param D the dimension of the samples
	 */
	protected void test(Sequence sequence, int D) {
		double[][] points = sequence.generate(TestThresholds.SAMPLES, D);

		Assert.assertEquals(TestThresholds.SAMPLES, points.length);

		checkRange(points, D);
		checkStatistics(points, D);
	}

	/**
	 * Asserts the samples are in the range {@code [0, 1]}.
	 * 
	 * @param points the samples
	 * @param D the dimension of the samples
	 */
	protected void checkRange(double[][] points, int D) {
		for (int i = 0; i < points.length; i++) {
			Assert.assertEquals(D, points[i].length);

			for (int j = 0; j < D; j++) {
				Assert.assertTrue((points[i][j] >= 0.0)
						&& (points[i][j] <= 1.0));
			}
		}
	}

	/**
	 * Asserts the samples are uniformly distributed.
	 * 
	 * @param points the samples
	 * @param D the dimension of the samples
	 */
	protected void checkStatistics(double[][] points, int D) {
		for (int i = 0; i < D; i++) {
			DescriptiveStatistics statistics = new DescriptiveStatistics();

			for (int j = 0; j < points.length; j++) {
				statistics.addValue(points[j][i]);
			}

			testUniformDistribution(0.0, 1.0, statistics);
		}
	}

	/**
	 * Asserts that the collected statistics appear to be from a uniform
	 * distribution.
	 * 
	 * @param min the minimum value
	 * @param max the maximum value
	 * @param statistics the collected statistics
	 */
	public void testUniformDistribution(double min, double max,
			DescriptiveStatistics statistics) {
		Assert.assertEquals((min + max) / 2.0, statistics.getMean(),
				TestThresholds.STATISTICS_EPS);
		Assert.assertEquals(Math.pow(max - min, 2.0) / 12.0, statistics
				.getVariance(), TestThresholds.STATISTICS_EPS);
		Assert.assertEquals(0.0, statistics.getSkewness(),
				TestThresholds.STATISTICS_EPS);
		Assert.assertEquals(-6.0 / 5.0, statistics.getKurtosis(),
				TestThresholds.STATISTICS_EPS);
		Assert.assertEquals(min, statistics.getMin(),
				TestThresholds.STATISTICS_EPS);
		Assert.assertEquals(max, statistics.getMax(),
				TestThresholds.STATISTICS_EPS);
	}

}
