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

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

/**
 * The Kruskal-Wallis One-Way Analysis of Variance by Ranks is a non-parametric
 * statistical test determining if (at least) two out of K >= 2 populations have
 * differing medians.
 * <p>
 * <ul>
 * <li>Null Hypothesis: All populations have equal medians.
 * <li>Alternative Hypothesis: Not all populations have equal medians.
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
 * <li>Kruskal, W.H. and Wallis W.A. "Use of Ranks in One-Criterion Variance
 * Analysis." Journal of the American Statistical Association, 47(260):583-621,
 * 1952.
 * <li>Sheskin, D.J. "Handbook of Parametric and Nonparametric Statistical
 * Procedures, Third Edition." Chapman & Hall/CRC. 2004.
 * </ol>
 */
public class KruskalWallisTest extends OrdinalStatisticalTest {

	/**
	 * Constructs a Kruskal-Wallis test with the specified number of groups.
	 * 
	 * @param numberOfGroups the number of groups being tested
	 */
	public KruskalWallisTest(int numberOfGroups) {
		super(numberOfGroups);
		
		if (numberOfGroups <= 1) {
			throw new IllegalArgumentException("requires two or more groups");
		}
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
	 * Computes the chi-squared approximation of the Kruskal-Wallis test
	 * statistic. See equation (22-1) in the reference book for details.
	 * 
	 * @return the chi-squared approximation of the Kruskal-Wallis test
	 *         statistic
	 */
	double H() {
		int[] n = new int[numberOfGroups];
		double[] rbar = new double[numberOfGroups];

		for (RankedObservation observation : data) {
			n[observation.getGroup()]++;
			rbar[observation.getGroup()] += observation.getRank();
		}

		double H = 0.0;
		for (int i = 0; i < numberOfGroups; i++) {
			H += Math.pow(rbar[i], 2.0) / n[i];
		}

		int N = data.size();
		return 12.0 / (N * (N + 1)) * H - 3.0 * (N + 1);
	}

	/**
	 * Computes the correction factor for ties. See equation (22-3) in the
	 * reference book for details.
	 * 
	 * @return the correction factor for ties
	 */
	double C() {
		int N = data.size();
		double C = 0.0;

		int i = 0;
		while (i < N) {
			int j = i + 1;

			while ((j < N)
					&& (data.get(i).getValue() == data.get(j).getValue())) {
				j++;
			}

			C += Math.pow(j - i, 3.0) - (j - i);
			i = j;
		}

		return 1 - C / (Math.pow(N, 3.0) - N);
	}

	@Override
	public boolean test(double alpha) {
		update();

		ChiSquaredDistribution dist = new ChiSquaredDistribution(
				numberOfGroups - 1);
		double H = H();
		double C = C();

		if (C == 0.0) {
			// all observations the same
			return false;
		}

		return 1.0 - dist.cumulativeProbability(H / C) < alpha;
	}

}
