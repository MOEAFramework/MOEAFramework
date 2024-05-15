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
	 * Throws an exception indicating the specified condition failed.
	 * 
	 * @param condition a description of the condition that failed
	 */
	protected final void failsOnCondition(String condition) {
		StringBuilder sb = new StringBuilder();
		sb.append("Expected ");
		sb.append(getPropertyName());
		sb.append(" to be ");
		sb.append(condition);
		sb.append(", given ");
		sb.append(getPropertyValue());
		
		throw new IllegalArgumentException(sb.toString());
	}
	
	/**
	 * Throws an exception indicating the specified condition failed along with threshold value that was violated.
	 * 
	 * @param condition a description of the condition that failed
	 * @param thresholdName the threshold name, or {@code null} if not associated with a named parameter
	 * @param thresholdValue the threshold value
	 */
	protected final void failsOnCondition(String condition, String thresholdName, T thresholdValue) {
		failsOnCondition(condition + " " +
				(thresholdName == null ? thresholdValue : thresholdName + " (" + thresholdValue + ")"));
	}
	
	/**
	 * Asserts the value is greater than some threshold.
	 * 
	 * @param thresholdName the threshold name, or {@code null} if not associated with a named parameter
	 * @param thresholdValue the threshold value
	 */
	protected final void isGreaterThan(String thresholdName, T thresholdValue) {
		if (getPropertyValue().compareTo(thresholdValue) <= 0) {
			failsOnCondition("greater than", thresholdName, thresholdValue);
		}
	}
	
	/**
	 * Asserts the value is greater than or equal to some threshold.
	 * 
	 * @param thresholdName the threshold name, or {@code null} if not associated with a named parameter
	 * @param thresholdValue the threshold value
	 */
	protected final void isGreaterThanOrEqualTo(String thresholdName, T thresholdValue) {
		if (getPropertyValue().compareTo(thresholdValue) < 0) {
			failsOnCondition("greater than or equal to", thresholdName, thresholdValue);
		}
	}
	
	/**
	 * Asserts the value is less than some threshold.
	 * 
	 * @param thresholdName the threshold name, or {@code null} if not associated with a named parameter
	 * @param thresholdValue the threshold value
	 */
	protected final void isLessThan(String thresholdName, T thresholdValue) {
		if (getPropertyValue().compareTo(thresholdValue) >= 0) {
			failsOnCondition("less than", thresholdName, thresholdValue);
		}
	}
	
	/**
	 * Asserts the value is less than or equal to some threshold.
	 * 
	 * @param thresholdName the threshold name, or {@code null} if not associated with a named parameter
	 * @param thresholdValue the threshold value
	 */
	protected final void isLessThanOrEqualTo(String thresholdName, T thresholdValue) {
		if (getPropertyValue().compareTo(thresholdValue) > 0) {
			failsOnCondition("less than or equal to", thresholdName, thresholdValue);
		}
	}
	
	/**
	 * Asserts the value equals some other parameter.
	 * 
	 * @param parameterName the parameter name, or {@code null} if not associated with a named parameter
	 * @param parameterValue the parameter value
	 */
	protected final void isEqualTo(String parameterName, T parameterValue) {
		if (getPropertyValue().compareTo(parameterValue) != 0) {
			failsOnCondition("equal to", parameterName, parameterValue);
		}
	}
	
	/**
	 * Asserts the value is not equal to some other parameter.
	 * 
	 * @param parameterName the parameter name, or {@code null} if not associated with a named parameter
	 * @param parameterValue the parameter value
	 */
	protected final void isNotEqualTo(String parameterName, T parameterValue) {
		if (getPropertyValue().compareTo(parameterValue) == 0) {
			failsOnCondition("not equal to", parameterName, parameterValue);
		}
	}
	
	/**
	 * Asserts the value is between the given lower and upper bounds, inclusive.
	 * 
	 * @param lower the lower bound
	 * @param upper the upper bound
	 */
	protected final void isBetween(T lower, T upper) {
		if (getPropertyValue().compareTo(lower) < 0 || getPropertyValue().compareTo(upper) > 0) {
			failsOnCondition("between " + lower + " and " + upper + " (inclusive)");
		}
	}

}
