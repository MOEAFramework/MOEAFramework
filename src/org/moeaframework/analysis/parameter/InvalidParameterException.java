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
package org.moeaframework.analysis.parameter;

import org.moeaframework.core.FrameworkException;

/**
 * An exception indicating a parameter is invalid.
 */
public class InvalidParameterException extends FrameworkException {

	private static final long serialVersionUID = -8402217875283482661L;

	/**
	 * Constructs a new exception for an invalid parameter.
	 * 
	 * @param parameterName the name of the parameter
	 * @param message the reason the parameter is invalid
	 */
	public InvalidParameterException(String parameterName, String message) {
		super(parameterName + " is invalid: " + message);
	}

}
