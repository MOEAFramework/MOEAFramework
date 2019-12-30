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
package org.moeaframework;

/**
 * Thresholds to use when comparing floating-point values and when sampling
 * statistics.
 */
public class TestThresholds {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private TestThresholds() {
		super();
	}

	/**
	 * The number of samples or trials to run when collecting statistical data.
	 */
	public static final int SAMPLES = 10000;

	/**
	 * The floating-point threshold when checking selection statistical results.
	 */
	public static final double SELECTION_EPS = 0.05;

	/**
	 * The floating-point threshold when checking variation statistical results.
	 */
	public static final double VARIATION_EPS = 0.05;

	/**
	 * The floating-point threshold when checking statistical results.
	 */
	public static final double STATISTICS_EPS = 0.05;

	/**
	 * The floating-point threshold when comparing solutions.
	 */
	public static final double SOLUTION_EPS = 0.05;

}
