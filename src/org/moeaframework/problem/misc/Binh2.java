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
package org.moeaframework.problem.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.constraint.LessThanOrEqual;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Binh (2) problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected Pareto set
 *   <li>Convex Pareto front
 *   <li>Constrained
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Binh, T. T. and Korn, U (1997).  "MOBES: A Multiobjective Evolution Strategy for Constrained Optimization
 *       Problems."  Proceedings of the Third International Conference on Genetic Algorithms (Mendel 97), pp. 176-182.
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: Classifications, Analyses, and New
 *       Innovations."  Air Force Institute of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Binh2 extends AbstractProblem {

	/**
	 * Constructs the Binh (2) problem.
	 */
	public Binh2() {
		super(2, 2, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		double f1 = 4.0*Math.pow(x, 2.0) + 4.0*Math.pow(y, 2.0);
		double f2 = Math.pow(x - 5.0, 2.0) + Math.pow(y - 5.0, 2.0);
		double c1 = Math.pow(x - 5.0, 2.0) + Math.pow(y, 2.0) - 25.0;
		double c2 = -Math.pow(x - 8.0, 2.0) - Math.pow(y + 3.0, 2.0) + 7.7;
		
		solution.setObjectiveValue(0, f1);
		solution.setObjectiveValue(1, f2);
		solution.setConstraintValue(0, c1);
		solution.setConstraintValue(1, c2);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2, 2);
		
		solution.setVariable(0, new RealVariable(0.0, 5.0));
		solution.setVariable(1, new RealVariable(0.0, 3.0));
		
		solution.setConstraint(0, LessThanOrEqual.to(0.0));
		solution.setConstraint(1, LessThanOrEqual.to(0.0));
		
		return solution;
	}

}
