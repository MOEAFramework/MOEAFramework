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
 * The Schaffer (2) problem.  The Schaffer (2) problem is univariate, with the
 * optimum defined by {@code 1 <= x <= 2} and {@code 4 <= x <= 5}.
 * <p>
 * Properties:
 * <ul>
 *   <li>Disconnected Pareto set
 *   <li>Disconnected Pareto front
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Schaffer, J. D. (1984).  "Some Experiments in Machine Learning using
 *       Vector Evaluated Genetic Algorithms."  Ph.D. Thesis, Vanderbilt
 *       University, Nashville, USA.
 *   <li>Schaffer, J. D. (1985).  "Multiple Objective Optimization with Vector
 *       Evaluated Genetic Algorithms."  Genetic Algorithms and Their
 *       Applications: Proceedings of the First International Conference on
 *       Genetic Algorithms, pp. 93-100.
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Schaffer2 extends AbstractProblem implements AnalyticalProblem {

	/**
	 * Constructs the Schaffer (2) problem.
	 */
	public Schaffer2() {
		super(1, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		
		if (x <= 1.0) {
			solution.setObjective(0, -x);
		} else if (x <= 3.0) {
			solution.setObjective(0, -2.0 + x);
		} else if (x <= 4.0) {
			solution.setObjective(0, 4.0 - x);
		} else {
			solution.setObjective(0, -4.0 + x);
		}
		
		solution.setObjective(1, Math.pow(x - 5.0, 2.0));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 2);
		solution.setVariable(0, EncodingUtils.newReal(-5.0, 10.0));
		return solution;
	}

	@Override
	public Solution generate() {
		Solution solution = newSolution();
		
		if (PRNG.nextBoolean()) {
			EncodingUtils.setReal(solution.getVariable(0),
					PRNG.nextDouble(1.0, 2.0));
		} else {
			EncodingUtils.setReal(solution.getVariable(0),
					PRNG.nextDouble(4.0, 5.0));
		}
		
		evaluate(solution);
		return solution;
	}

}
