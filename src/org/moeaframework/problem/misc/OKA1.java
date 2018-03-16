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
 * Tatsuya Okabe's OKA1 test problem. The probability density of points becomes
 * more sparse the closer a population gets to the Pareto front.
 * <p>
 * References:
 * <ol>
 * <li>Okabe, T., et al. "On Test Functions for Evolutionary Multi-Objective
 * Optimization." Parallel Problem Solving from Nature, pp. 792-802, 2004.
 * </ol>
 */
public class OKA1 extends AbstractProblem {

	/**
	 * Constructs the OKA1 problem.
	 */
	public OKA1() {
		super(2, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double[] x = EncodingUtils.getReal(solution);
		double x1 = Math.cos(Math.PI / 12.0) * x[0] - Math.sin(Math.PI / 12.0)
				* x[1];
		double x2 = Math.sin(Math.PI / 12.0) * x[0] + Math.cos(Math.PI / 12.0)
				* x[1];

		solution.setObjective(0, x1);
		solution.setObjective(1, Math.sqrt(2.0 * Math.PI)
				- Math.sqrt(Math.abs(x1)) + 2.0
				* Math.pow(Math.abs(x2 - 3.0 * Math.cos(x1) - 3), 1.0 / 3.0));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2);

		solution.setVariable(0, new RealVariable(
				6.0 * Math.sin(Math.PI / 12.0), 6.0 * Math.sin(Math.PI / 12.0)
						+ 2.0 * Math.PI * Math.cos(Math.PI / 12.0)));
		solution.setVariable(1, new RealVariable(-2.0 * Math.PI
				* Math.sin(Math.PI / 12), 6.0 * Math.cos(Math.PI / 12.0)));

		return solution;
	}

}
