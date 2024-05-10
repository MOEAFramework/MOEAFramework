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
package org.moeaframework.util.validate;

/**
 * Abstract validator for {@link Number}s.
 * <p>
 * To simplify the implementation of these validations, we use the fact that most numeric types implement the
 * {@link Comparable} interface.  Consequently, this class only supports the "boxed" types for numbers, while most
 * parameters we are validating are primitives.  Thus, prefer creating subclasses that accept only the primitive type,
 * which then call these generic methods.
 * 
 * @param <T> the number type
 */
public abstract class NumberValidator<T extends Number & Comparable<T>> extends Validator<T> {

	/**
	 * Constructs a new number validator.
	 * 
	 * @param propertyName the property name
	 * @param propertyValue the property value
	 */
	public NumberValidator(String propertyName, T propertyValue) {
		super(propertyName, propertyValue);
	}
	
	/**
	 * Asserts the value is greater than some threshold.
	 * 
	 * @param threshold the threshold
	 */
	protected void isGreaterThan(T threshold) {
		if (getPropertyValue().compareTo(threshold) <= 0) {
			throw new IllegalArgumentException("Expected " + getPropertyName() + " to be greater than " + threshold +
					", given " + getPropertyValue());
		}
	}
	
	/**
	 * Asserts the value is greater than or equal to some threshold.
	 * 
	 * @param threshold the threshold
	 */
	protected void isGreaterThanOrEqualTo(T threshold) {
		if (getPropertyValue().compareTo(threshold) < 0) {
			throw new IllegalArgumentException("Expected " + getPropertyName() + " to be greater than or equal to " +
					threshold + ", given " + getPropertyValue());
		}
	}
	
	/**
	 * Asserts the value is less than some threshold.
	 * 
	 * @param threshold the threshold
	 */
	protected void isLessThan(T threshold) {
		if (getPropertyValue().compareTo(threshold) >= 0) {
			throw new IllegalArgumentException("Expected " + getPropertyName() + " to be less than " + threshold +
					", given " + getPropertyValue());
		}
	}
	
	/**
	 * Asserts the value is less than or equal to some threshold.
	 * 
	 * @param threshold the threshold
	 */
	protected void isLessThanOrEqualTo(T threshold) {
		if (getPropertyValue().compareTo(threshold) > 0) {
			throw new IllegalArgumentException("Expected " + getPropertyName() + " to be less than or equal to " +
					threshold + ", given " + getPropertyValue());
		}
	}
	
	/**
	 * Asserts the value is between the given lower and upper bounds, inclusive.
	 * 
	 * @param lower the lower bound
	 * @param upper the upper bound
	 */
	protected void isBetween(T lower, T upper) {
		if (getPropertyValue().compareTo(lower) < 0 || getPropertyValue().compareTo(upper) > 0) {
			throw new IllegalArgumentException("Expected " + getPropertyName() + " to be between " + lower +
					" and " + upper + " (inclusive), given " + getPropertyValue());
		}
	}

}
