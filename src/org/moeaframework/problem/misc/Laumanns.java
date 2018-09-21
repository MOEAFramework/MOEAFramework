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
 * The Laumanns problem.  The optimum points like on the line {@code (x, 0)}
 * with {@code -2 <= x <= 0}.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected Pareto set
 *   <li>Disconnected Pareto front
 *   <li>Convex Pareto front
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Laumanns, M., Rudolph, G., and Schwefel, H. (1998).  "A Spatial
 *       Predator-Prey Approach to Multi-Objective Optimization: A Preliminary
 *       Study."  Proceedings of the Parallel Problem Solving from Nature,
 *       Springer, pp. 241-249.
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Laumanns extends AbstractProblem implements AnalyticalProblem {

	/**
	 * Constructs the Laumanns problem.
	 */
	public Laumanns() {
		super(2, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		double y = EncodingUtils.getReal(solution.getVariable(1));
		double f1 = Math.pow(x, 2.0) + Math.pow(y, 2.0);
		double f2 = Math.pow(x+2.0, 2.0) + Math.pow(y, 2.0);
		
		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2);
		
		solution.setVariable(0, EncodingUtils.newReal(-50.0, 50.0));
		solution.setVariable(1, EncodingUtils.newReal(-50.0, 50.0));
		
		return solution;
	}

	@Override
	public Solution generate() {
		Solution solution = newSolution();
		
		EncodingUtils.setReal(solution.getVariable(0),
				PRNG.nextDouble(-2.0, 0.0));
		EncodingUtils.setReal(solution.getVariable(1), 0.0);
		
		evaluate(solution);
		return solution;
	}

}
