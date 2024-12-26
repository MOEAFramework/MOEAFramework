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
package org.moeaframework.analysis.sensitivity;

/**
 * Stores the result from sensitivity analysis for a single parameter, or if measuring higher-order effects, a group of
 * parameters.
 * 
 * @param <T> the type of key identifying the parameter or parameters
 */
public class Sensitivity<T> {
	
	private final T key;
	
	private final double sensitivity;
	
	private final double confidenceInterval;
	
	/**
	 * Constructs a new sensitivity result.
	 * 
	 * @param key the key identifying the parameter or parameters involved
	 * @param sensitivity the sensitivity value
	 * @param confidenceInterval the confidence interval
	 */
	public Sensitivity(T key, double sensitivity, double confidenceInterval) {
		super();
		this.key = key;
		this.sensitivity = sensitivity;
		this.confidenceInterval = confidenceInterval;
	}

	/**
	 * The key identifying the parameter or parameters involved.
	 * 
	 * @return the key
	 */
	public T getKey() {
		return key;
	}

	/**
	 * Returns the sensitivity value.
	 * 
	 * @return the sensitivity value
	 */
	public double getSensitivity() {
		return sensitivity;
	}

	/**
	 * Returns the confidence interval.
	 * 
	 * @return the confidence interval
	 */
	public double getConfidenceInterval() {
		return confidenceInterval;
	}

}
