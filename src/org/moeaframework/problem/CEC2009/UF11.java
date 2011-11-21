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
package org.moeaframework.problem.CEC2009;

import org.moeaframework.core.CoreUtils;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The unconstrained UF11 test problem from the CEC 2009 special session and
 * competition.
 */
public class UF11 extends AbstractProblem {

	/**
	 * The decision variable lower and upper bounds for the instance with 10 
	 * decision variables.
	 */
	private static final double[][] bound_10D = {
			{ -1.118, -0.951, -2.055, -0.472, -1.070, 0, 0, 0, 0, 0 },
			{ 0.899, 1.257, 0, 1.244, 0.869, 1, 1, 1, 1, 1 } };

	/**
	 * The decision variable lower and upper bounds for the instance with 30 
	 * decision variables.
	 */
	private static final double[][] bound_30D = {
			{ -1.773, -1.846, -1.053, -2.370, -1.603, -1.878, -1.677, -0.935,
					-1.891, -0.964, -0.885, -1.690, -2.235, -1.541, -0.720,
					0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000,
					0.000, 0.000, 0.000, 0.000, 0.000, 0.000, 0.000 },
			{ 1.403, 1.562, 2.009, 0.976, 1.490, 1.334, 1.074, 2.354, 1.462,
					2.372, 2.267, 1.309, 0.842, 1.665, 2.476, 1.000, 1.000,
					1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000,
					1.000, 1.000, 1.000, 1.000, 1.000 } };

	/**
	 * Constructs a UF11 test problem with 30 decision variables and 5
	 * objectives.
	 */
	public UF11() {
		this(30, 5);
	}

	/**
	 * Constructs a UF11 test problem with the specified number of decision
	 * variables and objectives.
	 * 
	 * @param numberOfVariables the number of decision variables
	 * @param numberOfObjectives the number of objectives
	 */
	public UF11(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
		
		if ((numberOfVariables != 10) && (numberOfVariables != 30)) {
			throw new IllegalArgumentException(
					"number of variables must be 10 or 30");
		}
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = CoreUtils.castVariablesToDoubleArray(solution);
		double[] f = new double[numberOfObjectives];

		CEC2009.R2_DTLZ2_M5(x, f, numberOfVariables, numberOfObjectives);

		solution.setObjectives(f);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives);

		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i,
					new RealVariable(numberOfVariables == 10 ? bound_10D[0][i]
							: bound_30D[0][i],
							numberOfVariables == 10 ? bound_10D[1][i]
									: bound_30D[1][i]));
		}

		return solution;
	}

}
