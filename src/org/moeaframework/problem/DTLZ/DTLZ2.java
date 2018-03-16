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
package org.moeaframework.problem.DTLZ;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;

/**
 * The DTLZ2 test problem.
 */
public class DTLZ2 extends DTLZ {

	/**
	 * Constructs a DTLZ2 test problem with the specified number of objectives.
	 * This is equivalent to calling {@code new DTLZ2(numberOfObjectives+9,
	 * numberOfObjectives)}.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public DTLZ2(int numberOfObjectives) {
		this(numberOfObjectives + 9, numberOfObjectives);
	}

	/**
	 * Constructs a DTLZ2 test problem with the specified number of variables
	 * and objectives.
	 * 
	 * @param numberOfVariables the number of variables for this problem
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public DTLZ2(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
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

		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = 1.0 + g;

			for (int j = 0; j < numberOfObjectives - i - 1; j++) {
				f[i] *= Math.cos(0.5 * Math.PI * x[j]);
			}

			if (i != 0) {
				f[i] *= Math.sin(0.5 * Math.PI * x[numberOfObjectives - i - 1]);
			}
		}

		solution.setObjectives(f);
	}

	@Override
	public Solution generate() {
		Solution solution = newSolution();

		for (int i = 0; i < numberOfObjectives - 1; i++) {
			((RealVariable)solution.getVariable(i)).setValue(PRNG.nextDouble());
		}

		for (int i = numberOfObjectives - 1; i < numberOfVariables; i++) {
			((RealVariable)solution.getVariable(i)).setValue(0.5);
		}

		evaluate(solution);

		return solution;
	}

}
