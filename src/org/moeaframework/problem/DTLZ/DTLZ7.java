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

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AnalyticalProblem;

/**
 * The DTLZ7 test problem.
 */
public class DTLZ7 extends DTLZ implements AnalyticalProblem {

	/**
	 * Constructs a DTLZ7 test problem with the specified number of objectives.  This is equivalent to calling
	 * {@code new DTLZ7(numberOfObjectives+19, numberOfObjectives)}
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public DTLZ7(int numberOfObjectives) {
		this(numberOfObjectives + 19, numberOfObjectives);
	}

	/**
	 * Constructs a DTLZ7 test problem with the specified number of variables and objectives.
	 * 
	 * @param numberOfVariables the number of variables for this problem
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public DTLZ7(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
	}
	
	private double g(double[] x) {
		int k = numberOfVariables - numberOfObjectives + 1;
		double g = 0.0;
		
		for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
			g += x[i];
		}
		
		return 1.0 + (9.0 * g) / k;
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = RealVariable.getReal(solution);
		double[] f = new double[numberOfObjectives];
		double g = g(x);

		double h = numberOfObjectives;
		for (int i = 0; i < numberOfObjectives - 1; i++) {
			h -= x[i] / (1.0 + g) * (1.0 + Math.sin(3.0 * Math.PI * x[i]));
		}

		for (int i = 0; i < numberOfObjectives - 1; i++) {
			f[i] = x[i];
		}
		
		f[numberOfObjectives - 1] = (1.0 + g) * h;

		solution.setObjectiveValues(f);
	}

	@Override
	public Solution generate() {
		return generateAt(0.0);
	}

}
