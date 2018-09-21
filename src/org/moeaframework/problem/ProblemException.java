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
package org.moeaframework.problem;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Problem;

/**
 * An exception that originated from a problem.
 */
public class ProblemException extends FrameworkException {

	private static final long serialVersionUID = -1342333343341450305L;
	
	/**
	 * The problem responsible for this exception.
	 */
	private final Problem problem;

	/**
	 * Constructs an problem exception originating from the specified
	 * problem.
	 * 
	 * @param problem the problem responsible for this exception
	 */
	public ProblemException(Problem problem) {
		super();
		this.problem = problem;
	}

	/**
	 * Constructs an problem exception originating from the specified
	 * problem with the given cause.
	 * 
	 * @param problem the problem responsible for this exception
	 * @param cause the cause of this exception
	 */
	public ProblemException(Problem problem, String message, Throwable cause) {
		super(message, cause);
		this.problem = problem;
	}

	/**
	 * Constructs an problem exception originating from the specified
	 * problem with the given message.
	 * 
	 * @param problem the problem responsible for this exception
	 * @param message the message describing this exception
	 */
	public ProblemException(Problem problem, String message) {
		super(message);
		this.problem = problem;
	}

	/**
	 * Constructs an problem exception originating from the specified
	 * problem with the given cause.
	 * 
	 * @param problem the problem responsible for this exception
	 * @param cause the cause of this exception
	 */
	public ProblemException(Problem problem, Throwable cause) {
		super(cause);
		this.problem = problem;
	}

	/**
	 * Returns the problem responsible for this exception.
	 * 
	 * @return the problem responsible for this exception
	 */
	public Problem getProblem() {
		return problem;
	}

}
