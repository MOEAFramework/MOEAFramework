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
package org.moeaframework.problem.CEC2009;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The unconstrained UF4 test problem from the CEC 2009 special session and competition.
 */
public class UF4 extends AbstractProblem {

	/**
	 * Constructs a UF4 test problem with 30 decision variables.
	 */
	public UF4() {
		this(30);
	}

	/**
	 * Constructs a UF4 test problem with the specified number of decision variables.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public UF4(int numberOfVariables) {
		super(numberOfVariables, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = RealVariable.getReal(solution);
		double[] f = new double[2];

		CEC2009.UF4(x, f, numberOfVariables);

		solution.setObjectiveValues(f);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, 2);

		solution.setVariable(0, new RealVariable(0.0, 1.0));
		for (int i = 1; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(-2.0, 2.0));
		}

		return solution;
	}

}
