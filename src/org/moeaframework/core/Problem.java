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

/**
 * Interface for defining optimization problems.  All methods must be thread
 * safe.
 */
public interface Problem {

	/**
	 * Returns the user-friendly name for this problem.
	 * 
	 * @return the user-friendly name for this problem
	 */
	public String getName();

	/**
	 * Returns the number of decision variables defined by this problem.
	 * 
	 * @return the number of decision variables defined by this problem
	 */
	public int getNumberOfVariables();

	/**
	 * Returns the number of objectives defined by this problem.
	 * 
	 * @return the number of objectives defined by this problem
	 */
	public int getNumberOfObjectives();

	/**
	 * Returns the number of constraints defined by this problem.
	 * 
	 * @return the number of constraints defined by this problem
	 */
	public int getNumberOfConstraints();

	/**
	 * Evaluates the solution, updating the solution's objectives in place.
	 * Algorithms must explicitly call this method when appropriate to evaluate
	 * new solutions or reevaluate modified solutions.
	 * 
	 * @param solution the solution to be evaluated
	 */
	public void evaluate(Solution solution);

	/**
	 * Returns a new solution for this problem. Implementations must initialize
	 * the variables so that the valid range of values is defined, but may leave
	 * the actual value at a default or undefined state.
	 * 
	 * @return a new solution for this problem
	 */
	public Solution newSolution();
	
	/**
	 * Closes any underlying resources used by this problem.  Once closed,
	 * further invocations of any methods on this problem may throw exceptions.
	 */
	public void close();

}
