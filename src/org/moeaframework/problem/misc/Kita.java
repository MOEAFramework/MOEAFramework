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
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Kita problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Disconnected Pareto set
 *   <li>Disconnected and concave Pareto front
 *   <li>Constrained
 *   <li>Maximization (objectives are negated)
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Kita, H., et al. (1996).  "Multi-Objective Optimization by Means of
 *       the Thermodynamical Genetic Algorithm."  Parallel Problem Solving from
 *       Nature — PPSN IV, Lecture Notes in Computer Science, Springer,
 *       pp. 504-512.
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Kita extends AbstractProblem {

	/**
	 * Constructs the Kita problem.
	 */
	public Kita() {
		super(2, 2, 3);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		double f1 = -Math.pow(x,  2.0) + y;
		double f2 = 0.5*x + y + 1.0;
		double c1 = 1.0/6.0*x + y - 13.0/2.0;
		double c2 = 0.5*x + y - 15.0/2.0;
		double c3 = 5.0*x + y - 30.0;
		
		solution.setObjective(0, -f1);
		solution.setObjective(1, -f2);
		solution.setConstraint(0, c1 <= 0.0 ? 0.0 : c1);
		solution.setConstraint(1, c2 <= 0.0 ? 0.0 : c2);
		solution.setConstraint(2, c3 <= 0.0 ? 0.0 : c3);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2, 3);
		
		solution.setVariable(0, new RealVariable(0.0, 7.0));
		solution.setVariable(1, new RealVariable(0.0, 7.0));
		
		return solution;
	}

}
