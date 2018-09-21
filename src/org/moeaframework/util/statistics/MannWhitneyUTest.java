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
package org.moeaframework.util.statistics;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.moeaframework.core.Settings;

/**
 * The Mann-Whitney U test determines if two populations have different medians.
 * <p>
 * <ul>
 * <li>Null Hypothesis: The two populations have the same medians.
 * <li>Alternative Hypothesis: The two populations have different medians.
 * </ul>
 * <p>
 * Assumptions:
 * <ol>
 * <li>Samples are randomly selected from their corresponding populations
 * <li>Samples are independent
 * <li>The dependent variable (value being sampled) is continuous
 * <li>The underlying distributions of the populations are identical in shape
 * </ol>
 * <p>
 * References:
 * <ol>
 * <li>Sheskin, D.J. "Handbook of Parametric and Nonparametric Statistical
 * Procedures, Third Edition." Chapman & Hall/CRC. 2004.
 * </ol>
 */
public class MannWhitneyUTest extends OrdinalStatisticalTest {

	/**
	 * The value of {@code U} from the last invocation of {@link #test}. This
	 * is package private and intended only for testing.
	 */
	double lastU;

	/**
	 * Constructs a Mann-Whitney U test.
	 */
	public MannWhitneyUTest() {
		super(2);
	}

	// make method public
	@Override
	public void add(double value, int group) {
		super.add(value, group);
	}
	
	// make method public
	@Override
	public void addAll(double[] values, int group) {
		super.addAll(values, group);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * When the samples from both populations are less than 20, only alpha
	 * values of 0.05 and 0.01 are valid. This is because a table is used to
	 * accurately determine the critical values. When more than 20 samples are
	 * available, the normal approximation is used allowing any value for alpha.
	 * 
	 * @throws IllegalArgumentException if an insufficient sampling size is
	 *         provided, or if an invalid alpha value is provided
	 */
	@Override
	public boolean test(double alpha) {
		double[] R = new double[2];
		int[] n = new int[2];

		update();

		for (RankedObservation observation : data) {
			n[observation.getGroup()]++;
			R[observation.getGroup()] += observation.getRank();
		}

		double U1 = n[0] * n[1] + n[0] * (n[0] + 1) / 2.0 - R[0];
		double U2 = n[0] * n[1] + n[1] * (n[1] + 1) / 2.0 - R[1];
		double U = Math.min(U1, U2);

		// expose U for testing
		lastU = U;

		if ((n[0] <= 20) && (n[1] <= 20)) {
			return U <= getCriticalUValueFromTable(n[0], n[1], alpha);
		} else {
			double z = 0.0;
			NormalDistribution dist = new NormalDistribution();

			if (Settings.isContinuityCorrection()) {
				z = (Math.abs(U - n[0] * n[1] / 2.0) - 0.5)
						/ Math.sqrt(n[0] * n[1] * (n[0] + n[1] + 1.0) / 12.0);
			} else {
				z = (U - n[0] * n[1] / 2.0)
						/ Math.sqrt(n[0] * n[1] * (n[0] + n[1] + 1.0) / 12.0);
			}

			return Math.abs(z) >= Math.abs(
					dist.inverseCumulativeProbability(alpha));
		}
	}

	/**
	 * Returns the critical U value from the lookup tables.
	 * 
	 * @param n1 the number of samples from the first group
	 * @param n2 the number of samples from the second group
	 * @param alpha the prespecified level of confidence; only values of 0.05
	 *        and 0.01 are permitted
	 * @return the critical U value from the lookup tables
	 * @throws IllegalArgumentException if an insufficient sampling size is
	 *         provided, or if an invalid alpha value is provided
	 */
	private static int getCriticalUValueFromTable(int n1, int n2, double alpha) {
		if ((n1 < 1) || (n1 > 20) || (n2 < 1) || (n2 > 20)) {
			throw new IllegalArgumentException(
					"only valid for 1 <= n1 <= 20, 1 <= n2 <= 20");
		}

		int value = -1;

		// 1 <= n1, n2 <= 20, so this subtracts 1 from each to get the 0-based
		// index
		if (alpha == 0.05) {
			value = TABLE_5[20 * n1 + n2 - 21];
		} else if (alpha == 0.01) {
			value = TABLE_1[20 * n1 + n2 - 21];
		} else {
			throw new IllegalArgumentException("only valid for 0.05 or 0.01");
		}

		if (value == -1) {
			throw new IllegalArgumentException("insufficient sampling size");
		}

		return value;
	}

	/**
	 * Table of critical U values for alpha=0.05.  Entries of -1 indicate an
	 * insufficient sampling size.
	 */
	private static final int[] TABLE_5 = new int[] { -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, -1, -1, -1,
			-1, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, -1, -1, -1, 0,
			1, 2, 3, 4, 4, 5, 6, 7, 8, 9, 10, 11, 11, 12, 13, 13, -1, -1, 0, 1,
			2, 3, 5, 6, 7, 8, 9, 11, 12, 13, 14, 15, 17, 18, 19, 20, -1, -1, 1,
			2, 3, 5, 6, 8, 10, 11, 13, 14, 16, 17, 19, 21, 22, 24, 25, 27, -1,
			-1, 1, 3, 5, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32,
			34, -1, 0, 2, 4, 6, 8, 10, 13, 15, 17, 19, 22, 24, 26, 29, 31, 34,
			36, 38, 41, -1, 0, 2, 4, 7, 10, 12, 15, 17, 21, 23, 26, 28, 31, 34,
			37, 39, 42, 45, 48, -1, 0, 3, 5, 8, 11, 14, 17, 20, 23, 26, 29, 33,
			36, 39, 42, 45, 48, 52, 55, -1, 0, 3, 6, 9, 13, 16, 19, 23, 26, 30,
			33, 37, 40, 44, 47, 51, 55, 58, 62, -1, 1, 4, 7, 11, 14, 18, 22,
			26, 29, 33, 37, 41, 45, 49, 53, 57, 61, 65, 69, -1, 1, 4, 8, 12,
			16, 20, 24, 28, 33, 37, 41, 45, 50, 54, 59, 63, 67, 72, 76, -1, 1,
			5, 9, 13, 17, 22, 26, 31, 36, 40, 45, 50, 55, 59, 64, 67, 74, 78,
			83, -1, 1, 5, 10, 14, 19, 24, 29, 34, 39, 44, 49, 54, 59, 64, 70,
			75, 80, 85, 90, -1, 1, 6, 11, 15, 21, 26, 31, 37, 42, 47, 53, 59,
			64, 70, 75, 81, 86, 92, 98, -1, 2, 6, 11, 17, 22, 28, 34, 39, 45,
			51, 57, 63, 67, 75, 81, 87, 93, 99, 105, -1, 2, 7, 12, 18, 24, 30,
			36, 42, 48, 55, 61, 67, 74, 80, 86, 93, 99, 106, 112, -1, 2, 7, 13,
			19, 25, 32, 38, 45, 52, 58, 65, 72, 78, 85, 92, 99, 106, 113, 119,
			-1, 2, 8, 14, 20, 27, 34, 41, 48, 55, 62, 69, 76, 83, 90, 98, 105,
			112, 119, 127 };

	/**
	 * Table of critical U values for alpha=0.01.  Entries of -1 indicate an
	 * insufficient sampling size.
	 */
	private static final int[] TABLE_1 = new int[] { -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0,
			-1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 3, 3,
			-1, -1, -1, -1, -1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 5, 5, 6, 6, 7, 8,
			-1, -1, -1, -1, 0, 1, 1, 2, 3, 4, 5, 6, 7, 7, 8, 9, 10, 11, 12, 13,
			-1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 12, 13, 15, 16, 17,
			18, -1, -1, -1, 0, 1, 3, 4, 6, 7, 9, 10, 12, 13, 15, 16, 18, 19,
			21, 22, 24, -1, -1, -1, 1, 2, 4, 6, 7, 9, 11, 13, 15, 17, 18, 20,
			22, 24, 26, 28, 30, -1, -1, 0, 1, 3, 5, 7, 9, 11, 13, 16, 18, 20,
			22, 24, 27, 29, 31, 33, 36, -1, -1, 0, 2, 4, 6, 9, 11, 13, 16, 18,
			21, 24, 26, 29, 31, 34, 37, 39, 42, -1, -1, 0, 2, 5, 7, 10, 13, 16,
			18, 21, 24, 27, 30, 33, 36, 39, 42, 45, 46, -1, -1, 1, 3, 6, 9, 12,
			15, 18, 21, 24, 27, 31, 34, 37, 41, 44, 47, 51, 54, -1, -1, 1, 3,
			7, 10, 13, 17, 20, 24, 27, 31, 34, 38, 42, 45, 49, 53, 56, 60, -1,
			-1, 1, 4, 7, 11, 15, 18, 22, 26, 30, 34, 38, 42, 46, 50, 54, 58,
			63, 67, -1, -1, 2, 5, 8, 12, 16, 20, 24, 29, 33, 37, 42, 46, 51,
			55, 60, 64, 69, 73, -1, -1, 2, 5, 9, 13, 18, 22, 27, 31, 36, 41,
			45, 50, 55, 60, 65, 70, 74, 79, -1, -1, 2, 6, 10, 15, 19, 24, 29,
			34, 39, 44, 49, 54, 60, 65, 70, 75, 81, 86, -1, -1, 2, 6, 11, 16,
			21, 26, 31, 37, 42, 47, 53, 58, 64, 70, 75, 81, 87, 92, -1, 0, 3,
			7, 12, 17, 22, 28, 33, 39, 45, 51, 56, 63, 69, 74, 81, 87, 93, 99,
			-1, 0, 3, 8, 13, 18, 24, 30, 36, 42, 46, 54, 60, 67, 73, 79, 86,
			92, 99, 105 };

}
