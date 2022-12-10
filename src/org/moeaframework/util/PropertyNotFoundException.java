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
package org.moeaframework.util;

import org.moeaframework.core.FrameworkException;

/**
 * Thrown when attempting to read a property and no default value was given.
 */
public class PropertyNotFoundException extends FrameworkException {

	private static final long serialVersionUID = 2355313923356588354L;

	/**
	 * Creates an exception indicating the given property was not found.
	 * 
	 * @param propertyName the name of the property
	 */
	public PropertyNotFoundException(String propertyName) {
		super("property '" + propertyName + "' is not set and no default given");
	}

}
