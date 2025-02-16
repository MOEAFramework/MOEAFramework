/* Copyright 2009-2025 David Hadka
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
 * The DTLZ6 test problem.
 * <p>
 * <strong>Note: The original DTLZ5 and DTLZ6 problems have a known defect where they produce additional solutions than
 * expected.  See Deb and Saxena (2006) for more details.</strong>
 * <p>
 * References:
 * <ol>
 *   <li>Deb, K. and Saxena, D. K. (2006). Searching for Pareto-optimal solutions through dimensionality reduction
 *       for certain large-dimensional multi-objective optimization problems.  In The 2006 IEEE Congress on
 *       Evolutionary Computation, pages 3353–3360.
 * </ol>
 */
public class DTLZ6 extends DTLZ implements AnalyticalProblem {

	/**
	 * Constructs a DTLZ6 test problem with the specified number of objectives.  This is equivalent to calling
	 * {@code new DTLZ6(numberOfObjectives+9, numberOfObjectives)}.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public DTLZ6(int numberOfObjectives) {
		this(numberOfObjectives + 9, numberOfObjectives);
	}

	/**
	 * Constructs a DTLZ6 test problem with the specified number of variables and objectives.
	 * 
	 * @param numberOfVariables the number of variables for this problem
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public DTLZ6(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
		
		if (numberOfObjectives > 3) {
			System.err.println("WARNING: DLTZ6 is known to produce additional solutions than expected for M > 3");
		}
	}
	
	private double g(double[] x) {
		int k = numberOfVariables - numberOfObjectives + 1;
		double g = 0.0;
		
		for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
			g += Math.pow(x[i], 0.1);
		}
		
		return g;
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = RealVariable.getReal(solution);
		double[] f = new double[numberOfObjectives];
		double g = g(x);
		
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

		solution.setObjectiveValues(f);
	}

	@Override
	public Solution generate() {
		return generateAt(0.0);
	}

}
