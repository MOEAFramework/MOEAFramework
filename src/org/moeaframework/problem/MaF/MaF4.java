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
package org.moeaframework.problem.MaF;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AnalyticalProblem;
import org.moeaframework.problem.DTLZ.DTLZ;

/**
 * The MaF4 test problem, which is an inverted and badly scaled version of the DTLZ3 problem.  This problem exhibits
 * the following properties:
 * <ul>
 *   <li>Concave Pareto front
 *   <li>Multimodal
 *   <li>Badly scaled decision variables
 *   <li>No single optimal solution in any subset of the objectives
 * </ul>
 */
public class MaF4 extends DTLZ implements AnalyticalProblem {

	/**
	 * Constructs an MaF4 test problem with the specified number of objectives.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public MaF4(int numberOfObjectives) {
		super(numberOfObjectives + 9, numberOfObjectives);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = RealVariable.getReal(solution);
		double[] f = new double[numberOfObjectives];
		final double a = 2.0;
		double g = g1(x);

		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = 1.0;

			for (int j = 0; j < numberOfObjectives - i - 1; j++) {
				f[i] *= Math.cos(0.5 * Math.PI * x[j]);
			}

			if (i != 0) {
				f[i] *= Math.sin(0.5 * Math.PI * x[numberOfObjectives - i - 1]);
			}
			
			f[i] = Math.pow(a, i + 1) * (1.0 - f[i]) * (1.0 + g);
		}

		solution.setObjectiveValues(f);
	}
	
	@Override
	public Solution generate() {
		return generateAt(0.5);
	}

}
