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
 * The unconstrained UF13 test problem from the CEC 2009 special session and
 * competition.
 */
public class UF13 extends AbstractProblem {

	/**
	 * Constructs a UF13 test problem with 30 decision variables and 5
	 * objectives.
	 */
	public UF13() {
		this(30, 5);
	}

	/**
	 * Constructs a UF13 test problem with the specified number of decision
	 * variables and objectives.
	 * 
	 * @param numberOfVariables the number of decision variables
	 * @param numberOfObjectives the number of objectives
	 */
	public UF13(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = CoreUtils.castVariablesToDoubleArray(solution);
		double[] f = new double[numberOfObjectives];

		CEC2009.WFG1_M5(x, f, numberOfVariables, numberOfObjectives);

		solution.setObjectives(f);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives);

		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(0.0, 2.0 * (i + 1)));
		}

		return solution;
	}

}
