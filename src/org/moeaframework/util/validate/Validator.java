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
 * Base class for validators, which stores the property name and value.  Subclasses should define methods specific
 * to the type being checked.
 * 
 * @param <T> the value type
 */
public abstract class Validator<T> {
	
	private final String propertyName;
	
	private final T propertyValue;
	
	/**
	 * Constructs a new validator.
	 * 
	 * @param propertyName the property name
	 * @param propertyValue the property value
	 */
	public Validator(String propertyName, T propertyValue) {
		super();
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}
	
	/**
	 * Returns the property name.  This method is protected to simplify the API.
	 * 
	 * @return the property name
	 */
	protected String getPropertyName() {
		return propertyName;
	}
	
	/**
	 * Returns the property value.  This method is protected to simplify the API.
	 * 
	 * @return the property value
	 */
	protected T getPropertyValue() {
		return propertyValue;
	}

}
