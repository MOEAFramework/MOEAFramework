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
package org.moeaframework.problem.ZDT;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryVariable;

/**
 * The ZDT5 test problem.
 */
public class ZDT5 extends ZDT {

	/**
	 * Constructs a ZDT5 test problem with 11 decision variables.
	 */
	public ZDT5() {
		super(11);
	}

	/**
	 * Constructs a ZDT5 test problem with the specified number of decision
	 * variables.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public ZDT5(int numberOfVariables) {
		super(numberOfVariables);
	}

	@Override
	public void evaluate(Solution solution) {
		double f = 1 + ((BinaryVariable)solution.getVariable(0)).cardinality();

		double g = 0.0;
		for (int i = 1; i < numberOfVariables; i++) {
			g += v(((BinaryVariable)solution.getVariable(i)).cardinality());
		}

		double h = 1.0 / f;

		solution.setObjective(0, f);
		solution.setObjective(1, g * h);
	}

	private double v(int cardinality) {
		return cardinality < 5 ? 2 + cardinality : 1;
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, 2);

		solution.setVariable(0, new BinaryVariable(30));

		for (int i = 1; i < numberOfVariables; i++) {
			solution.setVariable(i, new BinaryVariable(5));
		}

		return solution;
	}

}
