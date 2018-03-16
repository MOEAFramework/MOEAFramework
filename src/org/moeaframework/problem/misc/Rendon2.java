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
 * The Rendon (2) problem.  The optimum is defined by {@code (x, -3)} where
 * {@code -3 <= x <= 0}.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected Pareto set
 *   <li>Convex Pareto front
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Valenzuela-Rendon, M. and Uresti-Charre, E. (1997).  "A
 *       Non-Generational Genetic Algorithm for Multiobjective Optimization."
 *       Proceedings of the Seventh International Conference on Genetic
 *       Algorithms, pp. 658-665.
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Rendon2 extends AbstractProblem implements AnalyticalProblem {

	/**
	 * Constructs the Rendon (2) problem.
	 */
	public Rendon2() {
		super(2, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		double y = EncodingUtils.getReal(solution.getVariable(1));
		double f1 = x + y + 1.0;
		double f2 = Math.pow(x,  2.0) + 2.0*y - 1.0;
		
		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2);
		
		solution.setVariable(0, EncodingUtils.newReal(-3.0, 3.0));
		solution.setVariable(1, EncodingUtils.newReal(-3.0, 3.0));
		
		return solution;
	}

	@Override
	public Solution generate() {
		Solution solution = newSolution();
		
		EncodingUtils.setReal(solution.getVariable(0),
				PRNG.nextDouble(-3.0, 0.0));
		EncodingUtils.setReal(solution.getVariable(1), -3.0);
		
		evaluate(solution);
		return solution;
	}

}
