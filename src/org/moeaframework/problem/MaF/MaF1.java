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
 * The MaF1 test problem, which is a modified inverted version of the DTLZ1 test problem.  This problem exhibits the
 * following properties:
 * <ul>
 *   <li>Linear Pareto front
 *   <li>No single optimal solution in any subset of the objectives
 * </ul>
 */
public class MaF1 extends DTLZ implements AnalyticalProblem {

	/**
	 * Constructs an MaF1 test problem with the specified number of objectives.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public MaF1(int numberOfObjectives) {
		super(numberOfObjectives + 9, numberOfObjectives);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = RealVariable.getReal(solution);
		double[] f = new double[numberOfObjectives];
		double g = g2(x);
		double xmul = x[0];
		
		for (int i = numberOfObjectives - 2; i > 0; i--) {
			f[i] = 1.0 - xmul * (1.0 - x[i]);
			xmul *= x[i];
		}
		
		f[0] = 1.0 - xmul;
		f[numberOfObjectives - 1] = x[0];
		
		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] *= 1.0 + g;
		}

		solution.setObjectiveValues(f);
	}
	
	@Override
	public Solution generate() {
		return generateAt(0.5);
	}

}
