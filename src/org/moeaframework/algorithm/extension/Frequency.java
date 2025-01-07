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
package org.moeaframework.algorithm.extension;

import org.moeaframework.util.validate.Validate;

/**
 * Defines a frequency at which some action, operation, or even toccurs.
 */
public class Frequency {

	/**
	 * The type of frequency.
	 */
	public enum Type {

		/**
		 * Measures the number of objective function evaluations.
		 */
		EVALUATIONS,

		/**
		 * Measures the number of iterations or steps of an algorithm.
		 */
		ITERATIONS

	}

	private final int value;

	private final Type type;

	/**
	 * Constructs a new frequency.
	 * 
	 * @param value the value of this frequency, which must be {@code > 0}
	 * @param type the type of this frequency
	 */
	public Frequency(int value, Type type) {
		super();
		this.value = value;
		this.type = type;
		
		Validate.that("value", value).isGreaterThan(0);
	}

	/**
	 * The value of this frequency.
	 * 
	 * @return the value of this frequency
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns the type of this frequency.
	 * 
	 * @return the type of this frequency
	 */
	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return value + " " + type.name();
	}

	/**
	 * Constructs a frequency based on the number of evaluations.
	 * 
	 * @param value the number of evaluations, which must be {@code > 0}
	 * @return the frequency
	 */
	public static Frequency ofEvaluations(int value) {
		return new Frequency(value, Type.EVALUATIONS);
	}

	/**
	 * Constructs a frequency based on the number of iterations.
	 * 
	 * @param value the number of iterations, which must be {@code > 0}
	 * @return the frequency
	 */
	public static Frequency ofIterations(int value) {
		return new Frequency(value, Type.ITERATIONS);
	}

}
