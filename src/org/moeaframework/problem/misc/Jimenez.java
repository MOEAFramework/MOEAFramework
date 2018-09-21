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
 * The Jimenez problem.  The Pareto set is defined by the line from
 * {@code (40, 15)} to {@code (50, 0)}.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected and symmetric Pareto set
 *   <li>Convex Pareto front
 *   <li>Constrained
 *   <li>Maximization (objectives are negated)
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Jimenez, F. and Verdegay, J. L. (1998).  "Constrained Multiobjective
 *       Optimization by Evolutionary Algorithms."  Proceedings of the
 *       International ICSC Symposium on Engineering of Intelligent Systems, 
 *       pp. 266-271.
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Jimenez extends AbstractProblem implements AnalyticalProblem {

	/**
	 * Constructs the Jimenez problem.
	 */
	public Jimenez() {
		super(2, 2, 4);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		double y = EncodingUtils.getReal(solution.getVariable(1));
		double f1 = 5.0*x + 3.0*y;
		double f2 = 2.0*x + 8.0*y;
		double c1 = x + 4.0*y - 100.0;
		double c2 = 3.0*x + 2.0*y - 150.0;
		double c3 = 200.0 - 5.0*x - 3.0*y;
		double c4 = 75.0 - 2.0*x - 8.0*y;
		
		solution.setObjective(0, -f1);
		solution.setObjective(1, -f2);
		solution.setConstraint(0, c1 <= 0.0 ? 0.0 : c1);
		solution.setConstraint(1, c2 <= 0.0 ? 0.0 : c2);
		solution.setConstraint(2, c3 <= 0.0 ? 0.0 : c3);
		solution.setConstraint(3, c4 <= 0.0 ? 0.0 : c4);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2, 4);
		
		solution.setVariable(0, EncodingUtils.newReal(0.0, 50.0));
		solution.setVariable(1, EncodingUtils.newReal(0.0, 50.0));
		
		return solution;
	}

	@Override
	public Solution generate() {
		Solution solution = newSolution();
		double p = PRNG.nextDouble(0.0, 1.0);
		
		EncodingUtils.setReal(solution.getVariable(0), 40.0 + 10.0*p);
		EncodingUtils.setReal(solution.getVariable(1), 15.0 - 15.0*p);
		
		evaluate(solution);
		return solution;
	}

}
