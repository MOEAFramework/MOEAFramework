/* Copyright 2009-2022 David Hadka
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
package org.moeaframework.core.configuration;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Variable;

/**
 * Methods to validate inputs and throw {@link IllegalArgumentException} if the check fails.
 */
public class Validate {
	
	private Validate() {
		super();
	}
	
	/**
	 * Validates that the value is greater than {@code 0}.
	 * 
	 * @param propertyName the property name being set
	 * @param value the value to check
	 * @throws IllegalArgumentException if the check fails
	 */
	public static void greaterThanZero(String propertyName, int value) {
		greaterThan(propertyName, 0, value);
	}
	
	/**
	 * Validates that the value is greater than {@code 0.0}.
	 * 
	 * @param propertyName the property name being set
	 * @param value the value to check
	 * @throws IllegalArgumentException if the check fails
	 */
	public static void greaterThanZero(String propertyName, double value) {
		greaterThan(propertyName, 0.0, value);
	}
	
	/**
	 * Validates that the value is greater than or equal to {@code 0}.
	 * 
	 * @param propertyName the property name being set
	 * @param value the value to check
	 * @throws IllegalArgumentException if the check fails
	 */
	public static void greaterThanOrEqualToZero(String propertyName, int value) {
		greaterThanOrEqual(propertyName, 0, value);
	}
	
	/**
	 * Validates that the value is greater than some minimum value, exclusively.
	 * 
	 * @param propertyName the property name being set
	 * @param minValue the minimum value, exclusive
	 * @param value the value to check
	 * @throws IllegalArgumentException if the check fails
	 */
	public static void greaterThan(String propertyName, int minValue, int value) {
		if (value <= minValue) {
			throw new IllegalArgumentException(propertyName + " must be greater than " + minValue + ", given " + value);
		}
	}
	
	/**
	 * Validates that the value is greater than some minimum value, exclusively.
	 * 
	 * @param propertyName the property name being set
	 * @param minValue the minimum value, exclusive
	 * @param value the value to check
	 * @throws IllegalArgumentException if the check fails
	 */
	public static void greaterThan(String propertyName, double minValue, double value) {
		if (value <= minValue) {
			throw new IllegalArgumentException(propertyName + " must be greater than " + minValue + ", given " + value);
		}
	}
	
	/**
	 * Validates that the value is greater than or equal to some minimum value.
	 * 
	 * @param propertyName the property name being set
	 * @param minValue the minimum value, inclusive
	 * @param value the value to check
	 * @throws IllegalArgumentException if the check fails
	 */
	public static void greaterThanOrEqual(String propertyName, int minValue, int value) {
		if (value < minValue) {
			throw new IllegalArgumentException(propertyName + " must be greater than or equal to " + minValue +
					", given " + value);
		}
	}
	
	/**
	 * Validates that the value is greater than or equal to some minimum value.
	 * 
	 * @param propertyName the property name being set
	 * @param minValue the minimum value, inclusive
	 * @param value the value to check
	 * @throws IllegalArgumentException if the check fails
	 */
	public static void greaterThanOrEqual(String propertyName, double minValue, double value) {
		if (value < minValue) {
			throw new IllegalArgumentException(propertyName + " must be greater than or equal to " + minValue +
					", given " + value);
		}
	}
	
	/**
	 * Validates that the value is contained within the lower and upper bounds, inclusively.
	 * 
	 * @param propertyName the property name being set
	 * @param lowerBound the lower bound, inclusive
	 * @param upperBound the upperBound, inclusive
	 * @param value the value to check
	 * @throws IllegalArgumentException if the check fails
	 */
	public static void inclusiveBetween(String propertyName, double lowerBound, double upperBound, double value) {
		if (value < lowerBound || value > upperBound) {
			throw new IllegalArgumentException(propertyName + " must be between " + lowerBound + " and " + upperBound +
					", given " + value);
		}
	}
	
	/**
	 * Validates that the value is a probability, namely between 0.0 and 1.0, inclusively.
	 * 
	 * @param propertyName the property name being set
	 * @param value the value to check
	 * @throws IllegalArgumentException if the check fails
	 */
	public static void probability(String propertyName, double value) {
		inclusiveBetween(propertyName, 0.0, 1.0, value);
	}
	
	/**
	 * Validates that the object is not null.
	 * 
	 * @param propertyName the property name being set
	 * @param object the object to check
	 * @throws IllegalArgumentException if the check fails
	 */
	public static void notNull(String propertyName, Object object) {
		if (object == null) {
			throw new IllegalArgumentException(propertyName + " can not be null");
		}
	}
	
	/**
	 * Validates that the problem is unconstrained.
	 * 
	 * @param problem the problem
	 * @throws IllegalArgumentException if the check fails
	 */
	public static void problemHasNoConstraints(Problem problem) {
		if (problem.getNumberOfConstraints() > 0) {
			throw new IllegalArgumentException("problem has constraints, requires unconstrained problem");
		}
	}
	
	/**
	 * Validates that the problem only contains decision variables of the given type.
	 * 
	 * @param problem the problem
	 * @param type the required decision variable type
	 * @throws IllegalArgumentException if the check fails
	 */
	public static void problemType(Problem problem, Class<? extends Variable> type) {
		if (!problem.isType(type)) {
			throw new IllegalArgumentException("problem has unsupported decision variables, requires " +
					type.getSimpleName());
		}
	}

}
