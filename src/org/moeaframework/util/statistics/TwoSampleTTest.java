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

import java.util.List;

import org.apache.commons.math3.stat.inference.TestUtils;

/**
 * The Two-Sample t test determines if the mean of two populations are
 * different. The independent flag in the constructor is used to choose between
 * the unpaired (independent) and paired (dependent) test.
 * <p>
 * <ul>
 * <li>Null Hypothesis: The two population means are equal.
 * <li>Alternative Hypothesis: The two population means are not equal.
 * </ul>
 * <p>
 * Assumptions:
 * <ol>
 * <li>Samples are randomly selected from the corresponding population
 * <li>The distribution of the underlying populations is normal
 * </ol>
 * <p>
 * References:
 * <ol>
 * <li>Sheskin, D.J. "Handbook of Parametric and Nonparametric Statistical
 * Procedures, Third Edition." Chapman & Hall/CRC. 2004.
 * </ol>
 */
public class TwoSampleTTest extends IntervalRatioStatisticalTest {

	/**
	 * {@code true} if using the unpaired T test; {@code false} if using the
	 * paired T test.
	 */
	private final boolean independent;

	/**
	 * Constructs a two sample T test.
	 * 
	 * @param independent uses the unpaired T test if {@code true}; the paired T
	 *        test if {@code false}
	 */
	public TwoSampleTTest(boolean independent) {
		super(2);
		this.independent = independent;
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
	 * @see TestUtils#tTest(double[], double[], double)
	 * @see TestUtils#pairedTTest(double[], double[], double)
	 */
	@Override
	public boolean test(double alpha) {
		List<double[]> categories = categorize();

		if (independent) {
			return TestUtils.tTest(categories.get(0), categories.get(1), alpha);
		} else {
			return TestUtils.pairedTTest(categories.get(0), categories.get(1),
					alpha);
		}
	}

}
