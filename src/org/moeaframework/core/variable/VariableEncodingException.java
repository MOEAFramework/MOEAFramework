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
package org.moeaframework.core.variable;

import org.moeaframework.core.FrameworkException;

/**
 * Thrown when a variable could not be encoded or decoded.
 */
public class VariableEncodingException extends FrameworkException {

	private static final long serialVersionUID = -21772456210784197L;

	/**
	 * Creates an exception indicating an issue encoding or decoding a variable.
	 * 
	 * @param message the error message
	 */
	public VariableEncodingException(String message) {
		super(message);
	}
	
	/**
	 * Creates an exception indicating an issue encoding or decoding a variable.
	 * 
	 * @param message the error message
	 * @param cause the underlying cause
	 */
	public VariableEncodingException(String message, Throwable cause) {
		super(message, cause);
	}

}
