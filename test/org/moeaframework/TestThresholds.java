/* Copyright 2009-2024 David Hadka
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

import org.moeaframework.core.Settings;

/**
 * Thresholds to use when comparing floating-point values and when sampling statistics.
 */
public class TestThresholds {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private TestThresholds() {
		super();
	}

	/**
	 * The number of samples or trials to use in testing.
	 */
	public static final int SAMPLES = 10000;

	/**
	 * The floating-point threshold for low precision / low fidelity comparisons.
	 */
	public static final double LOW_PRECISION = 0.05;
	
	/**
	 * The floating-point threshold for high precision / high fidelity comparisons.
	 */
	public static final double HIGH_PRECISION = Settings.EPS;

}
