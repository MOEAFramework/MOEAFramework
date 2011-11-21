/* Copyright 2009-2011 David Hadka
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

import org.moeaframework.core.CoreUtils;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;

/**
 * The Rosenbrock problem.
 * <p>
 * References:
 * <ol>
 *   <li>Rosenbrock, H. H.  "An automatic method for finding the greatest or 
 *       least value of a function."  The Computer Journal, 3:175–184, 1960.
 * </ol>
 */
public class Rosenbrock extends AbstractProblem {

	/**
	 * Constructs a Rosenbrock problem instance with the specified number of
	 * decision variables.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public Rosenbrock(int numberOfVariables) {
		super(numberOfVariables, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		double sum = 0.0;
		double[] x = CoreUtils.castVariablesToDoubleArray(solution);

		for (int i = 0; i < numberOfVariables - 1; i++) {
			sum += Math.pow(1.0 - x[i], 2.0) + 100.0
					* Math.pow(x[i + 1] - Math.pow(x[i], 2.0), 2.0);
		}

		solution.setObjective(0, sum);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, 1);

		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(-10.0, 10.0));
		}

		return solution;
	}

}
