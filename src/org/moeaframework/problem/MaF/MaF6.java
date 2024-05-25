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
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The MaF6 test problem, also known as the "DTLZ5(I,M)" problem with {@code I=2}.  This problem exhibits the following
 * properties:
 * <ul>
 *   <li>Concave Pareto front
 *   <li>Degenerate
 * </ul>
 */
public class MaF6 extends AbstractProblem {
	
	/**
	 * Constructs an MaF6 test problem with the specified number of objectives.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public MaF6(int numberOfObjectives) {
		super(numberOfObjectives + 9, numberOfObjectives);
	}
	
	protected double g(double[] x) {
		int k = numberOfVariables - numberOfObjectives + 1;
		double g = 0.0;
		
		for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
			g += Math.pow(x[i] - 0.5, 2.0);
		}
		
		return g;
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double[] f = new double[numberOfObjectives];
		final int I = 2;
		double g = g(x);
		
		double[] theta = new double[numberOfObjectives - 1];
		for (int i = 0; i < numberOfObjectives - 1; i++) {
			theta[i] = i < I - 1 ? 0.5 * Math.PI * x[i] : Math.PI / (4.0 * (1.0 + g)) * (1.0 + 2.0 * g * x[i]);
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = 1.0 + 100.0 * g;

			for (int j = 0; j < numberOfObjectives - i - 1; j++) {
				f[i] *= Math.cos(theta[j]);
			}

			if (i != 0) {
				f[i] *= Math.sin(theta[numberOfObjectives - i - 1]);
			}
		}

		solution.setObjectives(f);
	}
	
	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives);

		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(0.0, 1.0));
		}

		return solution;
	}

}
