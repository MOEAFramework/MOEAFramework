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
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * Tatsuya Okabe's OKA2 test problem. The probability density of points becomes
 * more sparse the closer a population gets to the Pareto front.
 * <p>
 * References:
 * <ol>
 * <li>Okabe, T., et al. "On Test Functions for Evolutionary Multi-Objective
 * Optimization." Parallel Problem Solving from Nature, pp. 792-802, 2004.
 * </ol>
 */
public class OKA2 extends AbstractProblem {

	/**
	 * Constructs the OKA2 problem.
	 */
	public OKA2() {
		super(3, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);

		solution.setObjective(0, x[0]);
		solution.setObjective(1, 1.0 - 1.0 / (4.0 * Math.pow(Math.PI, 2.0))
				* Math.pow(x[0] + Math.PI, 2.0)
				+ Math.pow(Math.abs(x[1] - 5.0 * Math.cos(x[0])), 1.0 / 3.0)
				+ Math.pow(Math.abs(x[2] - 5.0 * Math.sin(x[0])), 1.0 / 3.0));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(3, 2);

		solution.setVariable(0, new RealVariable(-Math.PI, Math.PI));
		solution.setVariable(1, new RealVariable(-5, 5));
		solution.setVariable(2, new RealVariable(-5, 5));

		return solution;
	}

}
