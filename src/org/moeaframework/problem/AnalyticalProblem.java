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
package org.moeaframework.problem;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Interface for problems whose Pareto optimal set is known analytically, providing the {@link #generate} method for
 * producing randomly-generated reference sets.
 */
public interface AnalyticalProblem extends Problem {

	/**
	 * Returns a randomly-generated solution using the analytical solution to this problem.  The exact behavior of this
	 * method depends on the implementation, but in general (1) the solutions should be non-dominated and (2) spread
	 * uniformly across the Pareto front.
	 * <p>
	 * It is not always possible to guarantee these conditions.  For example, a discontinuous / disconnected Pareto
	 * surface could generate dominated solutions, and a biased problem could result in non-uniform distributions.
	 * Therefore, we recommend callers filter solutions through a {@link NondominatedPopulation}, in particular one
	 * that maintains a spread of solutions.
	 * <p>
	 * Furthermore, some implementations may not provide the corresponding decision variables for the solution.  These
	 * implementations should indicate this by returning a solution with {@code 0} decision variables.
	 * 
	 * @return a randomly-generated Pareto optimal solution to this problem
	 */
	public Solution generate();

}
