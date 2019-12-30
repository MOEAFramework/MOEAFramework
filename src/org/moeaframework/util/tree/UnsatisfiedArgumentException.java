/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.util.tree;

import org.moeaframework.core.FrameworkException;

/**
 * Exception indicating a tree node had one or more unsatisfied arguments
 * so no valid tree could be constructed.
 */
public class UnsatisfiedArgumentException extends FrameworkException {

	private static final long serialVersionUID = 7202226763114783268L;

	/**
	 * Exception indicating a tree node had one or more unsatisfied arguments.
	 * 
	 * @param message the message describing this exception
	 * @param cause the cause of this exception
	 */
	public UnsatisfiedArgumentException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Exception indicating a tree node had one or more unsatisfied arguments.
	 * 
	 * @param message the message describing this exception
	 */
	public UnsatisfiedArgumentException(String message) {
		super(message);
	}
	
}
