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
package org.moeaframework.problem.ZDT;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;

/**
 * The ZDT3 test problem.
 */
public class ZDT3 extends ZDT {

	/**
	 * Constructs a ZDT3 test problem with 30 decision variables.
	 */
	public ZDT3() {
		this(30);
	}

	/**
	 * Constructs a ZDT3 test problem with the specified number of decision variables.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public ZDT3(int numberOfVariables) {
		super(numberOfVariables);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = RealVariable.getReal(solution);

		double g = 0.0;
		for (int i = 1; i < x.length; i++) {
			g += x[i];
		}
		g = (9.0 / (numberOfVariables - 1)) * g + 1.0;

		double h = 1.0 - Math.sqrt(x[0] / g) - (x[0] / g) * Math.sin(10.0 * Math.PI * x[0]);

		solution.setObjectiveValue(0, x[0]);
		solution.setObjectiveValue(1, g * h);
	}

}
