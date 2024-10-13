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

import org.moeaframework.problem.Problem;

/**
 * Static methods for creating validators of parameters, arguments, or inputs.  Each method produces a validator for
 * a specific type.  Additionally, when creating the validator, the name of the parameter, argument, or input is given
 * along with the value, which is used to format the error message.
 * <p>
 * All validators throw {@link IllegalArgumentException}s if the conditions fail, though they may also throw
 * {@link NullPointerException} for unexpected {@code null} values.
 */
public final class Validate {
	
	private Validate() {
		super();
	}
	
	/**
	 * Constructs a validator for a double value.
	 * 
	 * @param name the parameter name
	 * @param value the parameter value
	 * @return the validator
	 */
	public static final DoubleValidator that(String name, double value) {
		return new DoubleValidator(name, value);
	}
	
	/**
	 * Constructs a validator for an integer value.
	 * 
	 * @param name the parameter name
	 * @param value the parameter value
	 * @return the validator
	 */
	public static final IntegerValidator that(String name, int value) {
		return new IntegerValidator(name, value);
	}
	
	/**
	 * Constructs a validator for a {@link Problem}.
	 * 
	 * @param name the parameter name
	 * @param problem the problem
	 * @return the validator
	 */
	public static final ProblemValidator that(String name, Problem problem) {
		return new ProblemValidator(name, problem);
	}
	
	/**
	 * Constructs a validator for any generic object.
	 * 
	 * @param name the parameter name
	 * @param value the object
	 * @param <T> the type of the object
	 * @return the validator
	 */
	public static final <T> ObjectValidator<T> that(String name, T value) {
		return new ObjectValidator<T>(name, value);
	}
	
	/**
	 * Always fails with the given message.
	 * 
	 * @param <R> the return type
	 * @param message the reason for the failure
	 * @return this method never returns
	 */
	public static final <R> R fail(String message) {
		throw new IllegalArgumentException(message);
	}

}
