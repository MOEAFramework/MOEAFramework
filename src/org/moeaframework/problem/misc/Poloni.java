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
 * The Poloni problem.  Van Veldhuizen observed a typo in the original paper;
 * this implementation uses Van Veldhuizen's version of the problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Disconnected Pareto set
 *   <li>Disconnected and convex Pareto front
 *   <li>Maximization (objectives are negated)
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 *   <li>Poloni, C., et al. (1996).  "Multiobjective Optimization by GAs:
 *       Application to System and Component Design."  Computational Methods in
 *       Applied Sciences '96: Invited Lectures and Special Technological
 *       Sessions of the Third ECCOMAS Computational Fluid Dynamics Conference
 *       and the Second ECCOMAS Conference on Numerical Methods in Engineering,
 *       pp. 258-264.
 * </ol>
 */
public class Poloni extends AbstractProblem {

	/**
	 * Constructs the Poloni problem.
	 */
	public Poloni() {
		super(2, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		double A1 = 0.5*Math.sin(1.0) - 2.0*Math.cos(1.0) + Math.sin(2.0) - 
				1.5*Math.cos(2.0);
		double A2 = 1.5*Math.sin(1.0) - Math.cos(1.0) + 2.0*Math.sin(2.0) -
				0.5*Math.cos(2.0);
		double B1 = 0.5*Math.sin(x) - 2.0*Math.cos(x) + Math.sin(y) -
				1.5*Math.cos(y);
		double B2 = 1.5*Math.sin(x) - Math.cos(x) + 2.0*Math.sin(y) -
				0.5*Math.cos(y);
		double f1 = 1 + Math.pow(A1 - B1, 2.0) + Math.pow(A2 - B2, 2.0);
		double f2 = Math.pow(x + 3.0, 2.0) + Math.pow(y + 1.0, 2.0);
		
		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2);
		
		solution.setVariable(0, new RealVariable(-Math.PI, Math.PI));
		solution.setVariable(1, new RealVariable(-Math.PI, Math.PI));
		
		return solution;
	}

}
