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
package org.moeaframework.util.validate;

/**
 * Validator for {@code double} primitives.
 */
public final class DoubleValidator extends NumberValidator<Double> {

	/**
	 * Constructs a new validator for {@code double} primitives.
	 * 
	 * @param propertyName the property name
	 * @param propertyValue the property value
	 */
	public DoubleValidator(String propertyName, double propertyValue) {
		super(propertyName, propertyValue);
	}
	
	/**
	 * Asserts the value is greater than some threshold.
	 * 
	 * @param thresholdValue the threshold value
	 */
	public final void isGreaterThan(double thresholdValue) {
		isGreaterThan(null, thresholdValue);
	}
	
	/**
	 * Asserts the value is greater than some threshold.
	 * 
	 * @param thresholdName the threshold name
	 * @param thresholdValue the threshold value
	 */
	public final void isGreaterThan(String thresholdName, double thresholdValue) {
		isValidNumber();
		super.isGreaterThan(thresholdName, thresholdValue);
	}
	
	/**
	 * Asserts the value is greater than or equal to some threshold.
	 * 
	 * @param thresholdValue the threshold value
	 */
	public final void isGreaterThanOrEqualTo(double thresholdValue) {
		isGreaterThanOrEqualTo(null, thresholdValue);
	}
	
	/**
	 * Asserts the value is greater than or equal to some threshold.
	 * 
	 * @param thresholdName the threshold name
	 * @param thresholdValue the threshold value
	 */
	public final void isGreaterThanOrEqualTo(String thresholdName, double thresholdValue) {
		isValidNumber();
		super.isGreaterThanOrEqualTo(thresholdName, thresholdValue);
	}
	
	/**
	 * Asserts the value is less than some threshold.
	 * 
	 * @param thresholdValue the threshold value
	 */
	public final void isLessThan(double thresholdValue) {
		isLessThan(null, thresholdValue);
	}
	
	/**
	 * Asserts the value is less than some threshold.
	 * 
	 * @param thresholdName the threshold name
	 * @param thresholdValue the threshold value
	 */
	public final void isLessThan(String thresholdName, double thresholdValue) {
		isValidNumber();
		super.isLessThan(thresholdName, thresholdValue);
	}
	
	/**
	 * Asserts the value is less than or equal to some threshold.
	 * 
	 * @param thresholdValue the threshold value
	 */
	public final void isLessThanOrEqualTo(double thresholdValue) {
		isLessThanOrEqualTo(null, thresholdValue);
	}
	
	/**
	 * Asserts the value is less than or equal to some threshold.
	 * 
	 * @param thresholdName the threshold name
	 * @param thresholdValue the threshold value
	 */
	public final void isLessThanOrEqualTo(String thresholdName, double thresholdValue) {
		isValidNumber();
		super.isLessThanOrEqualTo(thresholdName, thresholdValue);
	}
	
	/**
	 * Asserts the value is between the given lower and upper bounds, inclusive.
	 * 
	 * @param lower the lower bound
	 * @param upper the upper bound
	 */
	public final void isBetween(double lower, double upper) {
		isValidNumber();
		super.isBetween(lower, upper);
	}
	
	/**
	 * Asserts the value is between the given lower and upper bounds, but not equal to the lower or upper bounds.
	 * 
	 * @param lower the lower bound
	 * @param upper the upper bound
	 */
	public final void isStrictlyBetween(double lower, double upper) {
		isBetween(Math.nextUp(lower), Math.nextDown(upper));
	}
	
	/**
	 * Asserts the value represents a probability, meaning {@code 0.0 <= value <= 1.0}.
	 */
	public final void isProbability() {
		isBetween(0.0, 1.0);
	}
	
	/**
	 * Asserts the value is a finite number (i.e., not infinite or {@value Double#NaN}).
	 */
	public final void isFinite() {
		if (!Double.isFinite(getPropertyValue())) {
			throw new IllegalArgumentException("Expected " + getPropertyName() + " to be a finite number, given " +
					getPropertyValue());
		}
	}
	
	/**
	 * Asserts the value is a valid number (i.e., not {@value Double#NaN}).
	 */
	private final void isValidNumber() {
		if (Double.isNaN(getPropertyValue())) {
			throw new IllegalArgumentException("Expected " + getPropertyName() + " to be a valid number, given " +
					getPropertyValue());
		}
	}

}
