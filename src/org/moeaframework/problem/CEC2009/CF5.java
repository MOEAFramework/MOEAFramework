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
import org.moeaframework.core.constraint.GreaterThanOrEqual;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The constrained CF5 test problem from the CEC 2009 special session and competition.
 */
public class CF5 extends AbstractProblem {

	/**
	 * Constructs a CF5 test problem with 10 decision variables.
	 */
	public CF5() {
		this(10);
	}

	/**
	 * Constructs a CF5 test problem with the specified number of decision variables.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public CF5(int numberOfVariables) {
		super(numberOfVariables, 2, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = RealVariable.getReal(solution);
		double[] f = new double[2];
		double[] c = new double[1];

		CEC2009.CF5(x, f, c, numberOfVariables);

		solution.setObjectiveValues(f);
		solution.setConstraintValues(c);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, 2, 1);

		solution.setVariable(0, new RealVariable(0.0, 1.0));
		for (int i = 1; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(-2.0, 2.0));
		}
		
		solution.setConstraint(0, GreaterThanOrEqual.to(0.0));

		return solution;
	}

}
