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
 * The DTLZ7 test problem.
 */
public class DTLZ7 extends DTLZ {

	/**
	 * Constructs a DTLZ7 test problem with the specified number of objectives.
	 * This is equivalent to calling {@code new DTLZ7(numberOfObjectives+19, 
	 * numberOfObjectives)}
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public DTLZ7(int numberOfObjectives) {
		this(numberOfObjectives + 19, numberOfObjectives);
	}

	/**
	 * Constructs a DTLZ7 test problem with the specified number of variables
	 * and objectives.
	 * 
	 * @param numberOfVariables the number of variables for this problem
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public DTLZ7(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double[] f = new double[numberOfObjectives];

		int k = numberOfVariables - numberOfObjectives + 1;

		double g = 0.0;
		for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
			g += x[i];
		}
		g = 1.0 + (9.0 * g) / k;

		double h = numberOfObjectives;
		for (int i = 0; i < numberOfObjectives - 1; i++) {
			h -= x[i] / (1.0 + g) * (1.0 + Math.sin(3.0 * Math.PI * x[i]));
		}

		for (int i = 0; i < numberOfObjectives - 1; i++) {
			f[i] = x[i];
		}
		f[numberOfObjectives - 1] = (1.0 + g) * h;

		solution.setObjectives(f);
	}

	@Override
	public Solution generate() {
		Solution solution = newSolution();

		for (int i = 0; i < numberOfObjectives - 1; i++) {
			((RealVariable)solution.getVariable(i)).setValue(PRNG.nextDouble());
		}

		for (int i = numberOfObjectives - 1; i < numberOfVariables; i++) {
			((RealVariable)solution.getVariable(i)).setValue(0.0);
		}

		evaluate(solution);

		return solution;
	}

}
