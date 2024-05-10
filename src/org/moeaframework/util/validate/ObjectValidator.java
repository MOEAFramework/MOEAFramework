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

import java.lang.reflect.Array;

/**
 * Validator for generic objects, including arrays.
 * 
 * @param <T> the object type
 */
public class ObjectValidator<T> extends Validator<T> {

	/**
	 * Constructs a new validator.
	 * 
	 * @param propertyName the property name corresponding to the object
	 * @param propertyValue the object
	 */
	public ObjectValidator(String propertyName, T propertyValue) {
		super(propertyName, propertyValue);
	}
	
	/**
	 * Asserts the object is not {@code null}.
	 */
	public void isNotNull() {
		if (getPropertyValue() == null) {
			throw new IllegalArgumentException("Expected " + getPropertyName() + " to be set, given a <null> value");
		}
	}
	
	/**
	 * Asserts the given object is not empty.  An empty object is defined as:
	 * <ol>
	 *   <li>A {@code null} value,
	 *   <li>An array with length 0, or
	 *   <li>An {@link Iterable} that contains no elements, which applies to practically all collection types, .
	 * </ol>
	 */
	public void isNotEmpty() {
		isNotNull();
		
		T value = getPropertyValue();
		
		if (value instanceof Iterable<?> iterable && !iterable.iterator().hasNext()) {
			throw new IllegalArgumentException("Expected collection " + getPropertyName() + " to not be empty");
		}
		
		if (value.getClass().isArray() && Array.getLength(value) == 0) {
			throw new IllegalArgumentException("Expected array " + getPropertyName() + " to not be empty");
		}
	}

}
