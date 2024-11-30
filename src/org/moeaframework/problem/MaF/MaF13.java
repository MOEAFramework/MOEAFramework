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

import org.apache.commons.math3.stat.StatUtils;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.AnalyticalProblem;
import org.moeaframework.util.Vector;
import org.moeaframework.util.validate.Validate;

/**
 * The MaF13 test problem.  This problem exhibits the following properties:
 * <ul>
 *   <li>Concave Pareto front
 *   <li>Unimodal
 *   <li>Non-separable decision variables
 *   <li>Degenerate
 * </ul>
 */
public class MaF13 extends AbstractProblem implements AnalyticalProblem {
	
	/**
	 * Constructs an MaF13 test problem with the specified number of objectives.
	 * 
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public MaF13(int numberOfObjectives) {
		super(5, numberOfObjectives);
		Validate.that("numberOfObjectives", numberOfObjectives).isGreaterThanOrEqualTo(3);
	}
	
	// Computes the sum of squares of y given the indices of J_i
	private double func(double[] y, int start, int step, int end) {
		double sum = 0.0;
		int count = 0;
		
		for (int i = start; i < end; i += step) {
			sum += Math.pow(y[i], 2.0);
			count += 1;
		}
		
		return sum / count;
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = RealVariable.getReal(solution);
		double[] f = new double[numberOfObjectives];
		double[] y = new double[numberOfVariables];
		
		for (int i = 0; i < numberOfVariables; i++) {
			y[i] = x[i] - 2.0 * x[1] * Math.sin(2.0 * Math.PI * x[0] + (i + 1) * Math.PI / numberOfVariables);
		}
		
		f[0] = Math.sin(x[0] * Math.PI / 2.0) + 2.0 * func(y, 3, 3, numberOfVariables);
		f[1] = Math.cos(x[0] * Math.PI / 2.0) * Math.sin(x[1] * Math.PI / 2.0) + 2.0 * func(y, 4, 3, numberOfVariables);
		f[2] = Math.cos(x[0] * Math.PI / 2.0) * Math.cos(x[1] * Math.PI / 2.0) + 2.0 * func(y, 2, 3, numberOfVariables);
		
		for (int i = 3; i < numberOfObjectives; i++) {
			f[i] = Math.pow(f[0], 2.0) + Math.pow(f[1], 10.0) + Math.pow(f[2], 10.0) + 2 * func(y, 3, 1, numberOfVariables);
		}

		solution.setObjectiveValues(f);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives);
		
		solution.setVariable(0, new RealVariable(0.0, 1.0));
		solution.setVariable(1, new RealVariable(0.0, 1.0));
		
		for (int i = 2; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(-2.0, 2.0));
		}

		return solution;
	}
	
	@Override
	public Solution generate() {
		double[] p = Vector.uniform(3);
		double divisor = Math.sqrt(StatUtils.sumSq(p));
		
		for (int i = 0; i < 3; i++) {
			p[i] /= divisor;
		}
		
		double remainder = Math.pow(p[0], 2.0) + Math.pow(p[1], 10.0) + Math.pow(p[2], 10.0);
		Solution solution = new Solution(0, getNumberOfObjectives());
		
		for (int i = 0; i < getNumberOfObjectives(); i++) {
			solution.setObjectiveValue(i, i < 3 ? p[i] : remainder);
		}
		
		return solution;
	}

}
