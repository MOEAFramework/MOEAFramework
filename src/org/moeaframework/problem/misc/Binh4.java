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
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.AnalyticalProblem;

/**
 * The Binh (4) problem.  The global feasible optimum is at {@code (3.0, 0.5)}.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected Pareto set
 *   <li>Degenerate, concave Pareto front
 *   <li>Constrained
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Binh, T. T., and Korn, U. (1997).  "Multiobjective Evolution Strategy
 *       with Linear and Nonlinear Constraints."  Proc. of the 15th IMACS
 *       World Congress on Scientific Computation, Modeling and Applied
 *       Mathematics, pp. 357-362.
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Binh4 extends AbstractProblem implements AnalyticalProblem {

	/**
	 * Constructs the Binh (4) problem.
	 */
	public Binh4() {
		super(2, 3, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		double y = EncodingUtils.getReal(solution.getVariable(1));
		double f1 = 1.5 - x*(1.0 - y);
		double f2 = 2.25 - x*(1.0 - Math.pow(y, 2.0));
		double f3 = 2.625 - x*(1.0 - Math.pow(y, 3.0));
		double c1 = -Math.pow(x, 2.0) - Math.pow(y - 0.5, 2.0) + 9.0;
		double c2 = Math.pow(x - 1.0, 2.0) + Math.pow(y - 0.5, 2.0) - 6.25;
		
		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
		solution.setObjective(2, f3);
		solution.setConstraint(0, c1 <= 0.0 ? 0.0 : c1);
		solution.setConstraint(1, c2 <= 0.0 ? 0.0 : c2);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 3, 2);
		
		solution.setVariable(0, EncodingUtils.newReal(-10.0, 10.0));
		solution.setVariable(1, EncodingUtils.newReal(-10.0, 10.0));
		
		return solution;
	}

	@Override
	public Solution generate() {
		Solution solution = newSolution();
		
		EncodingUtils.setReal(solution.getVariable(0), 3.0);
		EncodingUtils.setReal(solution.getVariable(1), 0.5);
		
		evaluate(solution);
		return solution;
	}

}
