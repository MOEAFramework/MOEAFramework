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

import org.apache.commons.math3.stat.inference.TestUtils;

/**
 * The Single-Factor Between-Subjects Analysis of Variance determines if (at
 * least) two out of K >= 2 populations have differing means.
 * <p>
 * <ul>
 * <li>Null Hypothesis: All populations have equal means.
 * <li>Alternative Hypothesis: Not all populations have equal means.
 * </ul>
 * <p>
 * Assumptions:
 * <ol>
 * <li>Samples are randomly selected from their corresponding populations
 * <li>The distribution of the underlying populations are normal
 * <li>The variances of the underlying populations are identical
 * </ol>
 * <p>
 * References:
 * <ol>
 * <li>Sheskin, D.J. "Handbook of Parametric and Nonparametric Statistical
 * Procedures, Third Edition." Chapman & Hall/CRC. 2004.
 * </ol>
 */
public class OneWayANOVA extends IntervalRatioStatisticalTest {

	/**
	 * Constructs a one-way ANOVA statistical test with the specified number of
	 * groups.
	 * 
	 * @param numberOfGroups the number of groups being tested
	 */
	public OneWayANOVA(int numberOfGroups) {
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
	 * {@inheritDoc}
	 * 
	 * @see TestUtils#oneWayAnovaTest(java.util.Collection, double)
	 */
	@Override
	public boolean test(double alpha) {
		return TestUtils.oneWayAnovaTest(categorize(), alpha);
	}

}
