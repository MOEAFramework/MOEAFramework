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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.RetryOnTravis;
import org.moeaframework.TestThresholds;
import org.moeaframework.TravisRunner;

/**
 * Tests the {@link Saltelli} class.
 */
@RunWith(TravisRunner.class)
@RetryOnTravis
public class SaltelliTest extends SequenceTest {

	/**
	 * Tests to ensure the sequence exhibits a uniform distribution in the
	 * range {@code [0, 1]}.
	 */
	@Test
	public void test() {
		test(new Saltelli());
	}

	@Override
	protected void test(Sequence sequence, int D) {
		int N = (2 * D + 2) * (TestThresholds.SAMPLES / 10);

		double[][] points = sequence.generate(N, D);

		Assert.assertEquals(N, points.length);

		checkRange(points, D);
		checkStatistics(points, D);
		checkSaltelli(points, D);
	}

	/**
	 * Tests if an exception is thrown when requesting a Saltelli sequence with
	 * an invalid size.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSize() {
		Saltelli sequence = new Saltelli();

		sequence.generate(7, 2);
	}

	/**
	 * Tests if the sequence appears to be a Saltelli sequence.
	 * 
	 * @param sequence the sequence to test
	 * @param D the dimensionality
	 */
	protected void checkSaltelli(double[][] sequence, int D) {
		for (int i = 0; i < sequence.length; i++) {
			int index = (2 * D + 2) * (i / (2 * D + 2));

			for (int j = 0; j < D; j++) {
				Assert.assertTrue((sequence[i][j] == sequence[index][j])
						|| (sequence[i][j] == sequence[index + 2 * D + 1][j]));
			}
		}
	}

}
