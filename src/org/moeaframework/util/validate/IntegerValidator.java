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
 * Validator for {@code int} primitives.
 */
public final class IntegerValidator extends NumberValidator<Integer> {

	/**
	 * Constructs a new validator for {@code int} primitives.
	 * 
	 * @param propertyName the property name
	 * @param propertyValue the property value
	 */
	public IntegerValidator(String propertyName, int propertyValue) {
		super(propertyName, propertyValue);
	}
	
	/**
	 * Asserts the value is greater than some threshold.
	 * 
	 * @param thresholdValue the threshold value
	 */
	public final void isGreaterThan(int thresholdValue) {
		isGreaterThan(null, thresholdValue);
	}
	
	/**
	 * Asserts the value is greater than some threshold.
	 * 
	 * @param thresholdName the threshold name
	 * @param thresholdValue the threshold value
	 */
	public final void isGreaterThan(String thresholdName, int thresholdValue) {
		super.isGreaterThan(thresholdName, thresholdValue);
	}
	
	/**
	 * Asserts the value is greater than or equal to some threshold.
	 * 
	 * @param thresholdValue the threshold value
	 */
	public final void isGreaterThanOrEqualTo(int thresholdValue) {
		isGreaterThanOrEqualTo(null, thresholdValue);
	}
	
	/**
	 * Asserts the value is greater than or equal to some threshold.
	 * 
	 * @param thresholdName the threshold name
	 * @param thresholdValue the threshold value
	 */
	public final void isGreaterThanOrEqualTo(String thresholdName, int thresholdValue) {
		super.isGreaterThanOrEqualTo(thresholdName, thresholdValue);
	}
	
	/**
	 * Asserts the value is less than some threshold.
	 * 
	 * @param thresholdValue the threshold value
	 */
	public final void isLessThan(int thresholdValue) {
		isLessThan(null, thresholdValue);
	}
	
	/**
	 * Asserts the value is less than some threshold.
	 * 
	 * @param thresholdName the threshold name
	 * @param thresholdValue the threshold value
	 */
	public final void isLessThan(String thresholdName, int thresholdValue) {
		super.isLessThan(thresholdName, thresholdValue);
	}
	
	/**
	 * Asserts the value is less than or equal to some threshold.
	 * 
	 * @param thresholdValue the threshold value
	 */
	public final void isLessThanOrEqualTo(int thresholdValue) {
		isLessThanOrEqualTo(null, thresholdValue);
	}
	
	/**
	 * Asserts the value is less than or equal to some threshold.
	 * 
	 * @param thresholdName the threshold name
	 * @param thresholdValue the threshold value
	 */
	public final void isLessThanOrEqualTo(String thresholdName, int thresholdValue) {
		super.isLessThanOrEqualTo(thresholdName, thresholdValue);
	}
	
	/**
	 * Asserts the value equals some other parameter.
	 * 
	 * @param parameterValue the parameter value
	 */
	public final void isEqualTo(int parameterValue) {
		isEqualTo(null, parameterValue);
	}
	
	/**
	 * Asserts the value equals some other parameter.
	 * 
	 * @param parameterName the parameter name
	 * @param parameterValue the parameter value
	 */
	public final void isEqualTo(String parameterName, int parameterValue) {
		super.isEqualTo(parameterName, parameterValue);
	}
	
	/**
	 * Asserts the value is not equal to some other parameter.
	 * 
	 * @param parameterValue the parameter value
	 */
	public final void isNotEqualTo(int parameterValue) {
		isNotEqualTo(null, parameterValue);
	}
	
	/**
	 * Asserts the value is not equal to some other parameter.
	 * 
	 * @param parameterName the parameter name
	 * @param parameterValue the parameter value
	 */
	public final void isNotEqualTo(String parameterName, int parameterValue) {
		super.isNotEqualTo(parameterName, parameterValue);
	}
	
	/**
	 * Asserts the value is divisible by some divisor.
	 * 
	 * @param divisorValue the divisor value
	 */
	public final void isDivisibleBy(int divisorValue) {
		isDivisibleBy(null, divisorValue);
	}
	
	/**
	 * Asserts the value is divisible by some divisor.
	 * 
	 * @param divisorName the divisor name, or {@code null} if no name is associated with the value
	 * @param divisorValue the divisor value
	 */
	public final void isDivisibleBy(String divisorName, int divisorValue) {
		if (getPropertyValue() % divisorValue != 0) {
			failsOnCondition("divisible by", divisorName, divisorValue);
		}
	}
	
	/**
	 * Asserts the value is between the given lower and upper bounds, inclusive.
	 * 
	 * @param lower the lower bound
	 * @param upper the upper bound
	 */
	public final void isBetween(int lower, int upper) {
		super.isBetween(lower, upper);
	}

}
