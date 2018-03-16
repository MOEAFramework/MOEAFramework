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
 * The Osyczka problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Disconnected Pareto set
 *   <li>Convex Pareto front
 *   <li>Constrained
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Osyczka, A. and Kundu, S. (1995).  "A New Method to Solve Generalized
 *       Multicriteria Optimization Problems using the Simple Genetic
 *       Algorithm."  Structural Optimization, vol. 10, pp. 94-99.
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Osyczka extends AbstractProblem {

	/**
	 * Constructs the Osyczka problem.
	 */
	public Osyczka() {
		super(2, 2, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		double f1 = x + Math.pow(y, 2.0);
		double f2 = Math.pow(x, 2.0) + y;
		double c1 = 12.0 - x - y;
		double c2 = Math.pow(x, 2.0) + 10.0*x - Math.pow(y, 2.0) + 16.0*y - 
				80.0;
		
		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
		solution.setConstraint(0, c1 >= 0.0 ? 0.0 : c1);
		solution.setConstraint(1, c2 >= 0.0 ? 0.0 : c2);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2, 2);
		
		solution.setVariable(0, new RealVariable(2.0, 7.0));
		solution.setVariable(1, new RealVariable(5.0, 10.0));
		
		return solution;
	}

}
