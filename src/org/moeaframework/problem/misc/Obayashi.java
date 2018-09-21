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
 * The Obayashi problem.
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
 *   <li>Obayashi, S. (1998).  "Pareto Genetic Algorithm for Aerodynamic
 *       Design using the Navier-Stokes Equations."  Genetic Algorithms and
 *       Evolution Strategies in Engineering and Computer Science: Recent
 *       Advances and Industrial Applications, pp. 245-266.
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Obayashi extends AbstractProblem implements AnalyticalProblem {

	/**
	 * Constructs the Obayashi problem.
	 */
	public Obayashi() {
		super(2, 2, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		double y = EncodingUtils.getReal(solution.getVariable(1));
		double c = Math.pow(x, 2.0) + Math.pow(y, 2.0) - 1.0;
		
		solution.setObjective(0, -x);
		solution.setObjective(1, -y);
		solution.setConstraint(0, c <= 0.0 ? 0.0 : c);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2, 1);
		
		solution.setVariable(0, EncodingUtils.newReal(0.0, 1.0));
		solution.setVariable(1, EncodingUtils.newReal(0.0, 1.0));
		
		return solution;
	}

	@Override
	public Solution generate() {
		Solution solution = newSolution();
		double x = PRNG.nextDouble(0, 1);
		double y = Math.sqrt(1.0 - Math.pow(x, 2.0));
		
		EncodingUtils.setReal(solution.getVariable(0), x);
		EncodingUtils.setReal(solution.getVariable(1), y);
		
		evaluate(solution);
		return solution;
	}

}
