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

import org.moeaframework.core.Problem;
import org.moeaframework.core.Variable;

/**
 * Validator for {@link Problem}s.
 */
public final class ProblemValidator extends ObjectValidator<Problem> {

	/**
	 * Constructs a new problem validator.
	 * 
	 * @param propertyName the property name
	 * @param problem the problem
	 */
	public ProblemValidator(String propertyName, Problem problem) {
		super(propertyName, problem);
	}
	
	/**
	 * Asserts the problem has no constraints.
	 */
	public final void isUnconstrained() {
		isNotNull();
		
		if (getPropertyValue().getNumberOfConstraints() > 0) {
			throw new IllegalArgumentException("Expected " + getPropertyName() + " to be an unconstrained problem");
		}
	}
	
	/**
	 * Asserts the decision variables are all of the requested type.
	 * 
	 * @param type the requested type
	 */
	public final void isType(Class<? extends Variable> type) {
		isNotNull();
		
		if (!getPropertyValue().isType(type)) {
			throw new IllegalArgumentException("Expected " + getPropertyName() + " to have variables of type " +
					type.getSimpleName());
		}
	}

}
