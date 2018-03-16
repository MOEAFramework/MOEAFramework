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
package org.moeaframework.core;

import java.io.IOException;

/**
 * The framework exception is the parent type of all exceptions specific to the
 * MOEA Framework.  Framework exceptions only cover exceptional cases caused by
 * the MOEA Framework itself; user exceptions are still covered by Java's
 * built-in exceptions (i.e., {@link IllegalArgumentException}) or those 
 * defined in third-party libraries.  Unhandled checked exceptions, such as
 * {@link IOException}, should be wrapped in an appropriate framework exception.
 */
public class FrameworkException extends RuntimeException {

	private static final long serialVersionUID = 2158953778977260013L;

	/**
	 * Constructs a new framework exception with no message or cause.
	 */
	public FrameworkException() {
		super();
	}

	/**
	 * Constructs a new framework exception with the specified message and
	 * cause.
	 * 
	 * @param message the message describing this exception
	 * @param cause the cause of this exception
	 */
	public FrameworkException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new framework exception with the specified message.
	 * 
	 * @param message the message describing this exception
	 */
	public FrameworkException(String message) {
		super(message);
	}

	/**
	 * Constructs a new framework exception with the specified cause.
	 * 
	 * @param cause the cause of this exception
	 */
	public FrameworkException(Throwable cause) {
		super(cause);
	}

}
