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
import org.moeaframework.core.variable.EncodingUtils;

/**
 * The ZDT6 test problem.
 */
public class ZDT6 extends ZDT {

	/**
	 * Constructs a ZDT6 test problem with 10 decision variables.
	 */
	public ZDT6() {
		this(10);
	}

	/**
	 * Constructs a ZDT6 test problem with the specified number of decision
	 * variables.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public ZDT6(int numberOfVariables) {
		super(numberOfVariables);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);

		double f = 1.0 - Math.exp(-4.0 * x[0])
				* Math.pow(Math.sin(6.0 * Math.PI * x[0]), 6.0);

		double g = 0.0;
		for (int i = 1; i < numberOfVariables; i++) {
			g += x[i];
		}
		g = 1.0 + 9.0 * Math.pow(g / (numberOfVariables - 1), 0.25);

		double h = 1.0 - Math.pow(f / g, 2.0);

		solution.setObjective(0, f);
		solution.setObjective(1, g * h);
	}

}
