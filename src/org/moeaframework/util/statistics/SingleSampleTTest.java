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
 * The Single-Sample t test determines if the population's mean equals some
 * specified value.
 * <p>
 * <ul>
 * <li>Null Hypothesis: The population's mean equals X.
 * <li>Alternative Hypothesis: The population's mean does not equal X.
 * </ul>
 * <p>
 * Assumptions:
 * <ol>
 * <li>Samples are randomly selected from the population
 * <li>The distribution of the underlying population is normal
 * </ol>
 * <p>
 * References:
 * <ol>
 * <li>Sheskin, D.J. "Handbook of Parametric and Nonparametric Statistical
 * Procedures, Third Edition." Chapman & Hall/CRC. 2004.
 * </ol>
 */
public class SingleSampleTTest extends IntervalRatioStatisticalTest {

	/**
	 * The value being tested against the population mean.
	 */
	private final double mean;

	/**
	 * Constructs a single-sample T test with the specified mean.
	 * 
	 * @param mean the value being tested against the population mean
	 */
	public SingleSampleTTest(double mean) {
		super(1);
		this.mean = mean;
	}

	/**
	 * Returns the value being tested against the population mean.
	 * 
	 * @return the value being tested against the population mean
	 */
	public double getMean() {
		return mean;
	}

	/**
	 * Adds a new observation with the specified value.
	 * 
	 * @param value the value of the new observation
	 */
	public void add(double value) {
		super.add(value, 0);
	}
	
	/**
	 * Adds several new observations with the specified values.
	 * 
	 * @param values the values of the new observations
	 */
	public void addAll(double[] values) {
		super.addAll(values, 0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see TestUtils#tTest(double, double[], double)
	 */
	@Override
	public boolean test(double alpha) {
		return TestUtils.tTest(mean, categorize().get(0), alpha);
	}

}
