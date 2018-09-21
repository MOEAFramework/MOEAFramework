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

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.AnalyticalProblem;

/**
 * The Murata problem.  The optimum is defined by {@code (x, 2)} where
 * {@code 1 <= x < = 4}.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected Pareto set
 *   <li>Concave Pareto front
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Murata, T. and Ishibuchi, H. (1995).  "MOGA: Multi-Objective Genetic
 *       Algorithms."  IEEE International Conference on Evolutionary
 *       Computation, pp. 289-294.
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Murata extends AbstractProblem implements AnalyticalProblem {

	/**
	 * Constructs the Murata problem.
	 */
	public Murata() {
		super(2, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		double y = EncodingUtils.getReal(solution.getVariable(1));
		double f1 = 2.0 * Math.sqrt(x);
		double f2 = x * (1.0 - y) + 5.0;
		
		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2);
		
		solution.setVariable(0, EncodingUtils.newReal(1, 4));
		solution.setVariable(1, EncodingUtils.newReal(1, 2));
		
		return solution;
	}

	@Override
	public Solution generate() {
		Solution solution = newSolution();
		
		EncodingUtils.setReal(solution.getVariable(0),
				PRNG.nextDouble(1.0, 4.0));
		EncodingUtils.setReal(solution.getVariable(1), 2.0);
		
		evaluate(solution);
		return solution;
	}

}
