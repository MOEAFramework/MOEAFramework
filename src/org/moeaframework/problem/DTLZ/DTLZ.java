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
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.validate.Validate;

/**
 * Implements methods shared by all problems in the DTLZ test suite.  The DTLZ problems define a {@code g(X_M)}
 * function, which controls the convergence or distance to the Pareto front, and the {@code f_1(x), ..., f_M(x)}
 * functions controlling the shape or position of solutions on the Pareto front.
 */
public abstract class DTLZ extends AbstractProblem {

	/**
	 * Constructs a new DTLZ problem instance with the specified number of variables and objectives.
	 * 
	 * @param numberOfVariables the number of variables for this problem
	 * @param numberOfObjectives the number of objectives for this problem
	 */
	public DTLZ(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
		
		Validate.that("numberOfVariables", numberOfVariables).isGreaterThanOrEqualTo(numberOfObjectives);
		Validate.that("numberOfObjectives", numberOfObjectives).isGreaterThanOrEqualTo(2);
	}
	
	@Override
	public String getName() {
		return getClass().getSimpleName() + "_" + numberOfObjectives;
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, numberOfObjectives);

		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(0.0, 1.0));
		}

		return solution;
	}
	
	/**
	 * Computes the {@code g(X_M)} function used by {@link DTLZ1} and {@link DTLZ3}.  Note that while the entire array
	 * of decision variables is provided to this method, only the last {@code k = D - M + 1} variables are used in
	 * this calculation.
	 * 
	 * @param x the array of decision variable values
	 * @return the computed value of the {@code g(X_M)} function
	 */
	protected double g1(double[] x) {
		int k = numberOfVariables - numberOfObjectives + 1;
		double g = 0.0;
		
		for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
			g += Math.pow(x[i] - 0.5, 2.0) - Math.cos(20.0 * Math.PI * (x[i] - 0.5));
		}
		
		return 100.0 * (k + g);
	}
	
	/**
	 * Computes the {@code g(X_M)} function used by {@link DTLZ2}, {@link DTLZ4}, and {@link DTLZ5}.  Note that while
	 * the entire array of decision variables is provided to this method, only the last {@code k = D - M + 1} variables
	 * are used in this calculation.
	 * 
	 * @param x the array of decision variable values
	 * @return the computed value of the {@code g(X_M)} function
	 */
	protected double g2(double[] x) {
		int k = numberOfVariables - numberOfObjectives + 1;
		double g = 0.0;
		
		for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
			g += Math.pow(x[i] - 0.5, 2.0);
		}
		
		return g;
	}
	
	/**
	 * Generates a random Pareto optimal solution given the optimal value for the {@code K} decision variables used in
	 * the calculation of {@code g(X_M)}.
	 * 
	 * @param optimalValue the decision variable value that produces an optimal value for {@code g(X_M)}
	 * @return the solution
	 */
	protected Solution generateAt(double optimalValue) {
		Solution solution = newSolution();

		for (int i = 0; i < numberOfObjectives - 1; i++) {
			RealVariable.setReal(solution.getVariable(i), PRNG.nextDouble());
		}

		for (int i = numberOfObjectives - 1; i < numberOfVariables; i++) {
			RealVariable.setReal(solution.getVariable(i), optimalValue);
		}

		evaluate(solution);

		return solution;
	}

}
