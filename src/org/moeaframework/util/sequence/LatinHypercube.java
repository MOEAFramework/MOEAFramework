/* Copyright 2009-2018 David Hadka
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

import org.moeaframework.core.PRNG;

/**
 * Generates sequences using Latin hypercube sampling (LHS). Each axis is
 * divided into {@code N} stripes and exactly one point may exist in each
 * stripe.
 * <p>
 * References:
 * <ol>
 * <li>McKay M.D., Beckman, R.J., and Conover W.J. "A Comparison of Three
 * Methods for Selecting Values of Input Variables in the Analysis of Output
 * from a Computer Code." Technometrics, 21(2):239-245, 1979.
 * </ol>
 */
public class LatinHypercube implements Sequence {

	/**
	 * Constructs a Latin hypercube sequence generator.
	 */
	public LatinHypercube() {
		super();
	}

	@Override
	public double[][] generate(int N, int D) {
		double[][] result = new double[N][D];
		double[] temp = new double[N];
		double d = 1.0 / N;

		for (int i = 0; i < D; i++) {
			for (int j = 0; j < N; j++) {
				temp[j] = PRNG.nextDouble(j * d, (j + 1) * d);
			}

			PRNG.shuffle(temp);

			for (int j = 0; j < N; j++) {
				result[j][i] = temp[j];
			}
		}

		return result;
	}

}
