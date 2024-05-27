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
import org.moeaframework.problem.DTLZ.DTLZ;

/**
 * The MaF2 test problem, also known as the "DTLZ2BZ" problem.  This problem exhibits the following
 * properties:
 * <ul>
 *   <li>Concave Pareto front
 *   <li>No single optimal solution in any subset of the objectives
 * </ul>
 */
public class MaF2 extends DTLZ {

	/**
	 * Constructs an MaF2 test problem with the specified number of objectives.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public MaF2(int numberOfObjectives) {
		super(numberOfObjectives + 9, numberOfObjectives);
	}
	
	private double g(double[] x, int i) {
		double g = 0.0;
		int factor = (int)((numberOfVariables - numberOfObjectives + 1) / (double)numberOfObjectives);
		int startingIndex = numberOfObjectives + i * factor - 1;
		int endingIndex = i == numberOfObjectives - 1 ? numberOfVariables : numberOfObjectives + (i + 1) * factor - 1;
				
		for (int j = startingIndex; j < endingIndex; j++) {
			g += Math.pow((x[j] / 2.0 + 0.25) - 0.5, 2.0);
		}
		
		return g;
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double[] f = new double[numberOfObjectives];
		double[] theta = new double[numberOfObjectives - 1];
		
		for (int i = 0; i < numberOfObjectives - 1; i++) {
			theta[i] = (Math.PI / 2.0) * (x[i] / 2.0 + 0.25);
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = 1.0 + g(x, i);

			for (int j = 0; j < numberOfObjectives - i - 1; j++) {
				f[i] *= Math.cos(theta[j]);
			}

			if (i != 0) {
				f[i] *= Math.sin(theta[numberOfObjectives - i - 1]);
			}
		}

		solution.setObjectives(f);
	}

}
