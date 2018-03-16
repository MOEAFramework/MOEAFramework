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

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.moeaframework.core.Settings;

/**
 * The Wilcoxon Signed-Ranks test determines if the population median is equal
 * to a specified value.
 * <p>
 * <ul>
 * <li>Null Hypothesis: The population median equals X.</li>
 * <li>Alternative Hypothesis: The population median does not equal X.</li>
 * </ul>
 * <p>
 * Assumptions:
 * <ol>
 * <li>Samples are randomly selected from the population</li>
 * <li>The underlying population distribution is symmetrical</li>
 * </ol>
 * <p>
 * References:
 * <ol>
 * <li>Sheskin, D.J. "Handbook of Parametric and Nonparametric Statistical
 * Procedures, Third Edition." Chapman & Hall/CRC. 2004.
 * </ol>
 */
public class WilcoxonSignedRanksTest extends OrdinalStatisticalTest {

	/**
	 * Compares observations based on their absolute value.
	 */
	private static class AbsoluteObservationComparator implements
			Comparator<RankedObservation>, Serializable {

		private static final long serialVersionUID = 7337112773629454794L;

		@Override
		public int compare(RankedObservation o1, RankedObservation o2) {
			double v1 = Math.abs(o1.getValue());
			double v2 = Math.abs(o2.getValue());

			if (v1 < v2) {
				return -1;
			} else if (v1 > v2) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	/**
	 * The value being tested against the population median.
	 */
	private final double median;

	/**
	 * The value of {@code T} from the last invocation of {@link #test}. This
	 * is package private and intended only for testing.
	 */
	double lastT;

	/**
	 * Constructs a Wilcoxon signed ranks test with the specified median.
	 * 
	 * @param median the value being tested against the population median
	 */
	public WilcoxonSignedRanksTest(double median) {
		super(1, new AbsoluteObservationComparator());
		this.median = median;
	}

	/**
	 * Returns the value being tested against the population median.
	 * 
	 * @return the value being tested against the population median
	 */
	public double getMedian() {
		return median;
	}

	/**
	 * Adds a new observation with the specified value.
	 * 
	 * @param value the value of the new observation
	 */
	public void add(double value) {
		if (value - median != 0.0) {
			super.add(value - median, 0);
		}
	}
	
	/**
	 * Adds several new observations with the specified values.
	 * 
	 * @param values the values of the new observations
	 */
	public void add(double[] values) {
		for (double value : values) {
			add(value);
		}
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
		double Rpos = 0.0;
		double Rneg = 0.0;

		update();

		for (RankedObservation observation : data) {
			if (observation.getValue() < 0.0) {
				Rneg += observation.getRank();
			} else {
				Rpos += observation.getRank();
			}
		}

		int n = data.size();
		double T = Math.min(Rpos, Rneg);

		// expose T for testing
		lastT = T;

		if (n <= 50) {
			return T <= getCriticalTValueFromTable(n, alpha);
		} else {
			double z = 0.0;
			NormalDistribution dist = new NormalDistribution();

			if (Settings.isContinuityCorrection()) {
				z = (Math.abs(T - n * (n + 1) / 4.0) - 0.5)
						/ Math.sqrt(n * (n + 1) * (n + n + 1) / 24.0);
			} else {
				z = (T - n * (n + 1) / 4.0)
						/ Math.sqrt(n * (n + 1) * (n + n + 1) / 24.0);
			}

			return Math.abs(z) >= Math.abs(
					dist.inverseCumulativeProbability(alpha));
		}
	}

	/**
	 * Returns the critical T value from the lookup tables.
	 * 
	 * @param n1 the number of samples from the first group
	 * @param n2 the number of samples from the second group
	 * @param alpha the prespecified level of confidence; only values of 0.05
	 *        and 0.01 are permitted
	 * @return the critical U value from the lookup tables
	 * @throws IllegalArgumentException if an insufficient sampling size is
	 *         provided, or if an invalid alpha value is provided
	 */
	private static int getCriticalTValueFromTable(int n, double alpha) {
		if ((n < 6) || (n > 50)) {
			throw new IllegalArgumentException("only valid for 6 <= n <= 50");
		}

		int value = -1;

		if (alpha == 0.05) {
			value = TABLE_5[n - 6];
		} else if (alpha == 0.01) {
			value = TABLE_1[n - 6];
		} else {
			throw new IllegalArgumentException("only valid for 0.05 or 0.01");
		}

		if (value == -1) {
			throw new IllegalArgumentException("insufficient sampling size");
		}

		return value;
	}

	/**
	 * Table of critical T values for alpha=0.05.  Entries of -1 indicate an
	 * insufficient sampling size.
	 */
	private static final int[] TABLE_5 = new int[] { 0, 2, 3, 5, 8, 10, 13, 17,
			21, 25, 29, 34, 40, 46, 52, 58, 65, 73, 81, 89, 98, 107, 116, 126,
			137, 147, 159, 170, 182, 195, 208, 221, 235, 249, 264, 279, 294,
			310, 327, 343, 361, 378, 396, 415, 434 };

	/**
	 * Table of critical T values for alpha=0.01.  Entries of -1 indicate an
	 * insufficient sampling size.
	 */
	private static final int[] TABLE_1 = new int[] { -1, -1, 0, 1, 3, 5, 7, 9,
			12, 15, 19, 23, 27, 32, 37, 42, 48, 54, 61, 68, 75, 83, 91, 100,
			109, 118, 128, 138, 148, 159, 171, 182, 194, 207, 220, 233, 247,
			261, 276, 291, 307, 322, 339, 355, 373 };

}
