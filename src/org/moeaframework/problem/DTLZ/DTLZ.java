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
package org.moeaframework.problem.DTLZ;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.AnalyticalProblem;

/**
 * Implements methods shared by all problems in the DTLZ test suite.
 */
public abstract class DTLZ extends AbstractProblem  implements AnalyticalProblem {

	/**
	 * Constructs a new DTLZ problem instance with the specified number of variables and objectives.
	 * 
	 * @param numberOfVariables the number of variables for this problem
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public DTLZ(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
	}
	
	/**
	 * Computes the {@code g(X_M)} function used by the DTLZ problems.  This function controls the distance to the
	 * Pareto front.  While the entire decision variable array is passed as an argument, {@code X_M} typically only
	 * references the last {@code k = n - M + 1} variables, where {@code n} is the total number of decision variables
	 * and {@code M} is the number of objectives
	 * 
	 * @param x the decision variables
	 * @return the value of the {@code g(X_M)} function
	 */
	protected abstract double g(double[] x);

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives);

		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(0.0, 1.0));
		}

		return solution;
	}

}
