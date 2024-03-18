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
package org.moeaframework.problem.DTLZ;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

/**
 * The DTLZ5 test problem.
 * <p>
 * <strong>Note: The original DTLZ5 and DTLZ6 problems have a known defect where they produce additional solutions than
 * expected.  See Deb and Saxena (2006) for more details.</strong>
 * <p>
 * References:
 * <ol>
 *   <li>Deb, K. and Saxena, D. K. (2006). Searching for Pareto-optimal solutions through dimensionality reduction
 *       for certain large-dimensional multi-objective optimization problems. In The 2006 IEEE Congress on Evolutionary
 *       Computation, pages 3353–3360.
 * </ol>
 */
public class DTLZ5 extends DTLZ {

	/**
	 * Constructs a DTLZ5 test problem with the specified number of objectives.  This is equivalent to calling
	 * {@code new DTLZ5(numberOfObjectives+9, numberOfObjectives)}.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public DTLZ5(int numberOfObjectives) {
		this(numberOfObjectives + 9, numberOfObjectives);
	}

	/**
	 * Constructs a DTLZ5 test problem with the specified number of variables and objectives.
	 * 
	 * @param numberOfVariables the number of variables for this problem
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public DTLZ5(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
		
		if (numberOfObjectives > 3) {
			System.err.println("DLTZ5 is known to produce additional solutions than expected for M > 3");
		}
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double[] f = new double[numberOfObjectives];

		int k = numberOfVariables - numberOfObjectives + 1;

		double g = 0.0;
		for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
			g += Math.pow(x[i] - 0.5, 2.0);
		}
		
		double[] theta = new double[numberOfObjectives - 1];
		for (int i = 0; i < numberOfObjectives - 1; i++) {
			theta[i] = i == 0 ? 0.5 * Math.PI * x[i] : Math.PI / (4.0 * (1.0 + g)) * (1.0 + 2.0 * g * x[i]);
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = 1.0 + g;

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
	public Solution generate() {
		Solution solution = newSolution();

		for (int i = 0; i < numberOfObjectives - 1; i++) {
			EncodingUtils.setReal(solution.getVariable(i), PRNG.nextDouble());
		}

		for (int i = numberOfObjectives - 1; i < numberOfVariables; i++) {
			EncodingUtils.setReal(solution.getVariable(i), 0.5);
		}

		evaluate(solution);

		return solution;
	}

}
