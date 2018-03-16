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
package org.moeaframework.problem.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Kursawe test problem. According to a personal correspondence between Van
 * Veldhuizen and Marco Laumanns, a misprint exists in Kursawe's paper. This
 * implementation uses the corrected version.
 * <p>
 * Properties:
 * <ul>
 *   <li>Disconnected, symmetric Pareto set
 *   <li>Disconnected, concave Pareto front
 *   <li>Scalable number of variables
 * </ul>
 * <p>
 * References:
 * <ol>
 * <li>Kursawe, F. "A Variant of Evolution Strategies for Vector Optimization."
 * Parallel Problem Solving from Nature, pp. 193-197, 1991.
 * <li>Van Veldhuizen, D. "Multiobjective Evolutionary Algorithms:
 * Classifications, Analyses, and New Innovations." Ph.D. Dissertation. The Air
 * Force Institute of Technology, Air University, 1999.
 * </ol>
 */
public class Kursawe extends AbstractProblem {

	/**
	 * The lower bound for decision variables.
	 */
	private final double lowerBound;

	/**
	 * The upper bound for decision variables.
	 */
	private final double upperBound;
	
	/**
	 * Constructs the Kursawe problem with {@code 3} decision variables.
	 */
	public Kursawe() {
		this(3);
	}

	/**
	 * Constructs the Kursawe problem with the specified number of decision
	 * variables.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
	public Kursawe(int numberOfVariables) {
		this(numberOfVariables, -5.0, 5.0);
	}

	/**
	 * Constructs the Kursawe problem with the specified number of decision
	 * variables.
	 * 
	 * @param numberOfVariables the number of decision variables
	 * @param lowerBound the lower bound for decision variables
	 * @param upperBound the upper bound for decision variables
	 */
	public Kursawe(int numberOfVariables, double lowerBound, 
			double upperBound) {
		super(numberOfVariables, 2);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double f1 = 0.0;
		double f2 = 0.0;
		
		for (int i = 0; i < numberOfVariables - 1; i++) {
			f1 += -10.0 * Math.exp(-0.2 * Math.sqrt(
					Math.pow(x[i], 2.0) + Math.pow(x[i+1], 2.0)));
		}

		for (int i = 0; i < numberOfVariables; i++) {
			f2 += Math.pow(Math.abs(x[i]), 0.8) + 
					5.0 * Math.sin(Math.pow(x[i], 3.0));
		}

		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, 2);

		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, new RealVariable(lowerBound, upperBound));
		}

		return solution;
	}

}
