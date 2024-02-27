/* Copyright 2009-2024 David Hadka
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
package org.moeaframework.examples.single;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Single-objective Rosenbrock problem.  The optimum is located at {@code = (1, 1)} with an objective value of
 * {@code f(x) = 0.0}.
 */
public class RosenbrockProblem extends AbstractProblem {
	
	public RosenbrockProblem() {
		super(2, 1, 0);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		double y = EncodingUtils.getReal(solution.getVariable(1));
		
		solution.setObjective(0, 100*(y - x*x)*(y - x*x) + (1 - x)*(1 - x));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 1, 0);
		solution.setVariable(0, EncodingUtils.newReal(-10, 10));
		solution.setVariable(1, EncodingUtils.newReal(-10, 10));
		return solution;
	}

}