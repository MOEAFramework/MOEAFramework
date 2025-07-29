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
package org.moeaframework.util.sequence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.TestEnvironment;

@RunWith(CIRunner.class)
@Retryable
public class SaltelliTest extends AbstractSequenceTest<Saltelli> {
	
	@Override
	public Saltelli createInstance() {
		return new Saltelli();
	}

	@Override
	protected void test(Sequence sequence, int D) {
		int N = (2 * D + 2) * (TestEnvironment.SAMPLES / 10);

		double[][] points = sequence.generate(N, D);

		Assert.assertEquals(N, points.length);

		checkRange(points, D);
		checkStatistics(points, D);
		checkSaltelli(points, D);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidSize() {
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
