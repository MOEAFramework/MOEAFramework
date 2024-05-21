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
package org.moeaframework.core.penalty;

import org.moeaframework.core.Solution;

/**
 * Interface for penalty functions.  The penalty is used to offset the fitness / objective values of a solution,
 * essentially converting it into an unconstrained problem.  Penalty functions are often classified as being
 * <strong>static</strong>, where the penalty only depends on the constraint violation, <strong>dynamic</strong>,
 * where the penalty scales based on the current generation / NFE, and <strong>adaptive</strong>, where the penalty
 * scales based on how successful the algorithm is at finding feasible solutions.
 */
public interface PenaltyFunction {

	/**
	 * Calculates the penalty for the given solution.  In addition to returning the value to the caller, this method
	 * also sets the {@link org.moeaframework.core.attribute.Penalty} attribute on the solution.
	 * <p>
	 * Calling this method does not alter the objectives or constraints of the solution.  It is the responsibility of
	 * the caller to use the penalty value, such as during selection.  We encourage implementations to return a value
	 * of {@code 0.0} for feasible solutions and {@code > 0.0} for solutions violating constraints.
	 * 
	 * @param solution the solution
	 * @return the penalty for the solution
	 */
	public double calculate(Solution solution);

}
