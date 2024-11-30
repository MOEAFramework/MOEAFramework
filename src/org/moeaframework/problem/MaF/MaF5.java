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
 * The MaF5 test problem, which is a convex and badly scaled version of the DTLZ4 problem.  This problem exhibits
 * the following properties:
 * <ul>
 *   <li>Convex Pareto front
 *   <li>Badly scaled decision variables
 *   <li>Biased
 * </ul>
 */
public class MaF5 extends DTLZ implements AnalyticalProblem {

	/**
	 * Constructs an MaF5 test problem with the specified number of objectives.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public MaF5(int numberOfObjectives) {
		super(numberOfObjectives + 9, numberOfObjectives);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = RealVariable.getReal(solution);
		double[] f = new double[numberOfObjectives];
		final double alpha = 100.0;
		final double a = 2.0;
		double g = g2(x);

		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = 1.0 + g;

			for (int j = 0; j < numberOfObjectives - i - 1; j++) {
				f[i] *= Math.cos(0.5 * Math.PI * Math.pow(x[j], alpha));
			}

			if (i != 0) {
				f[i] *= Math.sin(0.5 * Math.PI * Math.pow(x[numberOfObjectives - i - 1], alpha));
			}
			
			f[i] = Math.pow(a, numberOfObjectives - i) * f[i];
		}

		solution.setObjectiveValues(f);
	}
	
	@Override
	public Solution generate() {
		return generateAt(0.5);
	}

}
