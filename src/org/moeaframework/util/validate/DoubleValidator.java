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
 * Validator for {@code double} primitives.
 */
public class DoubleValidator extends NumberValidator<Double> {

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
	 * @param threshold the threshold
	 */
	public void isGreaterThan(double threshold) {
		super.isGreaterThan(threshold);
	}
	
	/**
	 * Asserts the value is greater than or equal to some threshold.
	 * 
	 * @param threshold the threshold
	 */
	public void isGreaterThanOrEqualTo(double threshold) {
		super.isGreaterThanOrEqualTo(threshold);
	}
	
	/**
	 * Asserts the value is less than some threshold.
	 * 
	 * @param threshold the threshold
	 */
	public void isLessThan(double threshold) {
		super.isLessThan(threshold);
	}
	
	/**
	 * Asserts the value is less than or equal to some threshold.
	 * 
	 * @param threshold the threshold
	 */
	public void isLessThanOrEqualTo(double threshold) {
		super.isLessThanOrEqualTo(threshold);
	}
	
	/**
	 * Asserts the value is between the given lower and upper bounds, inclusive.
	 * 
	 * @param lower the lower bound
	 * @param upper the upper bound
	 */
	public void isBetween(double lower, double upper) {
		super.isBetween(lower, upper);
	}
	
	/**
	 * Asserts the value is between the given lower and upper bounds, but not equal to the lower or upper bounds.
	 * 
	 * @param lower the lower bound
	 * @param upper the upper bound
	 */
	public void isStrictlyBetween(double lower, double upper) {
		super.isBetween(Math.nextUp(lower), Math.nextDown(upper));
	}
	
	/**
	 * Asserts the value is a probability, meaning {@code 0.0 <= value <= 1.0}.
	 */
	public void isProbability() {
		super.isBetween(0.0, 1.0);
	}

}
