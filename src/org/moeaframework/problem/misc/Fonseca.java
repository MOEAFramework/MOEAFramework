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
 * The Fonseca problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected Pareto set
 *   <li>Concave Pareto front
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Fonseca, C. M. and Fleming, P. J. (1995).  "An Overview of Evolutioary
 *       Algorithms in Multiobjective Optimization."  Evolutionary Computation,
 *       vol. 3, no. 1, pp. 1-16.
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Fonseca extends AbstractProblem implements AnalyticalProblem {

	/**
	 * Constructs the Fonseca problem.
	 */
	public Fonseca() {
		super(2, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		double y = EncodingUtils.getReal(solution.getVariable(1));
		double f1 = 1.0 - Math.exp(-Math.pow(x-1.0, 2.0) - 
				Math.pow(y+1.0, 2.0));
		double f2 = 1.0 - Math.exp(-Math.pow(x+1.0, 2.0) - 
				Math.pow(y-1.0, 2.0));
		
		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2);
		
		solution.setVariable(0, EncodingUtils.newReal(-4.0, 4.0));
		solution.setVariable(1, EncodingUtils.newReal(-4.0, 4.0));
		
		return solution;
	}

	@Override
	public Solution generate() {
		Solution solution = newSolution();
		double x = PRNG.nextDouble(-1.0, 1.0);
		
		EncodingUtils.setReal(solution.getVariable(0), x);
		EncodingUtils.setReal(solution.getVariable(1), -x);
		
		evaluate(solution);
		return solution;
	}

}
